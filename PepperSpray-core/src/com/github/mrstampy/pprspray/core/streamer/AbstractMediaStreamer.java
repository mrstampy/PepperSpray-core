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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;
import com.github.mrstampy.kitchensync.stream.ByteArrayStreamer;
import com.github.mrstampy.kitchensync.stream.footer.Footer;
import com.github.mrstampy.pprspray.core.handler.NegotiationAckHandler;
import com.github.mrstampy.pprspray.core.handler.NegotiationHandler;
import com.github.mrstampy.pprspray.core.receiver.negotiation.NegotiationAckReceiver;
import com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunkProcessor;
import com.github.mrstampy.pprspray.core.streamer.chunk.event.ChunkEventBus;
import com.github.mrstampy.pprspray.core.streamer.event.MediaStreamerEvent;
import com.github.mrstampy.pprspray.core.streamer.event.MediaStreamerEventBus;
import com.github.mrstampy.pprspray.core.streamer.event.MediaStreamerEventType;
import com.github.mrstampy.pprspray.core.streamer.footer.MediaFooterChunk;
import com.github.mrstampy.pprspray.core.streamer.negotiation.AbstractNegotiationSubscriber;
import com.github.mrstampy.pprspray.core.streamer.negotiation.AcceptingNegotationSubscriber;
import com.github.mrstampy.pprspray.core.streamer.negotiation.NegotiationAckChunk;
import com.github.mrstampy.pprspray.core.streamer.negotiation.NegotiationChunk;
import com.github.mrstampy.pprspray.core.streamer.negotiation.NegotiationEventBus;
import com.github.mrstampy.pprspray.core.streamer.negotiation.NegotiationMessageUtils;
import com.github.mrstampy.pprspray.core.streamer.util.MediaStreamerUtils;
import com.google.common.eventbus.Subscribe;

// TODO: Auto-generated Javadoc
/**
 * AbstractMediaStreamer contains common methods and properties for the creation
 * of media streamers. Invocations of connect() will, if
 * {@link #isAutoNegotiate()}, negotiate with the remote site using a unique
 * identifier and will start streaming upon confirmation. Manual negotiations
 * will require a setting of {@link #setNotifyAccepted(boolean)} before
 * streaming can start.
 * 
 * @see MediaStreamType
 */
public abstract class AbstractMediaStreamer {
	private static final Logger log = LoggerFactory.getLogger(AbstractMediaStreamer.class);

	private static final AtomicInteger ID = new AtomicInteger(0);

	private AtomicBoolean streaming = new AtomicBoolean(false);

	/** The notifying. */
	protected AtomicBoolean notifying = new AtomicBoolean(false);

	/** The notify accepted. */
	protected AtomicBoolean notifyAccepted = new AtomicBoolean(false);

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
	private int concurrentThreads = 2;

	private String description;

	private KiSyChannel channel;
	private InetSocketAddress destination;

	private ByteArrayStreamer streamer;

	private MediaStreamType type;

	private boolean autoNegotiate = true;

	/**
	 * The Constructor.
	 *
	 * @param defaultPipeSize
	 *          the default pipe size
	 * @param channel
	 *          the channel
	 * @param destination
	 *          the destination
	 * @param type
	 *          the type
	 */
	protected AbstractMediaStreamer(int defaultPipeSize, KiSyChannel channel, InetSocketAddress destination,
			MediaStreamType type) {
		setStreamerPipeSize(defaultPipeSize);
		this.channel = channel;
		this.destination = destination;
		this.type = type;

		initStreamer();

		ChunkEventBus.register(this);

		addChannelCloseListener();
	}

	/**
	 * End of message.
	 *
	 * @param eom
	 *          the eom
	 * @see ChunkEventBus#register(Object)
	 */
	@Subscribe
	public void terminate(MediaFooterChunk eom) {
		if (!eom.isTerminateMessage(getMediaHash())) return;

		log.debug("Received receiver termination for type {}, hash {} from {}", getType(), getMediaHash(), getDestination());

		try {
			destroyImpl();
		} catch (Exception e) {
			log.error("Unexpected exception", e);
		}
	}

	private void initStreamer() {
		try {
			streamer = createStreamer();
		} catch (Exception e) {
			log.error("Unexpected exception", e);
			throw new IllegalStateException("Cannot initialize streamer", e);
		}
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
		MediaStreamerUtils.sendTerminationEvent(getMediaHash(), getChannel(), getDestination());
		destroyImpl();
	}

	/**
	 * Destroy impl.
	 */
	protected void destroyImpl() {
		if (isStreaming()) stop();

		streamer.cancel();
		unregisterForChunks();
		notifyDestroyed();
	}

