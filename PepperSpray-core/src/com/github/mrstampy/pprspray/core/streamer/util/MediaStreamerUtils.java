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
import com.github.mrstampy.pprspray.core.streamer.text.DefaultJsonChunk;
import com.github.mrstampy.pprspray.core.streamer.text.DefaultJsonChunkProcessor;

// TODO: Auto-generated Javadoc
/**
 * The Class MediaStreamerUtils.
 */
public class MediaStreamerUtils {

	private static final int JSON_KEY_LENGTH = DefaultJsonChunkProcessor.JSON_KEY_BYTES.length;

	/** The Constant DEFAULT_HEADER_LENGTH. */
	public static final int DEFAULT_HEADER_LENGTH = 18;

	/** The Constant MEDIA_TYPE_CHUNK. */
	protected static final Chunk MEDIA_TYPE_CHUNK = new Chunk(0, 4);

	/** The Constant HEADER_LENGTH_CHUNK. */
	protected static final Chunk HEADER_LENGTH_CHUNK = new Chunk(4, 6);

	/** The Constant MEDIA_HASH_CHUNK. */
	protected static final Chunk MEDIA_HASH_CHUNK = new Chunk(6, 10);

	/** The Constant SEQUENCE_CHUNK. */
	protected static final Chunk SEQUENCE_CHUNK = new Chunk(10, DEFAULT_HEADER_LENGTH);

	/** The Constant JSON_KEY_CHUNK. */
	protected static final Chunk JSON_KEY_CHUNK = new Chunk(DEFAULT_HEADER_LENGTH, DEFAULT_HEADER_LENGTH
			+ JSON_KEY_LENGTH);

	/**
	 * Checks if is binary chunk.
	 *
	 * @param message
	 *          the message
	 * @return true, if checks if is binary chunk
	 */
	public static boolean isBinaryChunk(byte[] message) {
		return isMediaType(message, MediaStreamType.BINARY);
	}

	/**
	 * Checks if is file chunk.
	 *
	 * @param message
	 *          the message
	 * @return true, if checks if is file chunk
	 */
	public static boolean isFileChunk(byte[] message) {
		return isMediaType(message, MediaStreamType.FILE);
	}

	/**
	 * Checks if is text chunk.
	 *
	 * @param message
	 *          the message
	 * @return true, if checks if is text chunk
	 */
	public static boolean isTextChunk(byte[] message) {
		return isMediaType(message, MediaStreamType.TEXT);
	}

	/**
	 * Checks if is video chunk.
	 *
	 * @param message
	 *          the message
	 * @return true, if checks if is video chunk
	 */
	public static boolean isVideoChunk(byte[] message) {
		return isMediaType(message, MediaStreamType.VIDEO);
	}

	/**
	 * Checks if is default audio chunk.
	 *
	 * @param message
	 *          the message
	 * @return true, if checks if is default audio chunk
	 */
	public static boolean isAudioChunk(byte[] message) {
		return isMediaType(message, MediaStreamType.AUDIO);
	}

	/**
	 * Checks if is media type.
	 *
	 * @param message
	 *          the message
	 * @param type
	 *          the type
	 * @param mediaHash
	 *          the media hash
	 * @return true, if checks if is media type
	 */
	public static boolean isMediaType(byte[] message, MediaStreamType type, int mediaHash) {
		if (message == null || message.length < DEFAULT_HEADER_LENGTH) return false;

		return isMediaType(message, type) && isMediaHash(message, mediaHash);
	}

	/**
	 * Checks if is media type.
	 *
	 * @param message
	 *          the message
	 * @param type
	 *          the type
	 * @return true, if checks if is media type
	 */
	public static boolean isMediaType(byte[] message, MediaStreamType type) {
		if (message == null || message.length < DEFAULT_HEADER_LENGTH) return false;

		byte[] b = getChunk(message, MEDIA_TYPE_CHUNK);

		return Arrays.equals(b, type.ordinalBytes());
	}

	/**
	 * Checks if is media type footer.
	 *
	 * @param message
	 *          the message
	 * @param type
	 *          the type
	 * @return true, if checks if is media type
	 */
	public static boolean isMediaTypeFooter(byte[] message, MediaStreamType type) {
		if (message == null || message.length < DEFAULT_HEADER_LENGTH) return false;

		byte[] b = getChunk(message, MEDIA_TYPE_CHUNK);

		return Arrays.equals(b, type.eomBytes());
	}

	/**
	 * Checks if is media hash.
	 *
	 * @param message
	 *          the message
	 * @param mediaHash
	 *          the media hash
	 * @return true, if checks if is media hash
	 */
	public static boolean isMediaHash(byte[] message, int mediaHash) {
		return getMediaStreamHash(message) == mediaHash;
	}

	/**
	 * Gets the media stream type as chunk header.
	 *
	 * @param message
	 *          the message
	 * @return the media stream type as chunk header
	 */
	public static MediaStreamType getMediaStreamTypeAsChunkHeader(byte[] message) {
		byte[] b = getChunk(message, MEDIA_TYPE_CHUNK);

		for (MediaStreamType type : MediaStreamType.values()) {
			if (Arrays.equals(type.ordinalBytes(), b)) return type;
		}

		return null;
	}

