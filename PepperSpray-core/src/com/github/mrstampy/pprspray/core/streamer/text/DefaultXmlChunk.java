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

/**
 * The Class DefaultXmlChunk.
 */
public class DefaultXmlChunk extends AbstractMetaTextChunk {

	private static final long serialVersionUID = 7824045936232960070L;

	/**
	 * The Constructor.
	 *
	 * @param message
	 *          the message
	 */
	public DefaultXmlChunk(byte[] message) {
		super(message, DefaultXmlChunkProcessor.XML_KEY_BYTES);
	}

}
