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
package com.github.mrstampy.pprspray.core.streamer.event;

import com.github.mrstampy.pprspray.core.streamer.AbstractMediaStreamer;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

// TODO: Auto-generated Javadoc
/**
 * The Class MediaStreamerEventBus.
 */
public class MediaStreamerEventBus {

	private static final EventBus BUS = new EventBus("Media Streamer Event Bus");

	/**
	 * Used by {@link AbstractMediaStreamer} implementations to publish events.
	 *
	 * @param e the e
	 */
	public static void post(MediaStreamerEvent e) {
		BUS.post(e);
	}

	/**
	 * Objects are registering for notification of {@link MediaStreamerEvent}s and must
	 * implement a method with return of void, accepting a {@link MediaStreamerEvent}
	 * object as the only parameter, and annotated with {@link Subscribe}.
	 *
	 * @param subscriber the subscriber
	 */
	public static void register(Object subscriber) {
		BUS.register(subscriber);
	}

}
