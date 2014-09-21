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

import com.github.mrstampy.pprspray.core.handler.NegotiationHandler;
import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.google.common.eventbus.EventBus;

/**
 * Classes interested in negotiating {@link MediaStreamType} connections will
 * register themselves on this bus and respond to {@link NegotiationEvent}s as
 * they are generated.
 * 
 * @see NegotiationHandler
 * @see AbstractNegotiationSubscriber
 * @see AcceptingNegotationSubscriber
 */
public class NegotiationEventBus {
	private static final Logger log = LoggerFactory.getLogger(NegotiationEventBus.class);

	private static final EventBus BUS = new EventBus("Negotiation Event Bus");

	/**
	 * Post.
	 *
	 * @param event
	 *          the event
	 */
	public static void post(NegotiationChunk event) {
		BUS.post(event);
	}

	/**
	 * Post.
	 *
	 * @param event
	 *          the event
	 */
	public static void post(NegotiationEvent event) {
		BUS.post(event);
	}

	/**
	 * Register.
	 *
	 * @param o
	 *          the o
	 */
	public static void register(Object o) {
		BUS.register(o);
	}

	/**
	 * Unregister.
	 *
	 * @param o
	 *          the o
	 */
	public static void unregister(Object o) {
		try {
			BUS.unregister(o);
		} catch (Exception e) {
			log.debug("{} is not registered on the negotiation event bus", o, e);
		}
	}

	private NegotiationEventBus() {
	}

}
