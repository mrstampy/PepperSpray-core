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

import com.github.mrstampy.pprspray.core.streamer.util.MediaStreamerUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class DefaultXmlChunk.
 */
public class DefaultXmlChunk extends DefaultTextChunk {

	private static final long serialVersionUID = 1173299761378273183L;
	private int xmlClassNameHash;

	/**
	 * The Constructor.
	 *
	 * @param message
	 *          the message
	 */
	public DefaultXmlChunk(byte[] message) {
		super(message);

		extractXmlClassNameHash(message);
	}

	private void extractXmlClassNameHash(byte[] message) {
		setXmlClassNameHash(MediaStreamerUtils.getXmlClassHash(message));
	}

	/**
	 * Returns true if {@link #getXmlClassNameHash()} references a class object of
	 * which the XML in {@link #getData()} is a representation.
	 *
	 * @return true, if checks for xml class
	 * @see Class#getName()
	 */
	public boolean hasXmlClass() {
		return NoXmlClass.class.getName().hashCode() != getXmlClassNameHash();
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
	 * @param xmlClassNameHash
	 *          the xml class name hash
	 */
	public void setXmlClassNameHash(int xmlClassNameHash) {
		this.xmlClassNameHash = xmlClassNameHash;
	}

	/**
	 * Object whose class signals that the JSON in
	 * {@link DefaultXmlChunk#getData()} is to be dealt with as a string only.
	 * 
	 * @author burton
	 * @see DefaultXmlChunk#hasXmlClass()
	 *
	 */
	public static final class NoXmlClass implements Serializable {

		private static final long serialVersionUID = -1903763731003959794L;

	}

}