	/**
	 * If the connection has not been negotiated {@link #negotiate()} will be
	 * invoked, else {@link #start()}.
	 */
	public void connect() {
		if (notifying()) return;
		if (isStreaming()) return;

		if (notifyAccepted()) {
			start();
		} else if (isAutoNegotiate()) {
			negotiate();
		} else {
			throw new IllegalStateException("Cannot connect media streamer, notifyAccepted() is false");
		}
	}

	/**
	 * Start.
	 */
	protected void start() {
		streaming.set(true);
		if (!streamer.isStreaming()) streamer.stream();
		notifyStart();

		sub = scheduler.createWorker().schedule(new Action0() {

			@Override
			public void call() {
				try {
					while (isStreaming()) {
						stream();
					}
				} finally {
					unsubscribe();
				}
			}
		});
	}

	/**
	 * Returns true if this media streamer is awaiting negotiation confirmation.
	 *
	 * @return true, if notifying
	 * @see NegotiationEventBus
	 * @see NegotiationChunk
	 * @see NegotiationAckChunk
	 * @see NegotiationAckReceiver
	 * @see NegotiationHandler
	 * @see NegotiationAckHandler
	 */
	public boolean notifying() {
		return notifying.get();
	}

	/**
	 * Returns true if this media streamer has received affirmative negotiation
	 * confirmation.
	 *
	 * @return true, if notify accepted
	 * @see NegotiationEventBus
	 * @see NegotiationChunk
	 * @see NegotiationAckChunk
	 * @see NegotiationAckReceiver
	 * @see NegotiationHandler
	 * @see NegotiationAckHandler
	 */
	public boolean notifyAccepted() {
		return notifyAccepted.get();
	}

	/**
	 * To be set manually when {@link #isAutoNegotiate()} is false, prior to
	 * calling {@link #connect()}.
	 *
	 * @param accepted
	 *          the notify accepted
	 */
	public void setNotifyAccepted(boolean accepted) {
		if (isAutoNegotiate()) log.warn("Auto negotiation is on.  Setting notify accepted to {}", accepted);

		notifyAccepted.set(accepted);
	}

	/**
	 * Sends a
	 * {@link NegotiationMessageUtils#getNegotiationMessage(int, MediaStreamType)}
	 * to the destinations and awaits acknowledgement. If affirmative
	 * {@link #start()} is invoked to commence streaming.
	 * 
	 * @see NegotiationEventBus
	 * @see NegotiationChunk
	 * @see NegotiationAckChunk
	 * @see NegotiationAckReceiver
	 * @see AbstractNegotiationSubscriber
	 * @see AcceptingNegotationSubscriber
	 */
	protected void negotiate() {
		log.debug("Negotiating with {} for media hash {}", getDestination(), getMediaHash());

		notifying.set(true);

		notifyNegotiating();

		ByteBuf buf = NegotiationMessageUtils.getNegotiationMessage(getMediaHash(), getType());

		ChunkEventBus.register(new AckReceiver(getMediaHash()));

		getChannel().send(buf.array(), getDestination());
	}

	/**
	 * Stop.
	 */
	public void stop() {
		unsubscribe();
		streamer.pause();
		notifyStop();
	}

