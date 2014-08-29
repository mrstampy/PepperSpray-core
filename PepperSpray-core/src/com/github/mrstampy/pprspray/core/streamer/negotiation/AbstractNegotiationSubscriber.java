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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.pprspray.core.handler.NegotiationAckHandler;
import com.github.mrstampy.pprspray.core.handler.NegotiationHandler;
import com.github.mrstampy.pprspray.core.receiver.AbstractMediaReceiver;
import com.github.mrstampy.pprspray.core.receiver.MediaEventBus;
import com.github.mrstampy.pprspray.core.receiver.MediaProcessor;
import com.github.mrstampy.pprspray.core.streamer.AbstractMediaStreamer;
import com.github.mrstampy.pprspray.core.streamer.chunk.event.ChunkEventBus;
import com.google.common.eventbus.Subscribe;

/**
 * Subclasses when {@link #isRegistered()} on the {@link NegotiationEventBus}
 * determine how to respond to {@link NegotiationChunk}s - requests to begin
 * streaming media of the specified type. If the negotiation is affirmative an
 * appropriate {@link AbstractMediaReceiver} and {@link MediaProcessor} must be
 * created and registered on the corresponding event buses and a
 * {@link NegotiationAckChunk} message must be sent back to the requester.
 * 
 * @see ChunkEventBus
 * @see MediaEventBus
 * @see AbstractMediaStreamer#isAutoNegotiate()
 * @see NegotiationHandler
 * @see NegotiationAckHandler
 */
public abstract class AbstractNegotiationSubscriber {
	private static final Logger log = LoggerFactory.getLogger(AcceptingNegotationSubscriber.class);

	private boolean registered;

	/**
	 * The Constructor.
	 */
	protected AbstractNegotiationSubscriber() {
		register();
	}

	/**
	 * Checks if is registered.
	 *
	 * @return true, if checks if is registered
	 */
	public boolean isRegistered() {
		return registered;
	}

	/**
	 * Register.
	 */
	public void register() {
		if (isRegistered()) return;
		NegotiationEventBus.register(this);
		registered = true;
	}

	/**
	 * Unregister.
	 */
	public void unregister() {
		if (!isRegistered()) return;
		NegotiationEventBus.unregister(this);
		registered = false;
	}

	/**
	 * Negotiation requested.
	 *
	 * @param event
	 *          the event
	 */
	@Subscribe
	public final void negotiationRequested(NegotiationChunk event) {
		try {
			negotiationRequestedImpl(event);
		} catch (Exception e) {
			log.error("Unexpected exception", e);
		}
	}

	/**
	 * Negotiation requested impl.
	 *
	 * @param event
	 *          the event
	 */
	protected abstract void negotiationRequestedImpl(NegotiationChunk event);
}
