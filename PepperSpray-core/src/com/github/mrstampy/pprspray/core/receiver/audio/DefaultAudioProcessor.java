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
package com.github.mrstampy.pprspray.core.receiver.audio;

import java.net.InetSocketAddress;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.pprspray.core.receiver.AbstractMediaProcessor;
import com.github.mrstampy.pprspray.core.receiver.MediaEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class DefaultAudioProcessor.
 */
public class DefaultAudioProcessor extends AbstractMediaProcessor {
	private static final Logger log = LoggerFactory.getLogger(DefaultAudioProcessor.class);

	private AudioFormat audioFormat;
	private Mixer.Info mixerInfo;
	private SourceDataLine dataLine;

	/**
	 * The Constructor.
	 *
	 * @param mediaHash
	 *          the media hash
	 * @param local
	 *          the local
	 * @param remote
	 *          the remote
	 * @param audioFormat
	 *          the audio format
	 * @param mixerInfo
	 *          the mixer info
	 */
	public DefaultAudioProcessor(int mediaHash, InetSocketAddress local, InetSocketAddress remote,
			AudioFormat audioFormat, Mixer.Info mixerInfo) {
		super(mediaHash, local, remote);

		setAudioFormat(audioFormat);
		setMixerInfo(mixerInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.receiver.AbstractMediaProcessor#
	 * mediaEventImpl(com.github.mrstampy.pprspray.core.receiver.MediaEvent)
	 */
	@Override
	protected void mediaEventImpl(MediaEvent event) throws Exception {
		if (!isOpen()) open();

		try {
			dataLine.write(event.getProcessed(), 0, event.getProcessed().length);
		} catch (Exception e) {
			log.error("Unexpected exception", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.receiver.AbstractMediaProcessor#openImpl
	 * ()
	 */
	@Override
	protected boolean openImpl() {
		try {
			closeDataLine();

			dataLine = AudioSystem.getSourceDataLine(getAudioFormat(), getMixerInfo());

			dataLine.addLineListener(new LineListener() {

				@Override
				public void update(LineEvent event) {
					setOpen(event.getType() == LineEvent.Type.START);
				}
			});

			dataLine.open(getAudioFormat());

			return true;
		} catch (Exception e) {
			log.error("Could not open audio processor for {}", getMediaHash(), e);
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.receiver.AbstractMediaProcessor#closeImpl
	 * ()
	 */
	@Override
	protected boolean closeImpl() {
		closeDataLine();
		return true;
	}

	private void closeDataLine() {
		if (dataLine != null) dataLine.close();
	}

	/**
	 * Gets the audio format.
	 *
	 * @return the audio format
	 */
	public AudioFormat getAudioFormat() {
		return audioFormat;
	}

	/**
	 * Sets the audio format.
	 *
	 * @param audioFormat
	 *          the audio format
	 */
	protected void setAudioFormat(AudioFormat audioFormat) {
		this.audioFormat = audioFormat;
	}

	/**
	 * Gets the mixer info.
	 *
	 * @return the mixer info
	 */
	public Mixer.Info getMixerInfo() {
		return mixerInfo;
	}

	/**
	 * Sets the mixer info.
	 *
	 * @param mixerInfo
	 *          the mixer info
	 */
	protected void setMixerInfo(Mixer.Info mixerInfo) {
		this.mixerInfo = mixerInfo;
	}

}
