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
package com.github.mrstampy.pprspray.core.streamer.chunk;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.Serializable;
import java.util.Arrays;

import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractMediaChunk.
 */
public abstract class AbstractMediaChunk implements Serializable, Comparable<AbstractMediaChunk> {

	private static final long serialVersionUID = 8518839866749374571L;

	/** The Constant HEADER_LENGTH. */
	public static final int HEADER_LENGTH = 16;

	private int mediaHash;

	private long sequence;

	private byte[] data;

	private MediaStreamType mediaStreamType;

	/**
	 * Constructor assumes that the message supplied is of the correct media type,
	 * as identified by implementations.
	 *
	 * @param message the message
	 * @param expected the expected
	 */
	protected AbstractMediaChunk(byte[] message, MediaStreamType expected) {
		MediaStreamType type = MediaStreamType.getMediaStreamTypeAsChunkHeader(message);
		if (type == null) throw new IllegalArgumentException("Message is not a media type message");
		if (type != expected) throw new IllegalArgumentException("Expected type " + expected + " but was " + type);

		setMediaStreamType(type);

		int length = 4;
		int headerLength = length + 12;

		setHashAndSequence(Arrays.copyOfRange(message, length, headerLength));
		setData(Arrays.copyOfRange(message, headerLength, message.length));
	}

	private void setHashAndSequence(byte[] hashAndSequence) {
		ByteBuf buf = Unpooled.copiedBuffer(hashAndSequence);

		setMediaHash(buf.getInt(0));
		setSequence(buf.getLong(4));
	}

	/**
	 * Implementation assumes that {@link AbstractMediaChunk}s have been placed in
	 * collections identified by {@link #getMediaHash()}.
	 *
	 * @param o the o
	 * @return the int
	 */
	@Override
	public int compareTo(AbstractMediaChunk o) {
		return (int) (getSequence() - o.getSequence());
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
	 * Gets the sequence.
	 *
	 * @return the sequence
	 */
	public long getSequence() {
		return sequence;
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public byte[] getData() {
		return data;
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
	 * Sets the sequence.
	 *
	 * @param sequence the sequence
	 */
	protected void setSequence(long sequence) {
		this.sequence = sequence;
	}

	/**
	 * Sets the data.
	 *
	 * @param data the data
	 */
	protected void setData(byte[] data) {
		this.data = data;
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
		this.mediaStreamType = mediaStreamType;
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
		if (message == null || message.length != HEADER_LENGTH) return false;
		
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
		if (message == null || message.length != HEADER_LENGTH) return false;

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
		return MediaStreamType.getMediaStreamHash(message) == mediaHash;
	}

}
