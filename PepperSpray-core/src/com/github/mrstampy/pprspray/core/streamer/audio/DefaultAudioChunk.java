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
package com.github.mrstampy.pprspray.core.streamer.audio;

import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunk;
import com.github.mrstampy.pprspray.core.streamer.util.MediaStreamerUtils;

/**
 * The Class DefaultAudioChunk represents a part of a discrete chunk of audio
 * data, and may be up to 2 kilobytes in size by default.
 */
public class DefaultAudioChunk extends AbstractMediaChunk {

	private static final long serialVersionUID = -955150605214590719L;

	/**
	 * Only create one of these when
	 * {@link MediaStreamerUtils#isDefaultAudioChunk(byte[])} returns true. Or
	 * not. Whatever.
	 *
	 * @param message
	 *          the message
	 */
	public DefaultAudioChunk(byte[] message) {
		super(message, MediaStreamType.AUDIO);
	}

}
