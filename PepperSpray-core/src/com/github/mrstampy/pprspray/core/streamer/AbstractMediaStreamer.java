/*
 * PepperSpray-core, Encrypted Secure Communications Library
 * 
 * Copyright (C) 2014 Burton Alexander
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 */
package com.github.mrstampy.pprspray.core.streamer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;
import com.github.mrstampy.kitchensync.stream.ByteArrayStreamer;
import com.github.mrstampy.kitchensync.stream.footer.Footer;
import com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunkProcessor;
import com.github.mrstampy.pprspray.core.streamer.event.MediaStreamerEvent;
import com.github.mrstampy.pprspray.core.streamer.event.MediaStreamerEventBus;
import com.github.mrstampy.pprspray.core.streamer.event.MediaStreamerEventType;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractMediaStreamer.
 */
public abstract class AbstractMediaStreamer {
	private static final Logger log = LoggerFactory.getLogger(AbstractMediaStreamer.class);

	private static final AtomicInteger ID = new AtomicInteger(0);

	private List<ByteArrayStreamer> streamers = new ArrayList<ByteArrayStreamer>();
	private List<ByteArrayStreamer> deadStreams = new ArrayList<ByteArrayStreamer>();

	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private ReadLock readLock = lock.readLock();
	private WriteLock writeLock = lock.writeLock();

	private AtomicBoolean streaming = new AtomicBoolean(false);

	private Scheduler scheduler = Schedulers.from(Executors.newSingleThreadExecutor());
	private Subscription sub;

	private AbstractMediaChunkProcessor mediaChunkProcessor;
	private Footer mediaFooter;

	private int id = -1;

	private boolean ackRequired;
	private boolean fullThrottle;

	private int throttle = 0;
	private int chunksPerSecond = -1;
	private int streamerPipeSize;
	private int concurrentThreads;

	private String description;

	/**
	 * The Constructor.
	 *
	 * @param defaultPipeSize
	 *          the default pipe size
	 */
	protected AbstractMediaStreamer(int defaultPipeSize) {
		setStreamerPipeSize(defaultPipeSize);
	}

	/**
	 * Utility method to return a unique id for a streamer, useful in quickly
	 * identifying streamers when receiving {@link MediaStreamerEvent}s.
	 *
	 * @return the id
	 * @see MediaStreamerEventBus
	 */
	public int getId() {
		if (id == -1) id = ID.incrementAndGet();

		return id;
	}

	/**
	 * Checks if is streaming.
	 *
	 * @return true, if checks if is streaming
	 */
	public boolean isStreaming() {
		return streaming.get();
	}

	/**
	 * Destroy.
	 */
	public void destroy() {
		if (isStreaming()) stop();

		remove(streamers.toArray(new ByteArrayStreamer[] {}));
		notifyDestroyed();
	}

	/**
	 * Start.
	 */
	public void start() {
		if (isStreaming()) return;
		if (!isStreamable()) throw new IllegalStateException("Media streamer cannot be opened");

		streaming.set(true);
		notifyStart();

		sub = scheduler.createWorker().schedule(new Action0() {

			@Override
			public void call() {
				try {
					while (isStreamable()) {
						stream();
					}
				} finally {
					unsubscribe();
				}
			}
		});
	}

	/**
	 * Stop.
	 */
	public void stop() {
		unsubscribe();
		notifyStop();
	}

