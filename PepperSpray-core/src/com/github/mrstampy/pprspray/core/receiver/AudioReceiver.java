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
package com.github.mrstampy.pprspray.core.receiver;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import com.github.mrstampy.kitchensync.util.KiSyUtils;
import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.audio.DefaultAudioChunk;
import com.github.mrstampy.pprspray.core.streamer.chunk.event.ChunkEventBus;
import com.github.mrstampy.pprspray.core.streamer.footer.MediaFooterMessage;
import com.google.common.eventbus.Subscribe;

// TODO: Auto-generated Javadoc
/**
 * The Class AudioReceiver.
 */
public class AudioReceiver {
	private static final Logger log = LoggerFactory.getLogger(AudioReceiver.class);

	private AudioFormat audioFormat;
	private Mixer.Info mixerInfo;
	private SourceDataLine dataLine;

	private AtomicBoolean open = new AtomicBoolean(false);

	private int mediaHash;

	private ConcurrentSkipListSet<DefaultAudioChunk> chunks = new ConcurrentSkipListSet<>();

	private Scheduler svc = Schedulers.from(Executors.newSingleThreadExecutor());
	private Subscription sub;

	/**
	 * The Constructor.
	 *
	 * @param audioFormat
	 *          the audio format
	 * @param mixerInfo
	 *          the mixer info
	 * @param mediaHash
	 *          the media hash
	 * @throws LineUnavailableException
	 *           the line unavailable exception
	 */
	public AudioReceiver(AudioFormat audioFormat, Mixer.Info mixerInfo, int mediaHash) throws LineUnavailableException {
		setAudioFormat(audioFormat);
		setMixerInfo(mixerInfo);
		setMediaHash(mediaHash);
		init();
	}

	/**
	 * Receive.
	 *
	 * @param chunk
	 *          the chunk
	 * @see ChunkEventBus#register(Object)
	 */
	@Subscribe
	public void receive(DefaultAudioChunk chunk) {
		if (!chunk.isApplicable(getMediaHash())) return;
		if (!open.get()) return;

		chunks.add(chunk);
	}

	/**
	 * End of message.
	 *
	 * @param eom
	 *          the eom
	 * @see ChunkEventBus#register(Object)
	 */
	@Subscribe
	public void endOfMessage(MediaFooterMessage eom) {
		if (!eom.isApplicable(MediaStreamType.AUDIO, getMediaHash())) return;

		close();
	}

	/**
	 * Clear.
	 */
	public void clear() {
		chunks.clear();
	}

	/**
	 * Open.
	 *
	 * @throws LineUnavailableException
	 *           the line unavailable exception
	 */
	public void open() throws LineUnavailableException {
		if (open.get()) return;

		dataLine.open();
		createWriterService();
	}

	private void createWriterService() {
		sub = svc.createWorker().schedule(new Action0() {

			@Override
			public void call() {
				while (open.get()) {
					while (open.get() && chunks.size() <= 10) {
						KiSyUtils.snooze(5);
					}

					while (open.get() && chunks.size() > 10) {
						write(chunks.pollFirst());
					}
				}

				sub.unsubscribe();
			}
		});
	}

	private void write(DefaultAudioChunk chunk) {
		try {
			dataLine.write(chunk.getData(), 0, chunk.getData().length);
		} catch (Exception e) {
			log.error("Cannot write audio, closing", e);
			close();
		}
	}

	/**
	 * Close.
	 */
	public void close() {
		if (!open.get()) return;
		dataLine.close();
	}

	/**
	 * Destroy.
	 * 
	 * @see ChunkEventBus#unregister(Object)
	 */
	public void destroy() {
		close();
		ChunkEventBus.unregister(this);
	}

	/**
	 * Inits the.
	 *
	 * @throws LineUnavailableException
	 *           the line unavailable exception
	 */
	public void init() throws LineUnavailableException {
		dataLine = AudioSystem.getSourceDataLine(getAudioFormat(), getMixerInfo());

		dataLine.addLineListener(new LineListener() {

			@Override
			public void update(LineEvent event) {
				open.set(event.getType() == LineEvent.Type.START);
			}
		});

		open.set(dataLine.isActive());
		if (open.get()) createWriterService();
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
	public void setAudioFormat(AudioFormat audioFormat) {
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
	public void setMixerInfo(Mixer.Info mixerInfo) {
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
	public void setMediaHash(int mediaHash) {
		this.mediaHash = mediaHash;
	}
}
