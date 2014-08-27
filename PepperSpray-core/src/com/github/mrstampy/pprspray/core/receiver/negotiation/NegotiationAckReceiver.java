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
import com.github.mrstampy.pprspray.core.streamer.footer.MediaFooterMessage;
import com.github.mrstampy.pprspray.core.streamer.negotiation.NegotiationAckChunk;
import com.github.mrstampy.pprspray.core.streamer.negotiation.NegotiationEventBus;

// TODO: Auto-generated Javadoc
/**
 * The Class NegotiationAckReceiver.
 */
public class NegotiationAckReceiver extends AbstractMediaReceiver<NegotiationAckChunk> {

	private static final NegotiationAckChunk[] MT = new NegotiationAckChunk[] {};

	/**
	 * The Constructor.
	 *
	 * @param mediaHash
	 *          the media hash
	 */
	public NegotiationAckReceiver(int mediaHash) {
		super(MediaStreamType.NEGOTIATION_ACK, mediaHash);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.receiver.AbstractMediaReceiver#receiveImpl
	 * (com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunk)
	 */
	@Override
	protected void receiveImpl(NegotiationAckChunk chunk) {
		NegotiationEventBus.post(chunk);
		destroy();
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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.receiver.AbstractMediaReceiver#getEmptyArray
	 * ()
	 */
	@Override
	protected NegotiationAckChunk[] getEmptyArray() {
		return MT;
	}

}
