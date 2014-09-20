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
package com.github.mrstampy.pprspray.core.receiver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Scheduler;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import com.github.mrstampy.pprspray.core.receiver.event.ReceiverEvent;
import com.github.mrstampy.pprspray.core.receiver.event.ReceiverEventBus;
import com.github.mrstampy.pprspray.core.receiver.event.ReceiverEventType;
import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunk;
import com.github.mrstampy.pprspray.core.streamer.chunk.event.ChunkEventBus;
import com.github.mrstampy.pprspray.core.streamer.footer.MediaFooterChunk;
import com.google.common.eventbus.Subscribe;

/**
 * AbstractMediaReceivers aggregate {@link AbstractMediaChunk}s received on the
 * {@link ChunkEventBus} and on end of message apply any transformations
 * necessary to the chunks, creating a single byte array of the processed data
 * and sending a {@link MediaEvent} to the {@link MediaEventBus}.
 *
 * @param <AMC>
 *          the generic type
 */
public abstract class AbstractChunkReceiver<AMC extends AbstractMediaChunk> {
	private static final Logger log = LoggerFactory.getLogger(AbstractChunkReceiver.class);

	private MediaStreamType type;
	private int mediaHash;

	private MediaTransformer transformer;

	private AtomicBoolean open = new AtomicBoolean(false);

	/** The incoming. */
	protected Map<Integer, ConcurrentSkipListSet<AMC>> incoming = new ConcurrentHashMap<>();

	/** The svc. */
	protected Scheduler svc = Schedulers.from(Executors.newSingleThreadExecutor());

	private int finalizeAwaitValue = 0;
	private TimeUnit finalizeUnits = TimeUnit.SECONDS;

	/**
	 * The Constructor.
	 *
	 * @param type
	 *          the type
	 * @param mediaHash
	 *          the media hash
	 */
	protected AbstractChunkReceiver(MediaStreamType type, int mediaHash) {
		setType(type);
		setMediaHash(mediaHash);
		setTransformer(new NoTransformTransformer());

		ChunkEventBus.register(this);
	}

	/**
	 * Receive.
	 *
	 * @param chunk
	 *          the chunk
	 * @see ChunkEventBus#register(Object)
	 */
	@Subscribe
	public void receive(AMC chunk) {
		try {
			boolean applicable = isApplicable(chunk);

			if (!applicable) return;

			receiveImpl(chunk);
		} catch (Exception e) {
			log.error("Unexpected exception", e);
		}
	}

	/**
	 * Checks if is applicable.
	 *
	 * @param chunk
	 *          the chunk
	 * @return true, if checks if is applicable
	 */
	protected boolean isApplicable(AMC chunk) {
		return !(chunk instanceof MediaFooterChunk) && chunk.isApplicable(getType(), getMediaHash());
	}

	/**
	 * Implementation should invoke {@link #add(AbstractMediaChunk)}.
	 *
	 * @param chunk
	 *          the chunk
	 */
	protected abstract void receiveImpl(AMC chunk);

	/**
	 * Adds the chunk.
	 *
	 * @param chunk
	 *          the chunk
	 */
	protected void add(AMC chunk) {
		if (!isOpen()) open();
		ConcurrentSkipListSet<AMC> set = getSet(chunk.getMessageHash());
		log.trace("Adding sequence {} for message {}", chunk.getSequence(), chunk.getMessageHash());
		set.add(chunk);
	}

	private ConcurrentSkipListSet<AMC> getSet(int messageHash) {
		ConcurrentSkipListSet<AMC> set = incoming.get(messageHash);

		if (set == null) {
			set = new ConcurrentSkipListSet<>();
			incoming.put(messageHash, set);
		}

		return set;
	}

	/**
	 * End of message.
	 *
	 * @param eom
	 *          the eom
	 * @see ChunkEventBus#register(Object)
	 */
	@Subscribe
	public void endOfMessage(MediaFooterChunk eom) {
		if (!isApplicable(eom)) return;

		try {
			if (eom.isTerminateMessage(getMediaHash())) {
				log.debug("Received streamer termination for type {}, hash {}", getType(), getMediaHash());
				destroy();
			} else {
				log.trace("Finalizing for message {}", eom.getMessageHash());
				endOfMessageImpl(eom);
			}
		} catch (Exception e) {
			log.error("Unexpected exception", e);
		}
	}

	/**
	 * Checks if is applicable.
	 *
	 * @param eom
	 *          the eom
	 * @return true, if checks if is applicable
	 */
	protected boolean isApplicable(MediaFooterChunk eom) {
		return eom.isTerminateMessage(getMediaHash()) || eom.isApplicable(getType(), getMediaHash());
	}

	/**
	 * End of message impl.
	 *
	 * @param eom
	 *          the eom
	 */
	protected abstract void endOfMessageImpl(MediaFooterChunk eom);

	/**
	 * Finalize message.
	 *
	 * @param eom
	 *          the eom
	 */
	protected void finalizeMessage(final MediaFooterChunk eom) {
		if (!incoming.containsKey(eom.getMessageHash())) return;

		svc.createWorker().schedule(new Action0() {

			@Override
			public void call() {
				finalizeMessage(eom.getMessageHash());
			}
		}, getFinalizeAwaitValue(), getFinalizeUnits());
	}

	private void finalizeMessage(int messageHash) {
		ConcurrentSkipListSet<AMC> set = incoming.remove(messageHash);

		log.trace("Rehydrating {} for message hash {}", set.size(), messageHash);

		write(set);
	}

