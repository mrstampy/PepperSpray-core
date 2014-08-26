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

	private MediaStreamType type;
	private int mediaHash;

	private MediaTransformer transformer = new NoTransformTransformer();

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
	 * End of message.
	 *
	 * @param eom
	 *          the eom
	 * @see ChunkEventBus#register(Object)
	 */
	@Subscribe
	public void endOfMessage(MediaFooterMessage eom) {
		if (!eom.isApplicable(getType(), getMediaHash())) return;

		endOfMessageImpl(eom);
	}

	/**
	 * End of message impl.
	 *
	 * @param eom
	 *          the eom
	 */
	protected abstract void endOfMessageImpl(MediaFooterMessage eom);

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
	}

	/**
	 * Close.
	 */
	public abstract void close();

	/**
	 * Open.
	 */
	public abstract void open();

	/**
	 * Checks if is open.
	 *
	 * @return true, if checks if is open
	 */
	public abstract boolean isOpen();

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
