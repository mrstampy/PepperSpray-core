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
package com.github.mrstampy.pprspray.core.streamer.footer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Arrays;

import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunk;
import com.github.mrstampy.pprspray.core.streamer.util.MediaStreamerUtils;

/**
 * The Class MediaFooterChunk represents the received {@link MediaFooter}
 * message.
 */
public class MediaFooterChunk extends AbstractMediaChunk {

	private static final long serialVersionUID = -2298381545581713528L;

	/**
	 * The Constructor.
	 *
	 * @param message
	 *          the message
	 */
	public MediaFooterChunk(byte[] message) {
		super(message, null);
	}

	/**
	 * Extract media hash.
	 *
	 * @param message
	 *          the message
	 */
	protected void extractMediaHash(byte[] message) {
		setMediaHash(extractInt(message, 8, 12));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunk#
	 * extractMessageHash(byte[])
	 */
	protected void extractMessageHash(byte[] message) {
		setMessageHash(extractInt(message, 4, 8));
	}

	private int extractInt(byte[] message, int start, int end) {
		byte[] hash = Arrays.copyOfRange(message, start, end);

		ByteBuf buf = Unpooled.copiedBuffer(hash);

		return buf.getInt(0);
	}

	/**
	 * Extract media stream type.
	 *
	 * @param message
	 *          the message
	 * @param expected
	 *          the expected
	 */
	protected void extractMediaStreamType(byte[] message, MediaStreamType expected) {
		MediaStreamType type = MediaStreamerUtils.getMediaStreamTypeAsFooter(message);
		if (type == null) throw new IllegalArgumentException("Message is not a media type message");

		setMediaStreamType(type);
	}

	/**
	 * Extract header length.
	 *
	 * @param message
	 *          the message
	 */
	protected void extractHeaderLength(byte[] message) {
		setHeaderLength(MediaStreamerUtils.FOOTER_LENGTH);
	}

	/**
	 * Extract sequence.
	 *
	 * @param message
	 *          the message
	 */
	protected void extractSequence(byte[] message) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunk#
	 * extractAckRequired(byte[])
	 */
	protected void extractAckRequired(byte[] message) {
	}

	/**
	 * Checks if is terminate message.
	 *
	 * @param mediaHash
	 *          the media hash
	 * @return true, if checks if is terminate message
	 */
	public boolean isTerminateMessage(int mediaHash) {
		return isApplicable(MediaStreamType.NEGOTIATION, mediaHash);
	}

}
