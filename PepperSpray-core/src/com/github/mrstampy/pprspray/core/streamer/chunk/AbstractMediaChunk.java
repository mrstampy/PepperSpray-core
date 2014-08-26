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

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Arrays;

import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.util.MediaStreamerUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractMediaChunk.
 */
public abstract class AbstractMediaChunk implements Serializable, Comparable<AbstractMediaChunk> {

	private static final long serialVersionUID = 8518839866749374571L;

	private MediaStreamType mediaStreamType;
	private int headerLength;
	private int mediaHash;
	private long sequence;

	private byte[] data;

	private byte[] customHeaderChunk;

	private int channelPort;
	private InetSocketAddress sender;

	/**
	 * Constructor assumes that the message supplied is of the correct media type,
	 * as identified by implementations.
	 *
	 * @param message
	 *          the message
	 * @param expected
	 *          the expected
	 */
	protected AbstractMediaChunk(byte[] message, MediaStreamType expected) {
		extractMediaStreamType(message, expected);
		extractHeaderLength(message);
		extractMediaHash(message);
		extractSequence(message);
		extractCustomHeaderChunk(message);
		setData(Arrays.copyOfRange(message, getHeaderLength(), message.length));
	}

	/**
	 * Extract custom header chunk.
	 *
	 * @param message
	 *          the message
	 */
	protected void extractCustomHeaderChunk(byte[] message) {
		setCustomHeaderChunk(MediaStreamerUtils.getCustomHeaderChunk(message, getHeaderLength()));
	}

	/**
	 * Extract sequence.
	 *
	 * @param message
	 *          the message
	 */
	protected void extractSequence(byte[] message) {
		long sequence = MediaStreamerUtils.getSequence(message);
		setSequence(sequence);
	}

	/**
	 * Extract media hash.
	 *
	 * @param message
	 *          the message
	 */
	protected void extractMediaHash(byte[] message) {
		int mediaHash = MediaStreamerUtils.getMediaStreamHash(message);
		setMediaHash(mediaHash);
	}

	/**
	 * Extract header length.
	 *
	 * @param message
	 *          the message
	 */
	protected void extractHeaderLength(byte[] message) {
		int headerLength = MediaStreamerUtils.getMediaStreamHeaderLength(message);
		if (headerLength < MediaStreamerUtils.DEFAULT_HEADER_LENGTH) {
			throw new IllegalArgumentException("Header length must be > " + MediaStreamerUtils.DEFAULT_HEADER_LENGTH
					+ ", was " + headerLength);
		}

		setHeaderLength(headerLength);
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
		MediaStreamType type = MediaStreamerUtils.getMediaStreamTypeAsChunkHeader(message);
		if (type == null) throw new IllegalArgumentException("Message is not a media type message");
		if (type != expected) throw new IllegalArgumentException("Expected type " + expected + " but was " + type);

		setMediaStreamType(type);
	}

	/**
	 * Implementation assumes that {@link AbstractMediaChunk}s have been placed in
	 * collections identified by {@link #getMediaHash()}.
	 *
	 * @param o
	 *          the o
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
	 * @param mediaHash
	 *          the media hash
	 */
	protected void setMediaHash(int mediaHash) {
		this.mediaHash = mediaHash;
	}

	/**
	 * Sets the sequence.
	 *
	 * @param sequence
	 *          the sequence
	 */
	protected void setSequence(long sequence) {
		this.sequence = sequence;
	}

	/**
	 * Sets the data.
	 *
	 * @param data
	 *          the data
	 */
	public void setData(byte[] data) {
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
	 * @param mediaStreamType
	 *          the media stream type
	 */
	protected void setMediaStreamType(MediaStreamType mediaStreamType) {
		this.mediaStreamType = mediaStreamType;
	}

	/**
	 * Gets the header length.
	 *
	 * @return the header length
	 */
	public int getHeaderLength() {
		return headerLength;
	}

	/**
	 * Sets the header length.
	 *
	 * @param headerLength
	 *          the header length
	 */
	protected void setHeaderLength(int headerLength) {
		this.headerLength = headerLength;
	}

	/**
	 * Gets the custom header chunk.
	 *
	 * @return the custom header chunk
	 * @see AbstractMediaChunkProcessor#writeHeader(com.github.mrstampy.kitchensync.stream.Streamer,
	 *      io.netty.buffer.ByteBuf, int)
	 */
	protected byte[] getCustomHeaderChunk() {
		return customHeaderChunk;
	}

	/**
	 * Sets the custom header chunk.
	 *
	 * @param customHeaderChunk
	 *          the custom header chunk
	 * @see AbstractMediaChunkProcessor#writeHeader(com.github.mrstampy.kitchensync.stream.Streamer,
	 *      io.netty.buffer.ByteBuf, int)
	 */
	protected void setCustomHeaderChunk(byte[] customHeaderChunk) {
		this.customHeaderChunk = customHeaderChunk;
	}

	/**
	 * Gets the sender.
	 *
	 * @return the sender
	 */
	public InetSocketAddress getSender() {
		return sender;
	}

	/**
	 * Sets the sender.
	 *
	 * @param sender
	 *          the sender
	 */
	public void setSender(InetSocketAddress sender) {
		this.sender = sender;
	}

	/**
	 * Gets the channel port.
	 *
	 * @return the channel port
	 */
	public int getChannelPort() {
		return channelPort;
	}

	/**
	 * Sets the channel port.
	 *
	 * @param channelPort
	 *          the channel port
	 */
	public void setChannelPort(int channelPort) {
		this.channelPort = channelPort;
	}

}
