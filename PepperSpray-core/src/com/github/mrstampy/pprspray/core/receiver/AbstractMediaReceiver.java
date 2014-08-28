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

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
import com.github.mrstampy.pprspray.core.streamer.footer.MediaFooterMessage;
import com.google.common.eventbus.Subscribe;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractMediaReceiver.
 *
 * @param <AMC>
 *          the generic type
 */
public abstract class AbstractMediaReceiver<AMC extends AbstractMediaChunk> {
	private static final Logger log = LoggerFactory.getLogger(AbstractMediaReceiver.class);

	private MediaStreamType type;
	private int mediaHash;

	private MediaTransformer transformer;

	private AtomicBoolean open = new AtomicBoolean(false);

	/** The chunks. */
	protected ConcurrentSkipListSet<AMC> chunks = new ConcurrentSkipListSet<>();

	/** The svc. */
	protected Scheduler svc = Schedulers.from(Executors.newSingleThreadExecutor());

	private Lock lock = new ReentrantLock();

	/**
	 * The Constructor.
	 *
	 * @param type
	 *          the type
	 * @param mediaHash
	 *          the media hash
	 */
	protected AbstractMediaReceiver(MediaStreamType type, int mediaHash) {
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
		if (!chunk.isApplicable(getMediaHash())) return;

		receiveImpl(chunk);
	}

	/**
	 * Receive impl.
	 *
	 * @param chunk
	 *          the chunk
	 */
	protected abstract void receiveImpl(AMC chunk);

	/**
	 * Adds the.
	 *
	 * @param chunk
	 *          the chunk
	 */
	protected void add(AMC chunk) {
		lock.lock();
		try {
			if (!isOpen()) open();
			chunks.add(chunk);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * End of message.
	 *
	 * @param eom
	 *          the eom
	 * @see ChunkEventBus#register(Object)
	 */
	@Subscribe
	public void endOfMessage(MediaFooterMessage eom) {
		if (!isApplicable(eom)) return;

		if (isTerminateMessage(eom)) {
			destroy();
		} else {
			endOfMessageImpl(eom);
		}
	}

	/**
	 * Checks if is applicable.
	 *
	 * @param eom
	 *          the eom
	 * @return true, if checks if is applicable
	 */
	protected boolean isApplicable(MediaFooterMessage eom) {
		return isTerminateMessage(eom) || eom.isApplicable(getType(), getMediaHash());
	}

	/**
	 * Checks if is terminate message.
	 *
	 * @param eom
	 *          the eom
	 * @return true, if checks if is terminate message
	 */
	protected boolean isTerminateMessage(MediaFooterMessage eom) {
		return eom.isApplicable(MediaStreamType.NEGOTIATION, getMediaHash());
	}

	/**
	 * End of message impl.
	 *
	 * @param eom
	 *          the eom
	 */
	protected abstract void endOfMessageImpl(MediaFooterMessage eom);

	/**
	 * Finalize message.
	 */
	protected void finalizeMessage() {
		if (chunks.isEmpty()) return;

		final AMC[] array = getCurrentAndClear();

		svc.createWorker().schedule(new Action0() {

			@Override
			public void call() {
				write(array);
			}
		});

	}

	private AMC[] getCurrentAndClear() {
		lock.lock();
		try {
			AMC[] array = chunks.toArray(getEmptyArray());
			clear();
			return array;
		} finally {
			lock.unlock();
		}
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
		chunks.clear();
	}

	/**
	 * Write.
	 *
	 * @param array
	 *          the array
	 */
	protected void write(AMC[] array) {
		try {
			byte[] b = rehydrateAndTransform(array);

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
	protected byte[] rehydrateAndTransform(AMC[] array) {
		int size = calcSize(array);
		ByteBuf buf = Unpooled.buffer(size);

		for (AMC chunk : array) {
			buf.writeBytes(chunk.getData());
		}

		return transform(buf.array());
	}

	private byte[] transform(byte[] rehydrated) {
		return getTransformer() == null ? rehydrated : getTransformer().transform(rehydrated, getMediaHash());
	}

	private int calcSize(AMC[] array) {
		int size = 0;
		for (AMC chunk : array) {
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
}
