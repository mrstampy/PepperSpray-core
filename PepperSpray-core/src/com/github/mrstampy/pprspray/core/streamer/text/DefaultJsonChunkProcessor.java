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
 * The Class DefaultJsonChunkProcessor.
 */
public class DefaultJsonChunkProcessor extends AbstractMetaTextChunkProcessor {

	/** The Constant JSON_KEY. */
	public static final String JSON_KEY = "JSON:";

	/** The Constant JSON_KEY_BYTES. */
	public static final byte[] JSON_KEY_BYTES = JSON_KEY.getBytes();

	/**
	 * The Constructor.
	 */
	public DefaultJsonChunkProcessor() {
		super(JSON_KEY_BYTES);
	}

	/**
	 * The Constructor.
	 *
	 * @param jsonClass
	 *          the json class
	 */
	public DefaultJsonChunkProcessor(Class<?> jsonClass) {
		super(JSON_KEY_BYTES, jsonClass);
	}

}
