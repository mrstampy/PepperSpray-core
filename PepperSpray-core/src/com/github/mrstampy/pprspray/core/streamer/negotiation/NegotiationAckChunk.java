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
package com.github.mrstampy.pprspray.core.streamer.negotiation;

import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunk;

/**
 * The Class NegotiationAckChunk represents an acknowledgement to a negotiation
 * request.
 * 
 * @see NegotiationChunk
 */
public class NegotiationAckChunk extends AbstractMediaChunk {

	private static final long serialVersionUID = -1778757023925120294L;

	/**
	 * The Constructor.
	 *
	 * @param message
	 *          the message
	 */
	public NegotiationAckChunk(byte[] message) {
		super(message, MediaStreamType.NEGOTIATION_ACK);
	}

	/**
	 * Checks if is accepted.
	 *
	 * @return true, if checks if is accepted
	 */
	public boolean isAccepted() {
		return getData()[0] == 1;
	}

}
