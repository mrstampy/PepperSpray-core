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
 * The Class AbstractNegotiationSubscriber.
 */
public abstract class AbstractNegotiationSubscriber {

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
		negotiationRequestedImpl(event);
	}

	/**
	 * Negotiation requested impl.
	 *
	 * @param event
	 *          the event
	 */
	protected abstract void negotiationRequestedImpl(NegotiationChunk event);
}