	/**
	 * Gets the media stream type as footer.
	 *
	 * @param message
	 *          the message
	 * @return the media stream type as footer
	 */
	public static MediaStreamType getMediaStreamTypeAsFooter(byte[] message) {
		byte[] b = getChunk(message, MEDIA_TYPE_CHUNK);

		for (MediaStreamType type : MediaStreamType.values()) {
			if (Arrays.equals(type.eomBytes(), b)) return type;
		}

		return null;
	}

	/**
	 * Gets the media stream hash.
	 *
	 * @param message
	 *          the message
	 * @return the media stream hash
	 */
	public static int getMediaStreamHash(byte[] message) {
		return getIntegerChunk(getChunk(message, MEDIA_HASH_CHUNK));
	}

	/**
	 * Gets the media stream header length.
	 *
	 * @param message
	 *          the message
	 * @return the media stream hash
	 */
	public static int getMediaStreamHeaderLength(byte[] message) {
		return getShortChunk(getChunk(message, HEADER_LENGTH_CHUNK));
	}

	/**
	 * Gets the sequence.
	 *
	 * @param message
	 *          the message
	 * @return the sequence
	 */
	public static long getSequence(byte[] message) {
		return getLongChunk(getChunk(message, SEQUENCE_CHUNK));
	}

	/**
	 * Gets the custom header chunk.
	 *
	 * @param message
	 *          the message
	 * @param headerLength
	 *          the header length
	 * @return the custom header chunk
	 */
	public static byte[] getCustomHeaderChunk(byte[] message, int headerLength) {
		//@formatter:off
		return headerLength <= DEFAULT_HEADER_LENGTH 
				? new byte[0] 
				: getChunk(message, new Chunk(DEFAULT_HEADER_LENGTH, headerLength));
		//@formatter:on
	}

	/**
	 * Returns true if the message indicates it is a JSON message.
	 *
	 * @param message
	 *          the message
	 * @return true, if checks if is json message
	 * @see DefaultJsonChunk
	 * @see DefaultJsonChunkProcessor
	 */
	public static boolean isJsonMessage(byte[] message) {
		if (message == null || message.length <= DEFAULT_HEADER_LENGTH + JSON_KEY_LENGTH) return false;

		byte[] b = getChunk(message, JSON_KEY_CHUNK);

		return Arrays.equals(b, DefaultJsonChunkProcessor.JSON_KEY_BYTES);
	}

	/**
	 * Returns true if the message indicates it is a JSON message and it contains
	 * the name of the specified class as the Java representation of the message.
	 *
	 * @param message
	 *          the message
	 * @param jsonClassBytes
	 *          the json class bytes
	 * @return true, if checks if is json message
	 * @see DefaultJsonChunk
	 * @see DefaultJsonChunkProcessor
	 */
	public static boolean isJsonMessage(byte[] message, byte[] jsonClassBytes) {
		if (!isJsonMessage(message)) return false;

		int start = DEFAULT_HEADER_LENGTH + JSON_KEY_LENGTH;
		int end = start + jsonClassBytes.length;

		byte[] b = getChunk(message, new Chunk(start, end));

		return Arrays.equals(b, jsonClassBytes);
	}

	/**
	 * Returns the hash of the name of the class representation relevant to this JSON message.
	 * 
	 * @param message
	 * @return
	 * @see DefaultJsonChunkProcessor#setJsonClassNameHash(int)
	 * @see Class#getName()
	 */
	public static int getJsonClassHash(byte[] message) {
		int start = DEFAULT_HEADER_LENGTH + JSON_KEY_LENGTH;
		int end = start + 4;

		return getIntegerChunk(getChunk(message, new Chunk(start, end)));
	}

	/**
	 * Gets the integer chunk.
	 *
	 * @param chunk
	 *          the chunk
	 * @return the integer chunk
	 */
	protected static int getIntegerChunk(byte[] chunk) {
		ByteBuf buf = Unpooled.copiedBuffer(chunk);

		return buf.getInt(0);
	}

	/**
	 * Gets the short chunk.
	 *
	 * @param chunk
	 *          the chunk
	 * @return the short chunk
	 */
	protected static int getShortChunk(byte[] chunk) {
		ByteBuf buf = Unpooled.copiedBuffer(chunk);

		return buf.getShort(0);
	}

	/**
	 * Gets the long chunk.
	 *
	 * @param chunk
	 *          the chunk
	 * @return the long chunk
	 */
	protected static long getLongChunk(byte[] chunk) {
		ByteBuf buf = Unpooled.copiedBuffer(chunk);

		return buf.getLong(0);
	}

	/**
	 * Gets the chunk.
	 *
	 * @param message
	 *          the message
	 * @param chunk
	 *          the chunk
	 * @return the chunk
	 */
	protected static byte[] getChunk(byte[] message, Chunk chunk) {
		return Arrays.copyOfRange(message, chunk.start, chunk.end);
	}

	/**
	 * The Constructor.
	 */
	protected MediaStreamerUtils() {
	}

	/**
	 * The Class Chunk.
	 */
	protected static class Chunk {

		/** The start. */
		int start;

		/** The end. */
		int end;

		/**
		 * The Constructor.
		 *
		 * @param start
		 *          the start
		 * @param end
		 *          the end
		 */
		public Chunk(int start, int end) {
			this.start = start;
			this.end = end;
		}
	}

}
