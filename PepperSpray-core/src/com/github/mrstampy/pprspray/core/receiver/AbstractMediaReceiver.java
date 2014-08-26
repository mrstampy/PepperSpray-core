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

	/**
	 * Sets the media hash.
	 *
	 * @param mediaHash
	 *          the media hash
	 */
	public void setMediaHash(int mediaHash) {
		this.mediaHash = mediaHash;
	}
}
