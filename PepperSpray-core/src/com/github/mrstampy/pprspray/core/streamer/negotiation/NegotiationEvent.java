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
package com.github.mrstampy.pprspray.core.streamer.negotiation;

// TODO: Auto-generated Javadoc
/**
 * The Class NegotiationEvent.
 */
public class NegotiationEvent {

	private boolean accepted;
	private NegotiationChunk chunk;

	/**
	 * The Constructor.
	 *
	 * @param chunk
	 *          the chunk
	 */
	public NegotiationEvent(NegotiationChunk chunk) {
		this(true, chunk);
	}

	/**
	 * The Constructor.
	 *
	 * @param accepted
	 *          the accepted
	 * @param chunk
	 *          the chunk
	 */
	public NegotiationEvent(boolean accepted, NegotiationChunk chunk) {
		this.accepted = accepted;
		this.chunk = chunk;
	}

	/**
	 * Checks if is accepted.
	 *
	 * @return true, if checks if is accepted
	 */
	public boolean isAccepted() {
		return accepted;
	}

	/**
	 * Gets the chunk.
	 *
	 * @return the chunk
	 */
	public NegotiationChunk getChunk() {
		return chunk;
	}

}
