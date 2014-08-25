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

import java.io.Serializable;

import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;

// TODO: Auto-generated Javadoc
/**
 * The Class MediaFooterMessage.
 */
public class MediaFooterMessage implements Serializable {

	private static final long serialVersionUID = -1006016564142259227L;

	/** The Constant FOOTER_LENGTH. */
	public static final int FOOTER_LENGTH = 8;

	private int mediaHash;
	private MediaStreamType mediaStreamType;

	/**
	 * The Constructor.
	 *
	 * @param message the message
	 */
	public MediaFooterMessage(byte[] message) {
		parseMessage(message);
	}

	/**
	 * The Constructor.
	 *
	 * @param type the type
	 * @param mediaHash the media hash
	 */
	public MediaFooterMessage(MediaStreamType type, int mediaHash) {
		setMediaHash(mediaHash);
		setMediaStreamType(type);
	}

	/**
	 * Parses the message.
	 *
	 * @param message the message
	 */
	protected void parseMessage(byte[] message) {
		ByteBuf buf = Unpooled.buffer(8);
		buf.writeBytes(message);

		setMediaStreamType(MediaStreamType.values()[Integer.MAX_VALUE - buf.getInt(0)]);
		setMediaHash(buf.getInt(4));
	}

	/**
	 * Gets the media hash.
	 *
	 * @return the media hash
	 */
	public int getMediaHash() {
		return mediaHash;
	}

	/**
	 * Sets the media hash.
	 *
	 * @param mediaHash the media hash
	 */
	protected void setMediaHash(int mediaHash) {
		this.mediaHash = mediaHash;
	}

	/**
	 * Gets the media stream type.
	 *
	 * @return the media stream type
	 */
	public MediaStreamType getMediaStreamType() {
		return mediaStreamType;
	}

	/**
	 * Sets the media stream type.
	 *
	 * @param mediaStreamType the media stream type
	 */
	protected void setMediaStreamType(MediaStreamType mediaStreamType) {
		if (mediaStreamType == null) throw new IllegalArgumentException("Media stream type cannot be null");
		this.mediaStreamType = mediaStreamType;
	}

	/**
	 * Checks if is media footer.
	 *
	 * @param message the message
	 * @param type the type
	 * @param mediaHash the media hash
	 * @return true, if checks if is media footer
	 */
	public static boolean isMediaFooter(byte[] message, MediaStreamType type, int mediaHash) {
		if (message == null || message.length != FOOTER_LENGTH) return false;

		ByteBuf buf = Unpooled.buffer(8);
		buf.writeBytes(message);

		return Integer.MAX_VALUE - type.ordinal() == buf.getInt(0) && mediaHash == buf.getInt(4);
	}
}
