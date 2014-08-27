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
package com.github.mrstampy.pprspray.core.streamer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Arrays;

// TODO: Auto-generated Javadoc
/**
 * The Enum MediaStreamType.
 */
public enum MediaStreamType {

	//@formatter:off
	/** The audio. */
	AUDIO,
	
	/** The binary. */
	BINARY,
	
	/** The file. */
	FILE,
	
	/** The text. */
	TEXT,
	
	/** The video. */
	VIDEO,
	
	/** The negotiation. */
	NEGOTIATION,
	
	/** The negotiation ack. */
	NEGOTIATION_ACK;
	//@formatter:on

	/** The ordinal bytes. */
	byte[] ordinalBytes;

	/** The eom bytes. */
	byte[] eomBytes;

	private MediaStreamType() {
		setOrdinalBytes();
		setEomBytes();
	}

	private void setEomBytes() {
		int eomOrdinal = Integer.MAX_VALUE - ordinal();

		ByteBuf buf = Unpooled.buffer(4);
		buf.writeInt(eomOrdinal);
		eomBytes = buf.array();
	}

	private void setOrdinalBytes() {
		ByteBuf buf = Unpooled.buffer(4);
		buf.writeInt(ordinal());
		ordinalBytes = buf.array();
	}

	/**
	 * Ordinal bytes.
	 *
	 * @return the byte[]
	 */
	public byte[] ordinalBytes() {
		return ordinalBytes;
	}

	/**
	 * Eom bytes.
	 *
	 * @return the byte[]
	 */
	public byte[] eomBytes() {
		return eomBytes;
	}

	/**
	 * Gets the type as header.
	 *
	 * @param b
	 *          the b
	 * @return the type as header
	 */
	public static MediaStreamType getTypeAsHeader(byte[] b) {
		for (MediaStreamType type : MediaStreamType.values()) {
			if (Arrays.equals(type.ordinalBytes(), b)) return type;
		}

		return null;
	}

	/**
	 * Gets the type as footer.
	 *
	 * @param b
	 *          the b
	 * @return the type as footer
	 */
	public static MediaStreamType getTypeAsFooter(byte[] b) {
		for (MediaStreamType type : MediaStreamType.values()) {
			if (Arrays.equals(type.eomBytes(), b)) return type;
		}

		return null;
	}
}