	private ByteArrayStreamer createStreamer() throws Exception {
		ByteArrayStreamer bas = new ByteArrayStreamer(getChannel(), getDestination(), getStreamerPipeSize());

		bas.setEomOnFinish(true);
		bas.setProcessChunk(true);
		bas.setChunkProcessor(getMediaChunkProcessor());
		bas.setFooter(getMediaFooter());

		if (isAckRequired()) bas.ackRequired();
		if (getChunksPerSecond() > 0) bas.setChunksPerSecond(getChunksPerSecond());
		if (isFullThrottle()) bas.fullThrottle();

		bas.setThrottle(getThrottle());
		bas.setConcurrentThreads(getConcurrentThreads());

		notifyAdd();

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

	private void notifyAdd() {
		log.debug("Adding Media Streamer for channel {} and destination {}", getChannel().localAddress(), getDestination());
		MediaStreamerEventBus.post(new MediaStreamerEvent(this, MediaStreamerEventType.DESTINATION_ADDED));
	}

	private void notifyStart() {
		log.debug("Started streamer, type {}, hash {} for {}", getType(), getMediaHash(), getDestination());
		MediaStreamerEventBus.post(new MediaStreamerEvent(this, MediaStreamerEventType.STARTED));
	}

	private void notifyStop() {
		log.debug("Stopped streamer, type {}, hash {} for {}", getType(), getMediaHash(), getDestination());
		MediaStreamerEventBus.post(new MediaStreamerEvent(this, MediaStreamerEventType.STOPPED));
	}

	private void notifyDestroyed() {
		log.debug("Destroyed streamer, type {}, hash {} for {}", getType(), getMediaHash(), getDestination());
		MediaStreamerEventBus.post(new MediaStreamerEvent(this, MediaStreamerEventType.DESTROYED));
	}

	private void notifyNegotiationFailed() {
		MediaStreamerEventBus.post(new MediaStreamerEvent(this, MediaStreamerEventType.NEGOTIATION_FAILED));
	}

	private void notifyNegotiationSuccessful() {
		MediaStreamerEventBus.post(new MediaStreamerEvent(this, MediaStreamerEventType.NEGOTIATION_SUCCESSFUL));
	}

	private void notifyNegotiating() {
		MediaStreamerEventBus.post(new MediaStreamerEvent(this, MediaStreamerEventType.NEGOTIATING));
	}

	/**
	 * Stream.
	 */
	protected void stream() {
		try {
			byte[] data = getBytes();

			if (data == null) return;

			sendData(data);
		} catch (Exception e) {
			log.error("Unexpected exception streaming from {} to {}", streamer.getChannel().localAddress(),
					streamer.getDestination(), e);
		}
	}

	/**
	 * Send data.
	 *
	 * @param data
	 *          the data
	 * @throws Exception
	 *           the exception
	 */
	protected void sendData(byte[] data) throws Exception {
		ChannelFuture cf = streamer.stream(data);
		cf.await();
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
		streamer.setChunkProcessor(mediaChunkProcessor);
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
		streamer.setFooter(mediaFooter);
	}

	/**
	 * Sets the ack required.
	 *
	 * @param isAckRequired
	 *          the ack required
	 */
	public void setAckRequired(boolean isAckRequired) {
		this.ackRequired = isAckRequired;
		if (isAckRequired) streamer.ackRequired();
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
		streamer.setThrottle(throttle);
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
		streamer.setChunksPerSecond(chunksPerSecond);
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
		streamer.setConcurrentThreads(concurrentThreads);
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

		streamer.fullThrottle();
	}

	/**
	 * Gets the channel.
	 *
	 * @return the channel
	 */
	public KiSyChannel getChannel() {
		return channel;
	}

	/**
	 * Gets the destination.
	 *
	 * @return the destination
	 */
	public InetSocketAddress getDestination() {
		return destination;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public MediaStreamType getType() {
		return type;
	}

	/**
	 * If true then any invocation of {@link #connect()} when
	 * {@link #notifyAccepted()} is false will send a
	 * {@link NegotiationMessageUtils#getNegotiationMessage(int, MediaStreamType)}
	 * to the {@link #getDestination()} and await a successful
	 * {@link NegotiationAckChunk} response to begin streaming.<br>
	 * <br>
	 * 
	 * If false then the encapsulating application must negotiate the
	 * {@link #getMediaHash()} with the {@link #getDestination()} and
	 * {@link #setNotifyAccepted(boolean)} appropriately prior to calling
	 * {@link #connect()}.
	 *
	 * @return true, if checks if is auto negotiate
	 */
	public boolean isAutoNegotiate() {
		return autoNegotiate;
	}

	/**
	 * Sets the auto negotiate.
	 *
	 * @param autoNegotiate
	 *          the auto negotiate
	 */
	public void setAutoNegotiate(boolean autoNegotiate) {
		this.autoNegotiate = autoNegotiate;
	}

	private void addChannelCloseListener() {
		getChannel().getChannel().closeFuture().addListener(new GenericFutureListener<ChannelFuture>() {

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				destroyImpl();
			}
		});
	}

	private void unregisterForChunks() {
		ChunkEventBus.unregister(this);
	}

	private class AckReceiver extends NegotiationAckReceiver {

		public AckReceiver(int mediaHash) {
			super(mediaHash);
		}

		@Override
		protected void ackReceived(NegotiationAckChunk chunk) {
			notifying.set(false);

			notifyAccepted.set(chunk.isAccepted());

			if (notifyAccepted()) {
				log.debug("Negotiations with {} for type {}, media hash {} successful", getDestination(),
						AbstractMediaStreamer.this.getType(), getMediaHash());

				notifyNegotiationSuccessful();
				start();
			} else {
				log.debug("Negotiations with {} for type {}, media hash {} unsuccessful", getDestination(), getType(),
						getMediaHash());

				notifyNegotiationFailed();
			}
		}

	}

}
