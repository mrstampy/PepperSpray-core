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
 * The Class DefaultXmlChunkProcessor.
 */
public class DefaultXmlChunkProcessor extends DefaultTextChunkProcessor {

	/** The Constant XML_KEY. */
	public static final String XML_KEY = "XML:";

	/** The Constant XML_KEY_BYTES. */
	public static final byte[] XML_KEY_BYTES = XML_KEY.getBytes();

	private Class<?> xmlClass;
	private int xmlClassNameHash;

	/**
	 * The Constructor.
	 */
	public DefaultXmlChunkProcessor() {
		this(DefaultXmlChunk.NoXmlClass.class);
	}

	/**
	 * The Constructor.
	 *
	 * @param xmlClass
	 *          the xml class
	 */
	public DefaultXmlChunkProcessor(Class<?> xmlClass) {
		setXmlClass(xmlClass);
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
		return super.sizeInBytes() + XML_KEY_BYTES.length + 4;
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

		buf.writeBytes(XML_KEY_BYTES);
		buf.writeInt(getXmlClassNameHash());
	}

	/**
	 * Gets the xml class.
	 *
	 * @return the xml class
	 */
	public Class<?> getXmlClass() {
		return xmlClass;
	}

	/**
	 * Sets the xml class.
	 *
	 * @param xmlClass
	 *          the xml class
	 */
	public void setXmlClass(Class<?> xmlClass) {
		this.xmlClass = xmlClass;

		setXmlClassNameHash(xmlClass.getName().hashCode());
	}

	/**
	 * Gets the xml class name hash.
	 *
	 * @return the xml class name hash
	 */
	public int getXmlClassNameHash() {
		return xmlClassNameHash;
	}

	/**
	 * Sets the xml class name hash.
	 *
	 * @param xmlClassHash
	 *          the xml class name hash
	 */
	public void setXmlClassNameHash(int xmlClassHash) {
		this.xmlClassNameHash = xmlClassHash;
	}

}
