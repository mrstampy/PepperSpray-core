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

import com.google.common.eventbus.Subscribe;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractNegotiationAckSubscriber.
 */
public abstract class AbstractNegotiationAckSubscriber {

	private int mediaHash;

	/**
	 * The Constructor.
	 *
	 * @param mediaHash
	 *          the media hash
	 */
	public AbstractNegotiationAckSubscriber(int mediaHash) {
		this.mediaHash = mediaHash;
	}

	/**
	 * Negotiation ack received.
	 *
	 * @param event
	 *          the event
	 */
	@Subscribe
	public void negotiationAckReceived(NegotiationAckChunk event) {
		if (!event.isApplicable(getMediaHash())) return;

		negotiationAckReceivedImpl(event);
	}

	/**
	 * Negotiation ack received impl.
	 *
	 * @param event
	 *          the event
	 */
	protected abstract void negotiationAckReceivedImpl(NegotiationAckChunk event);

	/**
	 * Gets the media hash.
	 *
	 * @return the media hash
	 */
	public int getMediaHash() {
		return mediaHash;
	}
}
