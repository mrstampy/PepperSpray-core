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

import static com.github.mrstampy.kitchensync.netty.channel.DefaultChannelRegistry.INSTANCE;

import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;
import com.github.mrstampy.pprspray.core.receiver.audio.AudioReceiver;
import com.github.mrstampy.pprspray.core.receiver.binary.BinaryReceiver;
import com.github.mrstampy.pprspray.core.receiver.file.FileReceiver;
import com.github.mrstampy.pprspray.core.receiver.text.TextReceiver;
import com.github.mrstampy.pprspray.core.receiver.webcam.WebcamReceiver;
import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
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
		KiSyChannel channel = INSTANCE.getChannel(event.getChannelPort());

		if (channel == null) channel = INSTANCE.getMulticastChannel(event.getReceiver());

		if (channel == null) {
			throw new IllegalStateException("Cannot locate channel for port " + event.getChannelPort() + " or address "
					+ event.getReceiver());
		}

		return channel;
	}

	/**
	 * Creates the receiver.
	 *
	 * @param requestedType
	 *          the requested type
	 * @param mediaHash
	 *          the media hash
	 */
	protected void createReceiver(MediaStreamType requestedType, int mediaHash) {
		switch (requestedType) {
		case AUDIO:
			new AudioReceiver(mediaHash);
			break;
		case BINARY:
			new BinaryReceiver(mediaHash);
			break;
		case FILE:
			new FileReceiver(mediaHash);
			break;
		case TEXT:
			new TextReceiver(mediaHash);
			break;
		case VIDEO:
			new WebcamReceiver(mediaHash);
			break;
		default:
			break;
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
