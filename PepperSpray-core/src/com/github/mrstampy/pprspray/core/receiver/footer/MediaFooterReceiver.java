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
package com.github.mrstampy.pprspray.core.receiver.footer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.pprspray.core.receiver.AbstractMediaReceiver;
import com.github.mrstampy.pprspray.core.streamer.chunk.event.ChunkEventBus;
import com.github.mrstampy.pprspray.core.streamer.footer.MediaFooterChunk;
import com.google.common.eventbus.Subscribe;

// TODO: Auto-generated Javadoc
/**
 * The Class MediaFooterReceiver.
 */
public class MediaFooterReceiver extends AbstractMediaReceiver<MediaFooterChunk> {
	private static final Logger log = LoggerFactory.getLogger(MediaFooterReceiver.class);

	private static final MediaFooterChunk[] MT = new MediaFooterChunk[] {};

	/**
	 * The Constructor.
	 */
	public MediaFooterReceiver() {
		super(null, -1);
	}

	/**
	 * Receive.
	 *
	 * @param chunk
	 *          the chunk
	 * @see ChunkEventBus#register(Object)
	 */
	@Subscribe
	public void receive(MediaFooterChunk chunk) {
		try {
			receiveImpl(chunk);
		} catch (Exception e) {
			log.error("Unexpected exception", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.receiver.AbstractMediaReceiver#receiveImpl
	 * (com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunk)
	 */
	@Override
	protected void receiveImpl(MediaFooterChunk chunk) {
		ChunkEventBus.post(chunk);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.receiver.AbstractMediaReceiver#
	 * endOfMessageImpl
	 * (com.github.mrstampy.pprspray.core.streamer.footer.MediaFooterChunk)
	 */
	@Override
	protected void endOfMessageImpl(MediaFooterChunk eom) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.receiver.AbstractMediaReceiver#getEmptyArray
	 * ()
	 */
	@Override
	protected MediaFooterChunk[] getEmptyArray() {
		return MT;
	}

}
