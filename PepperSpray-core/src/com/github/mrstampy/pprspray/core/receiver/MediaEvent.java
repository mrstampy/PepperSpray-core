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
package com.github.mrstampy.pprspray.core.receiver;

import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;

// TODO: Auto-generated Javadoc
/**
 * The Class MediaEvent.
 */
public class MediaEvent {

	private MediaStreamType type;
	private int mediaHash;
	private byte[] processed;

	/**
	 * The Constructor.
	 *
	 * @param type
	 *          the type
	 * @param mediaHash
	 *          the media hash
	 * @param processed
	 *          the processed
	 */
	public MediaEvent(MediaStreamType type, int mediaHash, byte[] processed) {
		this.type = type;
		this.mediaHash = mediaHash;
		this.processed = processed;
	}

	/**
	 * Checks if is applicable.
	 *
	 * @param type
	 *          the type
	 * @param mediaHash
	 *          the media hash
	 * @return true, if checks if is applicable
	 */
	public boolean isApplicable(MediaStreamType type, int mediaHash) {
		return type == getType() && mediaHash == getMediaHash();
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public MediaStreamType getType() {
		return type;
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
	 * Gets the processed.
	 *
	 * @return the processed
	 */
	public byte[] getProcessed() {
		return processed;
	}

}
