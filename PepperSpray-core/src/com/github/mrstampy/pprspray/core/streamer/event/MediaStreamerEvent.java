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

import java.net.InetSocketAddress;

import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;
import com.github.mrstampy.pprspray.core.streamer.AbstractMediaStreamer;

// TODO: Auto-generated Javadoc
/**
 * The Class MediaStreamerEvent.
 */
public class MediaStreamerEvent {

	private AbstractMediaStreamer source;
	private MediaStreamerEventType type;
	private KiSyChannel channel;
	private InetSocketAddress destination;

	/**
	 * The Constructor.
	 *
	 * @param source
	 *          the source
	 * @param type
	 *          the type
	 */
	public MediaStreamerEvent(AbstractMediaStreamer source, MediaStreamerEventType type) {
		this.source = source;
		this.type = type;
		this.channel = source.getChannel();
		this.destination = source.getDestination();
	}

	/**
	 * Gets the source.
	 *
	 * @return the source
	 */
	public AbstractMediaStreamer getSource() {
		return source;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public MediaStreamerEventType getType() {
		return type;
	}

	/**
	 * Gets the channel.
	 *
	 * @return the channel
	 */
	public KiSyChannel getChannel() {
		return channel;
	}

	/**
	 * Gets the destination.
	 *
	 * @return the destination
	 */
	public InetSocketAddress getDestination() {
		return destination;
	}

}
