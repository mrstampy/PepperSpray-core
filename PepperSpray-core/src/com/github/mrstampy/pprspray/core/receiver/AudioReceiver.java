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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
import com.github.mrstampy.pprspray.core.streamer.footer.MediaFooterMessage;

// TODO: Auto-generated Javadoc
/**
 * The Class AudioReceiver.
 */
public class AudioReceiver extends AbstractMediaReceiver<DefaultAudioChunk> {
	private static final Logger log = LoggerFactory.getLogger(AudioReceiver.class);

	private AudioFormat audioFormat;
	private Mixer.Info mixerInfo;
	private SourceDataLine dataLine;

	private AtomicBoolean open = new AtomicBoolean(false);

	private ConcurrentSkipListSet<DefaultAudioChunk> chunks = new ConcurrentSkipListSet<>();

	private Scheduler svc = Schedulers.from(Executors.newSingleThreadExecutor());
	private Subscription sub;

	private AtomicInteger errorCount = new AtomicInteger(0);

	private Lock lock = new ReentrantLock();

	private CountDownLatch latch;

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
		super(MediaStreamType.AUDIO, mediaHash);
		setAudioFormat(audioFormat);
		setMixerInfo(mixerInfo);
		init();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.receiver.AbstractMediaReceiver#receiveImpl
	 * (com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunk)
	 */
	protected void receiveImpl(DefaultAudioChunk chunk) {
		lock.lock();
		try {
			if (!isOpen()) open();
			chunks.add(chunk);
		} finally {
			lock.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.receiver.AbstractMediaReceiver#
	 * endOfMessageImpl
	 * (com.github.mrstampy.pprspray.core.streamer.footer.MediaFooterMessage)
	 */
	protected void endOfMessageImpl(MediaFooterMessage eom) {
		latch.countDown();
		latch = new CountDownLatch(1);
	}

	/**
	 * Clear.
	 */
	public void clear() {
		chunks.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.receiver.AbstractMediaReceiver#open()
	 */
	public void open() {
		if (isOpen()) return;

		try {
			dataLine.open();
			createWriterService();
		} catch (LineUnavailableException e) {
			int count = errorCount.incrementAndGet();
			if (count < 2) {
				retry();
			} else {
				log.error("Unexpected exception", e);
				throw new IllegalStateException("Cannot open " + getAudioFormat() + ", " + getMediaHash(), e);
			}
		}
	}

	private void retry() {
		log.warn("Line unavailable, initializing");

		try {
			init();
			open();
		} catch (LineUnavailableException e) {
			log.error("Unexpected exception", e);
			throw new IllegalStateException("Cannot open " + getAudioFormat() + ", " + getMediaHash(), e);
		} finally {
			errorCount.set(0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.receiver.AbstractMediaReceiver#isOpen()
	 */
	@Override
	public boolean isOpen() {
		return open.get();
	}

	private void createWriterService() {
		latch = new CountDownLatch(1);
		sub = svc.createWorker().schedule(new Action0() {

			@Override
			public void call() {
				while (isOpen()) {
					awaitWrite();
				}

				sub.unsubscribe();
			}
		});
	}

	private void awaitWrite() {
		boolean ok = KiSyUtils.await(latch, 100, TimeUnit.MILLISECONDS);

		if (!ok) return;

		if (chunks.isEmpty()) return;

		lock.lock();
		DefaultAudioChunk[] array = null;
		try {
			array = chunks.toArray(new DefaultAudioChunk[] {});
			clear();
		} finally {
			lock.unlock();
		}

		byte[] transformed = rehydrateAndTransform(array);

		write(transformed);
	}

	private void write(byte[] data) {
		try {
			dataLine.write(data, 0, data.length);
		} catch (Exception e) {
			log.error("Cannot write audio, closing", e);
			close();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.receiver.AbstractMediaReceiver#close()
	 */
	public void close() {
		if (!isOpen()) return;
		dataLine.close();
		if (sub != null) sub.unsubscribe();
		if (latch != null) latch.countDown();
	}

	/**
	 * Inits the.
	 *
	 * @throws LineUnavailableException
	 *           the line unavailable exception
	 */
	public void init() throws LineUnavailableException {
		close();

		dataLine = AudioSystem.getSourceDataLine(getAudioFormat(), getMixerInfo());

		dataLine.addLineListener(new LineListener() {

			@Override
			public void update(LineEvent event) {
				open.set(event.getType() == LineEvent.Type.START);
			}
		});

		open.set(dataLine.isActive());
		if (isOpen()) createWriterService();
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
}
