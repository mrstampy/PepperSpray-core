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
package com.github.mrstampy.pprspray.core.streamer.text;

import io.netty.buffer.ByteBuf;

import com.github.mrstampy.kitchensync.stream.Streamer;
import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunkProcessor;
import com.github.mrstampy.pprspray.core.streamer.util.MediaStreamerUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractMetaTextChunkProcessor.
 */
public class AbstractMetaTextChunkProcessor extends AbstractMediaChunkProcessor {

	private byte[] headerKey;
	private int marshallingClassHash;
	private Class<?> marshallingClass;

	/**
	 * The Constructor.
	 *
	 * @param headerKey
	 *          the header key
	 */
	protected AbstractMetaTextChunkProcessor(byte[] headerKey) {
		this(headerKey, null);
	}

	/**
	 * The Constructor.
	 *
	 * @param headerKey
	 *          the header key
	 * @param marshallingClass
	 *          the marshalling class
	 */
	protected AbstractMetaTextChunkProcessor(byte[] headerKey, Class<?> marshallingClass) {
		super(MediaStreamType.TEXT);
		this.headerKey = headerKey;
		this.marshallingClass = marshallingClass;
		this.marshallingClassHash = MediaStreamerUtils.createMarshallingClassNameHash(marshallingClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunkProcessor
	 * #sizeInBytes()
	 */
	@Override
	public int sizeInBytes() {
		return super.sizeInBytes() + headerKey.length + 4;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunkProcessor
	 * #writeHeader(com.github.mrstampy.kitchensync.stream.Streamer,
	 * io.netty.buffer.ByteBuf, int)
	 */
	protected void writeHeader(Streamer<?> streamer, ByteBuf buf, int headerLength) {
		super.writeHeader(streamer, buf, headerLength);

		buf.writeBytes(headerKey);
		buf.writeInt(getMarshallingClassHash());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunkProcessor
	 * #createMediaHash()
	 */
	@Override
	protected int createMediaHash() {
		return hashCode() + getMarshallingClassHash();
	}

	/**
	 * Gets the header key.
	 *
	 * @return the header key
	 */
	public byte[] getHeaderKey() {
		return headerKey;
	}

	/**
	 * Gets the marshalling class hash.
	 *
	 * @return the marshalling class hash
	 */
	public int getMarshallingClassHash() {
		return marshallingClassHash;
	}

	/**
	 * Gets the marshalling class.
	 *
	 * @return the marshalling class
	 */
	public Class<?> getMarshallingClass() {
		return marshallingClass;
	}

}
