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

import com.github.mrstampy.kitchensync.stream.Streamer;
import com.github.mrstampy.kitchensync.stream.header.AbstractChunkProcessor;
import com.github.mrstampy.pprspray.core.streamer.AbstractMediaStreamer;
import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.util.MediaStreamerUtils;

/**
 * The Class AbstractMediaChunkProcessor prepends the header to each chunk sent.
 * 
 * @see MediaStreamerUtils
 * @see AbstractMediaStreamer#setMediaChunkProcessor(AbstractMediaChunkProcessor)
 */
public abstract class AbstractMediaChunkProcessor extends AbstractChunkProcessor {

	private MediaStreamType mediaStreamType;
	private int mediaHash = Integer.MIN_VALUE;
	private int messageHash;

	/**
	 * The Constructor.
	 *
	 * @param mediaStreamType
	 *          the media stream type
	 */
	protected AbstractMediaChunkProcessor(MediaStreamType mediaStreamType) {
		setMediaStreamType(mediaStreamType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.kitchensync.stream.header.ChunkProcessor#sizeInBytes()
	 */
	@Override
	public int sizeInBytes(Streamer<?> streamer) {
		return MediaStreamerUtils.DEFAULT_HEADER_LENGTH;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.kitchensync.stream.header.AbstractChunkProcessor#
	 * processImpl(com.github.mrstampy.kitchensync.stream.Streamer, byte[])
	 */
	@Override
	protected ByteBuf processImpl(Streamer<?> streamer, byte[] message) {
		int headerLength = sizeInBytes(streamer);

		ByteBuf buf = createByteBuf(headerLength + message.length);

		writeHeader(streamer, buf, headerLength);
		appendToHeader(streamer, buf, headerLength);
		buf.writeBytes(message);

		return buf;
	}

	/**
	 * Hook for subclasses to append data to the header if required. Default impl
	 * does nothing.
	 * 
	 * @param streamer
	 *          supplied for state information
	 * @param buf
	 *          the buffer containing the default header information
	 * @param headerLength
	 *          the total length this header is expected to be
	 * @see #sizeInBytes(Streamer)
	 */
	protected void appendToHeader(Streamer<?> streamer, ByteBuf buf, int headerLength) {
	}

	/**
	 * Subclasses which have overridden {@link #sizeInBytes()} for custom header
	 * information will override this method, call super.writeHeader then write
	 * the custom header part.
	 *
	 * @param streamer
	 *          the streamer
	 * @param buf
	 *          the buf
	 * @param headerLength
	 *          the header length
	 * @see AbstractMediaChunk#extractCustomHeaderChunk(byte[])
	 */
	protected void writeHeader(Streamer<?> streamer, ByteBuf buf, int headerLength) {
		MediaStreamerUtils.writeHeader(buf,
				getMediaStreamType(),
				headerLength,
				getMessageHash(),
				getMediaHash(),
				streamer.getSequence(),
				streamer.isAckRequired());
	}

	/**
	 * Gets the media hash.
	 *
	 * @return the media hash
	 */
	public int getMediaHash() {
		if (mediaHash == Integer.MIN_VALUE) mediaHash = createMediaHash();

		return mediaHash;
	}

	/**
	 * Creates the media hash.
	 *
	 * @return the int
	 */
	protected abstract int createMediaHash();

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
	 * Gets the message hash.
	 *
	 * @return the message hash
	 */
	public int getMessageHash() {
		return messageHash;
	}

	/**
	 * Sets the message hash.
	 *
	 * @param messageHash
	 *          the message hash
	 */
	public void setMessageHash(int messageHash) {
		this.messageHash = messageHash;
	}

}
