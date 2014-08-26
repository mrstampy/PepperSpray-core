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

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class DefaultJsonChunk.
 */
public class DefaultJsonChunk extends DefaultTextChunk {

	private static final long serialVersionUID = 1173299761378273183L;
	private Class<?> jsonClass;

	/**
	 * The Constructor.
	 *
	 * @param message
	 *          the message
	 */
	public DefaultJsonChunk(byte[] message) {
		this(message, NoJsonClass.class);
	}

	/**
	 * The Constructor.
	 *
	 * @param message
	 *          the message
	 * @param jsonClass
	 *          the json class
	 */
	public DefaultJsonChunk(byte[] message, Class<?> jsonClass) {
		super(message);
		setJsonClass(jsonClass);
	}

	/**
	 * Returns true if {@link #getJsonClass()} returns a class object of which the
	 * JSON in {@link #getData()} is a representation.
	 *
	 * @return true, if checks for json class
	 */
	public boolean hasJsonClass() {
		return !NoJsonClass.class.equals(getJsonClass());
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
	}

	/**
	 * Object whose class signals that the JSON in
	 * {@link DefaultJsonChunk#getData()} is to be dealt with as a string only.
	 * 
	 * @author burton
	 * @see DefaultJsonChunk#hasJsonClass()
	 *
	 */
	public static final class NoJsonClass implements Serializable {

		private static final long serialVersionUID = 2573394575308524643L;

	}

}
