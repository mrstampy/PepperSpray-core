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
package com.github.mrstampy.pprspray.core.receiver.negotiation;

import com.github.mrstampy.pprspray.core.receiver.AbstractMediaReceiver;
import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.chunk.event.ChunkEventBus;
import com.github.mrstampy.pprspray.core.streamer.footer.MediaFooterMessage;
import com.github.mrstampy.pprspray.core.streamer.negotiation.NegotiationChunk;
import com.github.mrstampy.pprspray.core.streamer.negotiation.NegotiationEventBus;
import com.google.common.eventbus.Subscribe;

// TODO: Auto-generated Javadoc
/**
 * The Class NegotiationReceiver.
 */
public class NegotiationReceiver extends AbstractMediaReceiver<NegotiationChunk> {

	/** The Constant NEGOTIATION_HASH. */
	public static final int NEGOTIATION_HASH = -1;
	private static final NegotiationChunk[] MT = new NegotiationChunk[] {};

	/**
	 * The Constructor.
	 */
	public NegotiationReceiver() {
		super(MediaStreamType.NEGOTIATION, NEGOTIATION_HASH);
	}

	/**
	 * Receive.
	 *
	 * @param chunk
	 *          the chunk
	 * @see ChunkEventBus#register(Object)
	 */
	@Subscribe
	public void receive(NegotiationChunk chunk) {
		receiveImpl(chunk);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.receiver.AbstractMediaReceiver#receiveImpl
	 * (com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunk)
	 */
	@Override
	protected void receiveImpl(NegotiationChunk chunk) {
		NegotiationEventBus.post(chunk);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.receiver.AbstractMediaReceiver#
	 * endOfMessageImpl
	 * (com.github.mrstampy.pprspray.core.streamer.footer.MediaFooterMessage)
	 */
	@Override
	protected void endOfMessageImpl(MediaFooterMessage eom) {
		// N/A
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.receiver.AbstractMediaReceiver#getEmptyArray
	 * ()
	 */
	@Override
	protected NegotiationChunk[] getEmptyArray() {
		return MT;
	}

}
