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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.pprspray.core.streamer.audio.DefaultAudioChunk;
import com.github.mrstampy.pprspray.core.streamer.binary.DefaultBinaryChunk;
import com.github.mrstampy.pprspray.core.streamer.file.DefaultFileChunk;
import com.github.mrstampy.pprspray.core.streamer.footer.MediaFooterChunk;
import com.github.mrstampy.pprspray.core.streamer.negotiation.NegotiationAckChunk;
import com.github.mrstampy.pprspray.core.streamer.negotiation.NegotiationChunk;
import com.github.mrstampy.pprspray.core.streamer.text.DefaultJsonChunk;
import com.github.mrstampy.pprspray.core.streamer.text.DefaultTextChunk;
import com.github.mrstampy.pprspray.core.streamer.webcam.DefaultWebcamChunk;
import com.google.common.eventbus.Subscribe;

// TODO: Auto-generated Javadoc
/**
 * The Class LoggingChunkReceiver.
 */
public class LoggingChunkReceiver {
	private final Logger log = LoggerFactory.getLogger(LoggingChunkReceiver.class);

	/**
	 * Receive.
	 *
	 * @param chunk
	 *          the chunk
	 */
	@Subscribe
	public void receive(DefaultAudioChunk chunk) {
		log.debug("Received audio, type {}, hash {}, sequence {}", chunk.getMediaStreamType(), chunk.getMediaHash(),
				chunk.getSequence());
	}

	/**
	 * Receive.
	 *
	 * @param chunk
	 *          the chunk
	 */
	@Subscribe
	public void receive(DefaultBinaryChunk chunk) {
		log.debug("Received binary, type {}, hash {}, sequence {}", chunk.getMediaStreamType(), chunk.getMediaHash(),
				chunk.getSequence());
	}

	/**
	 * Receive.
	 *
	 * @param chunk
	 *          the chunk
	 */
	@Subscribe
	public void receive(DefaultFileChunk chunk) {
		log.debug("Received file, type {}, hash {}, sequence {}", chunk.getMediaStreamType(), chunk.getMediaHash(),
				chunk.getSequence());
	}

	/**
	 * Receive.
	 *
	 * @param chunk
	 *          the chunk
	 */
	@Subscribe
	public void receive(DefaultJsonChunk chunk) {
		log.debug("Received JSON, type {}, hash {}, sequence {}", chunk.getMediaStreamType(), chunk.getMediaHash(),
				chunk.getSequence());
	}

	/**
	 * Receive.
	 *
	 * @param chunk
	 *          the chunk
	 */
	@Subscribe
	public void receive(DefaultTextChunk chunk) {
		log.debug("Received text, type {}, hash {}, sequence {}", chunk.getMediaStreamType(), chunk.getMediaHash(),
				chunk.getSequence());
	}

	/**
	 * Receive.
	 *
	 * @param chunk
	 *          the chunk
	 */
	@Subscribe
	public void receive(DefaultWebcamChunk chunk) {
		log.debug("Received webcam, type {}, hash {}, sequence {}", chunk.getMediaStreamType(), chunk.getMediaHash(),
				chunk.getSequence());
	}

	/**
	 * Receive.
	 *
	 * @param chunk
	 *          the chunk
	 */
	@Subscribe
	public void receive(NegotiationChunk chunk) {
		log.debug("Received negotiation, type {}, hash {}, sequence {}", chunk.getMediaStreamType(), chunk.getMediaHash(),
				chunk.getSequence());
	}

	/**
	 * Receive.
	 *
	 * @param chunk
	 *          the chunk
	 */
	@Subscribe
	public void receive(NegotiationAckChunk chunk) {
		log.debug("Received negotiation acknowledgement, type {}, hash {}, sequence {}", chunk.getMediaStreamType(),
				chunk.getMediaHash(), chunk.getSequence());
	}

	/**
	 * Receive.
	 *
	 * @param footerMessage
	 *          the footer message
	 */
	@Subscribe
	public void receive(MediaFooterChunk footerMessage) {
		log.debug("Received footer, {}, {}", footerMessage.getMediaStreamType(), footerMessage.getMediaHash());
	}

}
