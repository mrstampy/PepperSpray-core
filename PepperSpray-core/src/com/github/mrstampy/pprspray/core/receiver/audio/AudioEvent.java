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
package com.github.mrstampy.pprspray.core.receiver.audio;

// TODO: Auto-generated Javadoc
/**
 * The Class AudioEvent.
 */
public class AudioEvent {

	private int mediaHash;
	private byte[] chunk;

	/**
	 * The Constructor.
	 *
	 * @param mediaHash
	 *          the media hash
	 * @param chunk
	 *          the chunk
	 */
	public AudioEvent(int mediaHash, byte[] chunk) {
		this.chunk = chunk;
		this.mediaHash = mediaHash;
	}

	/**
	 * Checks if is applicable.
	 *
	 * @param mediaHash
	 *          the media hash
	 * @return true, if checks if is applicable
	 */
	public boolean isApplicable(int mediaHash) {
		return mediaHash == getMediaHash();
	}

	/**
	 * Gets the chunk.
	 *
	 * @return the chunk
	 */
	public byte[] getChunk() {
		return chunk;
	}

	/**
	 * Gets the media hash.
	 *
	 * @return the media hash
	 */
	public int getMediaHash() {
		return mediaHash;
	}

}
