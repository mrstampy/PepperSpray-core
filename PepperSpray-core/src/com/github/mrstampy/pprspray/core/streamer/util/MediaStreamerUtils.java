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
package com.github.mrstampy.pprspray.core.streamer.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Arrays;

import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunk;

// TODO: Auto-generated Javadoc
/**
 * The Class MediaStreamerUtils.
 */
public class MediaStreamerUtils {
	
	/**
	 * Checks if is binary chunk.
	 *
	 * @param message the message
	 * @return true, if checks if is binary chunk
	 */
	public static boolean isBinaryChunk(byte[] message) {
		return isMediaType(message, MediaStreamType.BINARY);
	}

	/**
	 * Checks if is file chunk.
	 *
	 * @param message the message
	 * @return true, if checks if is file chunk
	 */
	public static boolean isFileChunk(byte[] message) {
		return isMediaType(message, MediaStreamType.FILE);
	}
	
	/**
	 * Checks if is text chunk.
	 *
	 * @param message the message
	 * @return true, if checks if is text chunk
	 */
	public static boolean isTextChunk(byte[] message) {
		return isMediaType(message, MediaStreamType.TEXT);
	}
	
	/**
	 * Checks if is video chunk.
	 *
	 * @param message the message
	 * @return true, if checks if is video chunk
	 */
	public static boolean isVideoChunk(byte[] message) {
		return isMediaType(message, MediaStreamType.VIDEO);
	}

	/**
	 * Checks if is default audio chunk.
	 *
	 * @param message the message
	 * @return true, if checks if is default audio chunk
	 */
	public static boolean isAudioChunk(byte[] message) {
		return isMediaType(message, MediaStreamType.AUDIO);
	}

	/**
	 * Checks if is media type.
	 *
	 * @param message the message
	 * @param type the type
	 * @param mediaHash the media hash
	 * @return true, if checks if is media type
	 */
	public static boolean isMediaType(byte[] message, MediaStreamType type, int mediaHash) {
		if (message == null || message.length != AbstractMediaChunk.HEADER_LENGTH) return false;
		
		return isMediaType(message, type) && isMediaHash(message, mediaHash);
	}

	/**
	 * Checks if is media type.
	 *
	 * @param message the message
	 * @param type the type
	 * @return true, if checks if is media type
	 */
	public static boolean isMediaType(byte[] message, MediaStreamType type) {
		if (message == null || message.length != AbstractMediaChunk.HEADER_LENGTH) return false;

		byte[] b = Arrays.copyOfRange(message, 0, 4);

		return Arrays.equals(b, type.ordinalBytes());
	}
	
	/**
	 * Checks if is media hash.
	 *
	 * @param message the message
	 * @param mediaHash the media hash
	 * @return true, if checks if is media hash
	 */
	public static boolean isMediaHash(byte[] message, int mediaHash) {
		return getMediaStreamHash(message) == mediaHash;
	}

	/**
	 * Gets the media stream type as chunk header.
	 *
	 * @param message the message
	 * @return the media stream type as chunk header
	 */
	public static MediaStreamType getMediaStreamTypeAsChunkHeader(byte[] message) {
		byte[] b = Arrays.copyOfRange(message, 0, 4);

		for (MediaStreamType type : MediaStreamType.values()) {
			if (Arrays.equals(type.ordinalBytes(), b)) return type;
		}

		return null;
	}

	/**
	 * Gets the media stream type as footer.
	 *
	 * @param message the message
	 * @return the media stream type as footer
	 */
	public static MediaStreamType getMediaStreamTypeAsFooter(byte[] message) {
		byte[] b = Arrays.copyOfRange(message, 0, 4);

		for (MediaStreamType type : MediaStreamType.values()) {
			if (Arrays.equals(type.eomBytes(), b)) return type;
		}

		return null;
	}

	/**
	 * Gets the media stream hash.
	 *
	 * @param message the message
	 * @return the media stream hash
	 */
	public static int getMediaStreamHash(byte[] message) {
		byte[] b = Arrays.copyOfRange(message, 4, 8);

		ByteBuf buf = Unpooled.copiedBuffer(b);

		return buf.getInt(0);
	}
	
	private MediaStreamerUtils() {
	}

}
