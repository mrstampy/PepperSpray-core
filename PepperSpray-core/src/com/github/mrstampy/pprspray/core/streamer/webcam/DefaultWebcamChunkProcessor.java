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
package com.github.mrstampy.pprspray.core.streamer.webcam;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunkProcessor;
import com.github.sarxos.webcam.Webcam;

// TODO: Auto-generated Javadoc
/**
 * The Class DefaultWebcamChunkProcessor.
 */
public class DefaultWebcamChunkProcessor extends AbstractMediaChunkProcessor {

	private Webcam webcam;

	/**
	 * The Constructor.
	 *
	 * @param webcam
	 *          the webcam
	 */
	public DefaultWebcamChunkProcessor(Webcam webcam) {
		super(MediaStreamType.VIDEO);
		this.webcam = webcam;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunkProcessor
	 * #createMediaHash()
	 */
	@Override
	protected int createMediaHash() {
		//@formatter:off
		return new HashCodeBuilder()
				.append(webcam)
				.toHashCode();
		//@formatter:on
	}

}
