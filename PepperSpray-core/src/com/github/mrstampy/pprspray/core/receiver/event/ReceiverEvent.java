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
package com.github.mrstampy.pprspray.core.receiver.event;

import com.github.mrstampy.pprspray.core.receiver.AbstractChunkReceiver;

/**
 * {@link ReceiverEvent}s are generated when an {@link AbstractChunkReceiver}
 * changes state.
 */
public class ReceiverEvent {

	private ReceiverEventType type;
	private int mediaHash;

	/**
	 * The Constructor.
	 *
	 * @param type
	 *          the type
	 * @param mediaHash
	 *          the media hash
	 */
	public ReceiverEvent(ReceiverEventType type, int mediaHash) {
		this.type = type;
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
	 * Gets the type.
	 *
	 * @return the type
	 */
	public ReceiverEventType getType() {
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
}
