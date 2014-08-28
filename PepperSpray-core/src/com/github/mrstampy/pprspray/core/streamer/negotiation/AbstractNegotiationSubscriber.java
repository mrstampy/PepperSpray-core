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

import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;
import com.github.mrstampy.pprspray.core.streamer.util.MediaStreamerUtils;
import com.google.common.eventbus.Subscribe;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractNegotiationSubscriber.
 */
public abstract class AbstractNegotiationSubscriber {

	/**
	 * Negotiation requested.
	 *
	 * @param event
	 *          the event
	 */
	@Subscribe
	public void negotiationRequested(NegotiationChunk event) {
		negotiationRequestedImpl(event);
	}

	/**
	 * Gets the channel.
	 *
	 * @param event
	 *          the event
	 * @return the channel
	 */
	protected KiSyChannel getChannel(NegotiationChunk event) {
		KiSyChannel channel = MediaStreamerUtils.getChannel(event.getReceiver());

		if (channel == null) {
			throw new IllegalStateException("Cannot locate channel for port " + event.getChannelPort() + " or address "
					+ event.getReceiver());
		}

		return channel;
	}

	/**
	 * Negotiation requested impl.
	 *
	 * @param event
	 *          the event
	 */
	protected abstract void negotiationRequestedImpl(NegotiationChunk event);
}
