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

import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunk;
import com.github.mrstampy.pprspray.core.streamer.util.MediaStreamerUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class MediaFooterChunk.
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
		setHeaderLength(8);
	}

	/**
	 * Extract sequence.
	 *
	 * @param message
	 *          the message
	 */
	protected void extractSequence(byte[] message) {
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
