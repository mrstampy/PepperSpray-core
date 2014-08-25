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

import javax.sound.sampled.AudioFormat;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunkProcessor;

// TODO: Auto-generated Javadoc
/**
 * The Class DefaultAudioChunkProcessor.
 */
public class DefaultAudioChunkProcessor extends AbstractMediaChunkProcessor {

	private AudioFormat audioFormat;

	/**
	 * The Constructor.
	 *
	 * @param audioFormat the audio format
	 */
	public DefaultAudioChunkProcessor(AudioFormat audioFormat) {
		super(MediaStreamType.AUDIO);

		if (audioFormat == null) throw new IllegalArgumentException("AudioFormat cannot be null");

		this.audioFormat = audioFormat;
	}

	/**
	 * Gets the audio format.
	 *
	 * @return the audio format
	 */
	public AudioFormat getAudioFormat() {
		return audioFormat;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunkProcessor#createMediaHash()
	 */
	@Override
	protected int createMediaHash() {
		//@formatter:off
		return new HashCodeBuilder()
				.append(audioFormat.getChannels())
				.append(audioFormat.getFrameRate())
				.append(audioFormat.getFrameSize())
				.append(audioFormat.getSampleRate())
				.append(audioFormat.getSampleSizeInBits())
				.append(audioFormat.getEncoding())
				.toHashCode();
		//@formatter:on
	}
}