	/**
	 * Gets the empty array.
	 *
	 * @return the empty array
	 */
	protected abstract AMC[] getEmptyArray();

	/**
	 * Clear.
	 */
	public void clear() {
		incoming.clear();
	}

	/**
	 * Writes the {@link AbstractMediaChunk}s in the ordered set as a
	 * {@link MediaEvent} on the {@link MediaEventBus}. It is invoked indirectly
	 * when a {@link MediaFooterChunk} has been received.
	 *
	 * @param array
	 *          the array
	 */
	protected void write(Set<AMC> set) {
		try {
			byte[] b = rehydrateAndTransform(set);

			if (hasTransformed(b)) MediaEventBus.post(new MediaEvent(getType(), getMediaHash(), b));
		} catch (Exception e) {
			log.error("Unexpected exception, closing", e);
			close();
		}
	}

	private boolean hasTransformed(byte[] b) {
		return b != null && b.length > 0;
	}

	/**
	 * Rehydrate and transform.
	 *
	 * @param array
	 *          the array
	 * @return the byte[]
	 * @see #setTransformer(MediaTransformer)
	 */
	protected byte[] rehydrateAndTransform(Set<AMC> set) {
		int size = calcSize(set);
		ByteBuf buf = Unpooled.buffer(size);

		for (AMC chunk : set) {
			buf.writeBytes(chunk.getData());
		}

		return transform(buf.array());
	}

	private byte[] transform(byte[] rehydrated) {
		return getTransformer() == null ? rehydrated : getTransformer().transform(rehydrated, getMediaHash());
	}

	private int calcSize(Set<AMC> set) {
		int size = 0;
		for (AMC chunk : set) {
			size += chunk.getData().length;
		}

		return size;
	}

	/**
	 * Destroy.
	 * 
	 * @see ChunkEventBus#unregister(Object)
	 */
	public void destroy() {
		close();
		ChunkEventBus.unregister(this);
		notifyDestroy();
	}

	/**
	 * Open.
	 */
	public void open() {
		if (isOpen()) return;

		setOpen(true);
	}

	/**
	 * Close.
	 */
	public void close() {
		if (!isOpen()) return;

		setOpen(false);
	}

	/**
	 * Checks if is open.
	 *
	 * @return true, if checks if is open
	 */
	public boolean isOpen() {
		return open.get();
	}

	/**
	 * Sets the open.
	 *
	 * @param b
	 *          the open
	 */
	protected void setOpen(boolean b) {
		open.set(b);

		if (b) {
			notifyOpen();
		} else {
			notifyClose();
		}
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public MediaStreamType getType() {
		return type;
	}

	private void setType(MediaStreamType type) {
		this.type = type;
	}

	/**
	 * Notify close.
	 */
	protected void notifyClose() {
		notify(ReceiverEventType.CLOSE);
	}

	/**
	 * Notify open.
	 */
	protected void notifyOpen() {
		notify(ReceiverEventType.OPEN);
	}

	/**
	 * Notify destroy.
	 */
	protected void notifyDestroy() {
		notify(ReceiverEventType.DESTROY);
	}

	private void notify(ReceiverEventType type) {
		ReceiverEvent event = new ReceiverEvent(type, getMediaHash());

		ReceiverEventBus.post(event);
	}

	/**
	 * Gets the media hash.
	 *
	 * @return the media hash
	 */
	public int getMediaHash() {
		return mediaHash;
	}

	private void setMediaHash(int mediaHash) {
		this.mediaHash = mediaHash;
	}

	/**
	 * Gets the transformer.
	 *
	 * @return the transformer
	 */
	public MediaTransformer getTransformer() {
		return transformer;
	}

	/**
	 * Sets the transformer.
	 *
	 * @param transformer
	 *          the transformer
	 */
	public void setTransformer(MediaTransformer transformer) {
		this.transformer = transformer;
	}

	/**
	 * Gets the finalize await value.
	 *
	 * @return the finalize await value
	 */
	public int getFinalizeAwaitValue() {
		return finalizeAwaitValue;
	}

	/**
	 * Sets the value used to wait after a {@link MediaFooterChunk} has been
	 * received before posting the {@link MediaEvent} on the {@link MediaEventBus}
	 * . Defaults to zero seconds. Potentially useful with high latency
	 * connections to await all {@link AbstractMediaChunk}s before processing to a
	 * {@link MediaEvent}.
	 *
	 * @param finalizeAwaitValue
	 *          the finalize await value
	 */
	public void setFinalizeAwaitValue(int finalizeAwaitValue) {
		if (finalizeAwaitValue < 0) {
			throw new IllegalArgumentException("Await value must be > 0, was " + finalizeAwaitValue);
		}

		this.finalizeAwaitValue = finalizeAwaitValue;
	}

	/**
	 * Gets the finalize units.
	 *
	 * @return the finalize units
	 */
	public TimeUnit getFinalizeUnits() {
		return finalizeUnits;
	}

	/**
	 * Sets the units used to wait after a {@link MediaFooterChunk} has been
	 * received before posting the {@link MediaEvent} on the {@link MediaEventBus}
	 * . Defaults to zero seconds. Potentially useful with high latency
	 * connections to await all {@link AbstractMediaChunk}s before processing to a
	 * {@link MediaEvent}.
	 *
	 * @param finalizeUnits
	 *          the finalize units
	 */
	public void setFinalizeUnits(TimeUnit finalizeUnits) {
		if (finalizeUnits == null) throw new IllegalArgumentException("Finalize units cannot be null");
		this.finalizeUnits = finalizeUnits;
	}

}
