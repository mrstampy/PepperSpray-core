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

import com.github.mrstampy.pprspray.core.streamer.util.MediaStreamerUtils;

/**
 * The Class AbstractMetaTextChunk encapsulates the meta information for a chunk
 * of text. The {@link #getMarshallingClassNameHash()} is an integer that
 * represents the hash code of the value returned by Class.getName().hashCode() of the pojo
 * which is the Java representation of the message to allow ease of matching of
 * messages to classes.
 */
public class AbstractMetaTextChunk extends DefaultTextChunk {

	/** The Constant NO_MARSHALLING_CLASS. */
	public static final int NO_MARSHALLING_CLASS = -1;

	private static final long serialVersionUID = 3660882521469296336L;

	private int marshallingClassNameHash;
	private byte[] headerBytes;

	/**
	 * The Constructor.
	 *
	 * @param message
	 *          the message
	 * @param headerBytes
	 *          the header bytes
	 */
	protected AbstractMetaTextChunk(byte[] message, byte[] headerBytes) {
		super(message);
		this.headerBytes = headerBytes;
		extractMarshallingClassNameHash(message);
	}

	private void extractMarshallingClassNameHash(byte[] message) {
		int mcnh = MediaStreamerUtils.getMarshallingClassHash(message, headerBytes.length);
		this.marshallingClassNameHash = mcnh;
	}

	/**
	 * Checks for marshalling class.
	 *
	 * @return true, if checks for marshalling class
	 */
	public boolean hasMarshallingClass() {
		return NO_MARSHALLING_CLASS != getMarshallingClassNameHash();
	}

	/**
	 * Gets the marshalling class name hash.
	 *
	 * @return the marshalling class name hash
	 */
	public int getMarshallingClassNameHash() {
		return marshallingClassNameHash;
	}

}
