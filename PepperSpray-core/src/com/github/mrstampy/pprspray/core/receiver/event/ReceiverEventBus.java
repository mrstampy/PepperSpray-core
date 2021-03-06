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
package com.github.mrstampy.pprspray.core.receiver.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.pprspray.core.receiver.AbstractChunkReceiver;
import com.google.common.eventbus.EventBus;

/**
 * Register to receive notification of changes to the state of
 * {@link AbstractChunkReceiver}s.
 */
public class ReceiverEventBus {
	private static final Logger log = LoggerFactory.getLogger(ReceiverEventBus.class);

	private static final EventBus BUS = new EventBus("Media Receiver Event Bus");

	/**
	 * Post, invoked when an {@link AbstractChunkReceiver} changes state.
	 *
	 * @param event
	 *          the event
	 */
	public static void post(ReceiverEvent event) {
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
			log.debug("{} is not registered on the receiver event bus", o, e);
		}
	}
}
