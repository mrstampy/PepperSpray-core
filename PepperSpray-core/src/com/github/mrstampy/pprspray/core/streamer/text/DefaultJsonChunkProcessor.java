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

// TODO: Auto-generated Javadoc
/**
 * The Class DefaultJsonChunkProcessor.
 */
public class DefaultJsonChunkProcessor extends DefaultTextChunkProcessor {

	/** The Constant JSON_KEY. */
	public static final String JSON_KEY = "JSON:";

	/** The Constant JSON_KEY_BYTES. */
	public static final byte[] JSON_KEY_BYTES = JSON_KEY.getBytes();

	private Class<?> jsonClass;
	private int jsonClassNameHash;

	/**
	 * The Constructor.
	 */
	public DefaultJsonChunkProcessor() {
		this(DefaultJsonChunk.NoJsonClass.class);
	}

	/**
	 * The Constructor.
	 *
	 * @param jsonClass
	 *          the json class
	 */
	public DefaultJsonChunkProcessor(Class<?> jsonClass) {
		setJsonClass(jsonClass);
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
		return super.sizeInBytes() + JSON_KEY_BYTES.length + 4;
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

		buf.writeBytes(JSON_KEY_BYTES);
		buf.writeInt(getJsonClassNameHash());
	}

	/**
	 * Gets the json class.
	 *
	 * @return the json class
	 */
	public Class<?> getJsonClass() {
		return jsonClass;
	}

	/**
	 * Sets the json class.
	 *
	 * @param jsonClass
	 *          the json class
	 */
	public void setJsonClass(Class<?> jsonClass) {
		this.jsonClass = jsonClass;

		setJsonClassNameHash(jsonClass.getName().hashCode());
	}

	/**
	 * Gets the json class name hash.
	 *
	 * @return the json class name hash
	 */
	public int getJsonClassNameHash() {
		return jsonClassNameHash;
	}

	/**
	 * Sets the json class name hash.
	 *
	 * @param jsonClassHash
	 *          the json class name hash
	 */
	public void setJsonClassNameHash(int jsonClassHash) {
		this.jsonClassNameHash = jsonClassHash;
	}

}
