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

import java.net.InetSocketAddress;
import java.util.Arrays;

import com.github.mrstampy.kitchensync.netty.channel.DefaultChannelRegistry;
import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;
import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.footer.MediaFooter;
import com.github.mrstampy.pprspray.core.streamer.text.AbstractMetaTextChunk;
import com.github.mrstampy.pprspray.core.streamer.text.DefaultJsonChunk;
import com.github.mrstampy.pprspray.core.streamer.text.DefaultJsonChunkProcessor;
import com.github.mrstampy.pprspray.core.streamer.text.DefaultXmlChunkProcessor;

// TODO: Auto-generated Javadoc
/**
 * The Class MediaStreamerUtils.
 */
public class MediaStreamerUtils {

	/** The Constant DEFAULT_HEADER_LENGTH. */
	public static final int DEFAULT_HEADER_LENGTH = 19;

	/** The Constant FOOTER_LENGTH. */
	public static final int FOOTER_LENGTH = 8;

	/** The Constant MEDIA_TYPE_CHUNK. */
	protected static final Chunk MEDIA_TYPE_CHUNK = new Chunk(0, 4);

	/** The Constant HEADER_LENGTH_CHUNK. */
	protected static final Chunk HEADER_LENGTH_CHUNK = new Chunk(4, 6);

	/** The Constant MEDIA_HASH_CHUNK. */
	protected static final Chunk MEDIA_HASH_CHUNK = new Chunk(6, 10);

	/** The Constant SEQUENCE_CHUNK. */
	protected static final Chunk SEQUENCE_CHUNK = new Chunk(10, 18);

	protected static final Chunk ACK_REQ_CHUNK = new Chunk(18, DEFAULT_HEADER_LENGTH);

	/**
	 * Creates the marshalling class name hash.
	 *
	 * @param clazz
	 *          the clazz
	 * @return the int
	 */
	public static int createMarshallingClassNameHash(Class<?> clazz) {
		return clazz == null ? AbstractMetaTextChunk.NO_MARSHALLING_CLASS : clazz.getName().hashCode();
	}

	/**
	 * Send termination event.
	 *
	 * @param mediaHash
	 *          the media hash
	 * @param local
	 *          the local
	 * @param remote
	 *          the remote
	 */
	public static void sendTerminationEvent(int mediaHash, InetSocketAddress local, InetSocketAddress remote) {
		KiSyChannel channel = getChannel(local);

		sendTerminationEvent(mediaHash, channel, remote);
	}

	/**
	 * Send termination event.
	 *
	 * @param mediaHash
	 *          the media hash
	 * @param channel
	 *          the channel
	 * @param remote
	 *          the remote
	 */
	public static void sendTerminationEvent(int mediaHash, KiSyChannel channel, InetSocketAddress remote) {
		if (channel == null) return;

		MediaFooter footer = new MediaFooter(MediaStreamType.NEGOTIATION, mediaHash);

		byte[] terminate = footer.createFooter();
		channel.send(terminate, remote);
	}

	/**
	 * Gets the channel.
	 *
	 * @param local
	 *          the local
	 * @return the channel
	 */
	public static KiSyChannel getChannel(InetSocketAddress local) {
		KiSyChannel channel = DefaultChannelRegistry.INSTANCE.getChannel(local.getPort());
		if (channel == null) channel = DefaultChannelRegistry.INSTANCE.getMulticastChannel(local);

		return channel;
	}

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
		if (message == null || message.length < FOOTER_LENGTH) return false;

		byte[] b = getChunk(message, MEDIA_TYPE_CHUNK);

		return Arrays.equals(b, type.eomBytes());
	}

	/**
	 * Checks if is media footer.
	 *
	 * @param message
	 *          the message
	 * @param type
	 *          the type
	 * @param mediaHash
	 *          the media hash
	 * @return true, if checks if is media footer
	 */
	public static boolean isMediaFooter(byte[] message, MediaStreamType type, int mediaHash) {
		if (message == null || message.length != FOOTER_LENGTH) return false;

		ByteBuf buf = Unpooled.buffer(8);
		buf.writeBytes(message);

		return Integer.MAX_VALUE - type.ordinal() == buf.getInt(0) && mediaHash == buf.getInt(4);
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

		return MediaStreamType.getTypeAsHeader(b);
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

		return MediaStreamType.getTypeAsFooter(b);
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

	public static boolean isAckRequired(byte[] message) {
		return Arrays.copyOfRange(message, ACK_REQ_CHUNK.start, ACK_REQ_CHUNK.end)[0] == 1;
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
		return isCustomHeaderMessage(message, DefaultJsonChunkProcessor.JSON_KEY_BYTES);
	}

	/**
	 * Checks if is text only message.
	 *
	 * @param message
	 *          the message
	 * @return true, if checks if is text only message
	 */
	public static boolean isTextOnlyMessage(byte[] message) {
		return isTextChunk(message) && !isJsonMessage(message) && !isXmlMessage(message);
	}

	/**
	 * Gets the marshalling class hash.
	 *
	 * @param message
	 *          the message
	 * @param keyLength
	 *          the key length
	 * @return the marshalling class hash
	 */
	public static int getMarshallingClassHash(byte[] message, int keyLength) {
		int start = DEFAULT_HEADER_LENGTH + keyLength;
		int end = start + 4;

		return getIntegerChunk(getChunk(message, new Chunk(start, end)));
	}

	/**
	 * Checks if is xml message.
	 *
	 * @param message
	 *          the message
	 * @return true, if checks if is xml message
	 */
	public static boolean isXmlMessage(byte[] message) {
		return isCustomHeaderMessage(message, DefaultXmlChunkProcessor.XML_KEY_BYTES);
	}

	/**
	 * Checks if is custom header message.
	 *
	 * @param message
	 *          the message
	 * @param headerBytes
	 *          the header bytes
	 * @return true, if checks if is custom header message
	 */
	public static boolean isCustomHeaderMessage(byte[] message, byte[] headerBytes) {
		int minLength = DEFAULT_HEADER_LENGTH + headerBytes.length;

		if (message == null || message.length <= minLength) return false;

		byte[] b = getChunk(message, new Chunk(DEFAULT_HEADER_LENGTH, minLength));

		return Arrays.equals(b, headerBytes);
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
	 * Write header.
	 *
	 * @param buf
	 *          the buf
	 * @param type
	 *          the type
	 * @param headerLength
	 *          the header length
	 * @param mediaHash
	 *          the media hash
	 * @param sequence
	 *          the sequence
	 */
	public static void writeHeader(ByteBuf buf, MediaStreamType type, int headerLength, int mediaHash, long sequence,
			boolean ackRequired) {
		buf.writeBytes(type.ordinalBytes());
		buf.writeShort(headerLength);
		buf.writeInt(mediaHash);
		buf.writeLong(sequence);
		buf.writeBoolean(ackRequired);
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
