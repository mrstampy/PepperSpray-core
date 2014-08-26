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

import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.pprspray.core.receiver.event.ReceiverEvent;
import com.github.mrstampy.pprspray.core.receiver.event.ReceiverEventBus;
import com.google.common.eventbus.Subscribe;

// TODO: Auto-generated Javadoc
/**
 * The Class DefaultAudioProcessor.
 */
public class DefaultAudioProcessor {
	private static final Logger log = LoggerFactory.getLogger(DefaultAudioProcessor.class);

	private AudioFormat audioFormat;
	private Mixer.Info mixerInfo;
	private SourceDataLine dataLine;
	private int mediaHash;

	private AtomicBoolean open = new AtomicBoolean(false);

	/**
	 * The Constructor.
	 *
	 * @param mediaHash
	 *          the media hash
	 * @param audioFormat
	 *          the audio format
	 * @param mixerInfo
	 *          the mixer info
	 */
	public DefaultAudioProcessor(int mediaHash, AudioFormat audioFormat, Mixer.Info mixerInfo) {
		setMediaHash(mediaHash);
		setAudioFormat(audioFormat);
		setMixerInfo(mixerInfo);

		ReceiverEventBus.register(this);
		AudioEventBus.register(this);
	}

	/**
	 * Receiver event.
	 *
	 * @param event
	 *          the event
	 */
	@Subscribe
	public void receiverEvent(ReceiverEvent event) {
		if (!event.isApplicable(getMediaHash())) return;

		switch (event.getType()) {
		case CLOSE:
			close();
			break;
		case DESTROY:
			destroy();
			break;
		case OPEN:
			open();
			break;
		default:
			break;
		}
	}

	/**
	 * Audio event.
	 *
	 * @param event
	 *          the event
	 */
	@Subscribe
	public void audioEvent(AudioEvent event) {
		if (!event.isApplicable(getMediaHash())) return;

		if (!isOpen()) open();

		try {
			dataLine.write(event.getChunk(), 0, event.getChunk().length);
		} catch (Exception e) {
			log.error("Unexpected exception", e);
		}
	}

	/**
	 * Open.
	 */
	public void open() {
		if (isOpen()) return;
		
		try {
			close();

			dataLine = AudioSystem.getSourceDataLine(getAudioFormat(), getMixerInfo());

			dataLine.addLineListener(new LineListener() {

				@Override
				public void update(LineEvent event) {
					open.set(event.getType() == LineEvent.Type.START);
				}
			});
			
			dataLine.open(getAudioFormat());
		} catch (Exception e) {
			log.error("Could not open", e);
		}
	}

	/**
	 * Checks if is open.
	 *
	 * @return true, if checks if is open
	 */
	public boolean isOpen() {
		return open.get();
	}

	/**
	 * Close.
	 */
	public void close() {
		if (!isOpen()) return;
		if (dataLine != null) dataLine.close();
	}

	/**
	 * Destroy.
	 */
	public void destroy() {
		close();

		AudioEventBus.unregister(this);
		ReceiverEventBus.unregister(this);
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

	/**
	 * Gets the media hash.
	 *
	 * @return the media hash
	 */
	public int getMediaHash() {
		return mediaHash;
	}

	/**
	 * Sets the media hash.
	 *
	 * @param mediaHash
	 *          the media hash
	 */
	protected void setMediaHash(int mediaHash) {
		this.mediaHash = mediaHash;
	}

}
