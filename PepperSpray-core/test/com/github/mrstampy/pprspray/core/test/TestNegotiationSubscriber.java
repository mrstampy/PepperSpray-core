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
package com.github.mrstampy.pprspray.core.test;

import javax.sound.sampled.AudioFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.pprspray.core.receiver.MediaProcessor;
import com.github.mrstampy.pprspray.core.streamer.negotiation.AcceptingNegotationSubscriber;
import com.github.mrstampy.pprspray.core.streamer.negotiation.NegotiationChunk;
import com.github.mrstampy.pprspray.core.streamer.negotiation.NegotiationEvent;
import com.google.common.eventbus.Subscribe;

// TODO: Auto-generated Javadoc
/**
 * The Class TestNegotiationSubscriber.
 */
public class TestNegotiationSubscriber extends AcceptingNegotationSubscriber {
	private static final Logger log = LoggerFactory.getLogger(TestNegotiationSubscriber.class);

	/** The Constant AUDIO_FORMAT. */
	public static final AudioFormat AUDIO_FORMAT = new AudioFormat(22000, 16, 2, true, true);

	/**
	 * The Constructor.
	 */
	public TestNegotiationSubscriber() {
		super(AUDIO_FORMAT);
	}

	/**
	 * Negotiation.
	 *
	 * @param event
	 *          the event
	 */
	@Subscribe
	public void negotiation(NegotiationEvent event) {
		log.debug("NegotiationEvent successful? {}, type {}, hash {}", event.isAccepted(), event.getChunk()
				.getMediaStreamType(), event.getChunk().getMediaHash());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.streamer.negotiation.
	 * AcceptingNegotationSubscriber
	 * #getMediaProcessor(com.github.mrstampy.pprspray
	 * .core.streamer.negotiation.NegotiationChunk)
	 */
	protected MediaProcessor getMediaProcessor(NegotiationChunk event) {
		return new LoggingProcessor();
	}

}