	/**
	 * Adds the.
	 *
	 * @param channel
	 *          the channel
	 * @param destination
	 *          the destination
	 */
	public void add(KiSyChannel channel, InetSocketAddress destination) {
		if (contains(destination)) {
			log.warn("Destination {} already added", destination);
			return;
		}

		writeLock.lock();
		try {
			streamers.add(createStreamer(channel, destination));
		} catch (Exception e) {
			log.error("Unexpected exception", e);
			throw new IllegalStateException("Could not create streamer for destination " + destination);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Removes the.
	 *
	 * @param channel
	 *          the channel
	 * @param destination
	 *          the destination
	 */
	public void remove(KiSyChannel channel, InetSocketAddress destination) {
		ByteArrayStreamer bas = get(destination);
		if (bas == null) return;

		remove(bas);
	}

	private boolean contains(InetSocketAddress destination) {
		return get(destination) != null;
	}

	private ByteArrayStreamer get(InetSocketAddress destination) {
		readLock.lock();
		try {
			for (ByteArrayStreamer bas : streamers) {
				if (isStreamer(destination, bas)) return bas;
			}
		} finally {
			readLock.unlock();
		}

		return null;
	}

	private boolean isStreamer(InetSocketAddress destination, ByteArrayStreamer bas) {
		return bas.getDestination().equals(destination);
	}

	private ByteArrayStreamer createStreamer(KiSyChannel channel, InetSocketAddress destination) throws Exception {
		ByteArrayStreamer bas = new ByteArrayStreamer(channel, destination, getStreamerPipeSize());

		bas.setEomOnFinish(true);
		bas.setProcessChunk(true);
		bas.setChunkProcessor(getMediaChunkProcessor());
		bas.setFooter(getMediaFooter());

		if (isAckRequired()) bas.ackRequired();
		if (getChunksPerSecond() > 0) bas.setChunksPerSecond(getChunksPerSecond());
		if (isFullThrottle()) bas.fullThrottle();

		bas.setThrottle(getThrottle());
		bas.setConcurrentThreads(getConcurrentThreads());

		notifyAdd(channel, destination);

		return bas;
	}

	/**
	 * Checks if is ack required.
	 *
	 * @return true, if checks if is ack required
	 */
	public boolean isAckRequired() {
		return ackRequired;
	}

	private void notifyAdd(KiSyChannel channel, InetSocketAddress dest) {
		log.debug("Adding PhooCam streamer for channel {} and destination {}", channel.localAddress(), dest);
		MediaStreamerEventBus.post(new MediaStreamerEvent(this, MediaStreamerEventType.DESTINATION_ADDED, channel, dest));
	}

	private void notifyStart() {
		log.debug("Started");
		MediaStreamerEventBus.post(new MediaStreamerEvent(this, MediaStreamerEventType.STARTED));
	}

	private void notifyStop() {
		log.debug("Stopped");
		MediaStreamerEventBus.post(new MediaStreamerEvent(this, MediaStreamerEventType.STOPPED));
	}

	private void notifyDestroyed() {
		log.debug("Destroyed");
		MediaStreamerEventBus.post(new MediaStreamerEvent(this, MediaStreamerEventType.DESTROYED));
	}

	private void notifyRemoval(ByteArrayStreamer bas) {
		KiSyChannel channel = bas.getChannel();
		InetSocketAddress dest = bas.getDestination();

		log.debug("Removing PhooCam streamer for channel {} and destination {}", channel.localAddress(), dest);
		MediaStreamerEventBus.post(new MediaStreamerEvent(this, MediaStreamerEventType.DESTINATION_REMOVED, channel, dest));
	}

	/**
	 * Stream.
	 */
	protected void stream() {
		byte[] data = getBytes();

		if (data == null) return;

		readLock.lock();
		try {
			if (streamers.isEmpty()) stop();

			for (ByteArrayStreamer bas : streamers) {
				stream(data, bas);
			}
		} finally {
			readLock.unlock();
		}

		takeOutYourDead();
	}

	private void stream(byte[] bytes, ByteArrayStreamer bas) {
		try {
			bas.stream(bytes);
		} catch (Exception e) {
			log.error("Unexpected exception streaming from {} to {}", bas.getChannel().localAddress(), bas.getDestination(),
					e);
			deadStreams.add(bas);
		}
	}

	private void takeOutYourDead() {
		if (deadStreams.isEmpty()) return;

		ByteArrayStreamer[] bass = deadStreams.toArray(new ByteArrayStreamer[] {});
		deadStreams.clear();

		remove(bass);
	}

	private void remove(ByteArrayStreamer... bass) {
		writeLock.lock();
		try {
			for (ByteArrayStreamer bas : bass) {
				bas.cancel();
				notifyRemoval(bas);
				streamers.remove(bas);
			}
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Checks if is streamable.
	 *
	 * @return true, if checks if is streamable
	 */
	protected abstract boolean isStreamable();

	/**
	 * Gets the bytes.
	 *
	 * @return the bytes
	 */
	protected abstract byte[] getBytes();

	private void unsubscribe() {
		if (sub != null) sub.unsubscribe();
	}

	/**
	 * Gets the media chunk processor.
	 *
	 * @return the media chunk processor
	 */
	public AbstractMediaChunkProcessor getMediaChunkProcessor() {
		return mediaChunkProcessor;
	}

	/**
	 * Sets the media chunk processor.
	 *
	 * @param mediaChunkProcessor
	 *          the media chunk processor
	 */
	public void setMediaChunkProcessor(AbstractMediaChunkProcessor mediaChunkProcessor) {
		this.mediaChunkProcessor = mediaChunkProcessor;
		readLock.lock();
		try {
			for (ByteArrayStreamer streamer : streamers) {
				streamer.setChunkProcessor(mediaChunkProcessor);
			}
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * Gets the media hash.
	 *
	 * @return the media hash
	 */
	public int getMediaHash() {
		return getMediaChunkProcessor().getMediaHash();
	}

	/**
	 * Gets the media footer.
	 *
	 * @return the media footer
	 */
	public Footer getMediaFooter() {
		return mediaFooter;
	}

	/**
	 * Sets the media footer.
	 *
	 * @param mediaFooter
	 *          the media footer
	 */
	public void setMediaFooter(Footer mediaFooter) {
		this.mediaFooter = mediaFooter;
		readLock.lock();
		try {
			for (ByteArrayStreamer streamer : streamers) {
				streamer.setFooter(mediaFooter);
			}
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * Sets the ack required.
	 *
	 * @param isAckRequired
	 *          the ack required
	 */
	public void setAckRequired(boolean isAckRequired) {
		this.ackRequired = isAckRequired;
		if (isAckRequired) {
			readLock.lock();
			try {
				for (ByteArrayStreamer streamer : streamers) {
					streamer.ackRequired();
				}
			} finally {
				readLock.unlock();
			}
		}
	}

	/**
	 * Gets the throttle.
	 *
	 * @return the throttle
	 */
	public int getThrottle() {
		return throttle;
	}

	/**
	 * Sets the throttle.
	 *
	 * @param throttle
	 *          the throttle
	 */
	public void setThrottle(int throttle) {
		this.throttle = throttle;
		readLock.lock();
		try {
			for (ByteArrayStreamer streamer : streamers) {
				streamer.setThrottle(throttle);
			}
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * Gets the chunks per second.
	 *
	 * @return the chunks per second
	 */
	public int getChunksPerSecond() {
		return chunksPerSecond;
	}

	/**
	 * Sets the chunks per second.
	 *
	 * @param chunksPerSecond
	 *          the chunks per second
	 */
	public void setChunksPerSecond(int chunksPerSecond) {
		this.chunksPerSecond = chunksPerSecond;
		readLock.lock();
		try {
			for (ByteArrayStreamer streamer : streamers) {
				streamer.setChunksPerSecond(chunksPerSecond);
			}
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * Sets the streamer pipe size.
	 *
	 * @param streamerPipeSize
	 *          the streamer pipe size
	 */
	public void setStreamerPipeSize(int streamerPipeSize) {
		this.streamerPipeSize = streamerPipeSize;
	}

	/**
	 * Gets the streamer pipe size.
	 *
	 * @return the streamer pipe size
	 */
	public int getStreamerPipeSize() {
		return streamerPipeSize;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description
	 *          the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the concurrent threads.
	 *
	 * @return the concurrent threads
	 */
	public int getConcurrentThreads() {
		return concurrentThreads;
	}

	/**
	 * Sets the concurrent threads.
	 *
	 * @param concurrentThreads
	 *          the concurrent threads
	 */
	public void setConcurrentThreads(int concurrentThreads) {
		this.concurrentThreads = concurrentThreads;
		readLock.lock();
		try {
			for (ByteArrayStreamer streamer : streamers) {
				streamer.setConcurrentThreads(concurrentThreads);
			}
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * Checks if is full throttle.
	 *
	 * @return true, if checks if is full throttle
	 */
	public boolean isFullThrottle() {
		return fullThrottle;
	}

	/**
	 * Sets the full throttle.
	 *
	 * @param fullThrottle
	 *          the full throttle
	 */
	public void setFullThrottle(boolean fullThrottle) {
		this.fullThrottle = fullThrottle;
		if (!fullThrottle) return;

		readLock.lock();
		try {
			for (ByteArrayStreamer streamer : streamers) {
				streamer.fullThrottle();
			}
		} finally {
			readLock.unlock();
		}
	}

}
