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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import com.github.mrstampy.kitchensync.util.KiSyUtils;
import com.github.mrstampy.pprspray.core.streamer.AbstractMediaStreamer;
import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.footer.MediaFooter;
import com.github.mrstampy.pprspray.core.streamer.footer.MediaFooterMessage;

// TODO: Auto-generated Javadoc
/**
 * The Class AudioStreamer.
 */
public class AudioStreamer extends AbstractMediaStreamer {
	private static final Logger log = LoggerFactory.getLogger(AudioStreamer.class);

	private static final int DEFAULT_AUDIO_CHUNK_SIZE = 10240;
	private static final int DEFAULT_AUDIO_PIPE_SIZE = 1024 * 2000;

	private AudioFormat audioFormat;
	private Mixer.Info mixerInfo;
	private TargetDataLine dataLine;

	private int audioChunkSize = DEFAULT_AUDIO_CHUNK_SIZE;

	private ArrayBlockingQueue<Byte> queue;
	private AtomicBoolean dataReady = new AtomicBoolean(false);
	private AtomicBoolean streamable = new AtomicBoolean(false);

	private Scheduler scheduler = Schedulers.from(Executors.newFixedThreadPool(2));
	private Subscription audioSub;
	private Subscription monitorSub;

	private AudioTransformer transformer;

	/**
	 * The Constructor.
	 *
	 * @param audioFormat
	 *          the audio format
	 * @param mixerInfo
	 *          the mixer info
	 * @throws LineUnavailableException
	 *           the line unavailable exception
	 */
	public AudioStreamer(AudioFormat audioFormat, Mixer.Info mixerInfo) throws LineUnavailableException {
		super(DEFAULT_AUDIO_PIPE_SIZE);
		init(audioFormat, mixerInfo);

		initQueue();
		initDefaultChunkProcessorAndFooter();
		setTransformer(new DefaultAudioTransformer());
	}

	/**
	 * Inits the.
	 *
	 * @param audioFormat
	 *          the audio format
	 * @param mixerInfo
	 *          the mixer info
	 * @throws LineUnavailableException
	 *           the line unavailable exception
	 */
	public void init(AudioFormat audioFormat, Mixer.Info mixerInfo) throws LineUnavailableException {
		if (isStreaming()) throw new IllegalStateException("Cannot initialize when streaming");

		this.audioFormat = audioFormat;
		this.mixerInfo = mixerInfo;

		dataLine = AudioSystem.getTargetDataLine(audioFormat, mixerInfo);

		dataLine.addLineListener(new LineListener() {

			@Override
			public void update(LineEvent event) {
				streamable.set(event.getType() == LineEvent.Type.START);

				if (isStreaming() && !isStreamable()) stop();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.streamer.AbstractMediaStreamer#start()
	 */
	public void start() {
		dataLine.start();
		log.debug("Starting audio streaming for format {}, info {}", audioFormat, mixerInfo);
		startMonitoring();
		startAudioReading();
		super.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.streamer.AbstractMediaStreamer#stop()
	 */
	public void stop() {
		dataLine.stop();
		log.debug("Stopping audio streaming for format {}, info {}", audioFormat, mixerInfo);
		super.stop();
		unsubscribe(audioSub);
		unsubscribe(monitorSub);
	}

	private void unsubscribe(Subscription sub) {
		if (sub != null) sub.unsubscribe();
	}

	private void startMonitoring() {
		monitorSub = scheduler.createWorker().schedulePeriodically(new Action0() {

			@Override
			public void call() {
				setDataReady();
			}
		}, 0, 3, TimeUnit.MILLISECONDS);
	}

	private void setDataReady() {
		boolean ready = queue.size() >= getAudioChunkSize();

		if (ready == dataReady.get()) return;

		dataReady.set(ready);
	}

	private void startAudioReading() {
		audioSub = scheduler.createWorker().schedulePeriodically(new Action0() {

			@Override
			public void call() {
				if (isStreamable()) readAudio();
			}
		}, 0, 10, TimeUnit.MILLISECONDS);
	}

	private void readAudio() {
		int available = dataLine.available();

		if (available < 0) stop();
		if (available == 0) return;

		byte[] b = new byte[available];

		dataLine.read(b, 0, b.length);

		for (byte bite : b) {
			queue.add(bite);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.streamer.AbstractMediaStreamer#isStreamable
	 * ()
	 */
	@Override
	protected boolean isStreamable() {
		return streamable.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.streamer.AbstractMediaStreamer#getBytes()
	 */
	@Override
	protected byte[] getBytes() {
		// see startMonitoring, setDataReady
		while (!dataReady.get() && isStreaming()) {
			KiSyUtils.snooze(2);
		}

		// If the subscription is cancelled, will
		// we ever reach here?
		if (!isStreaming()) return null;

		int size = getAudioChunkSize();

		ByteBuf buf = Unpooled.buffer(size);

		for (int i = 0; i < size; i++) {
			try {
				buf.writeByte(queue.take());
			} catch (InterruptedException e) {
				log.error("Unexpected exception", e);
			}
		}

		if (getTransformer() == null) throw new IllegalArgumentException("Transformer cannot be null");

		return getTransformer().transform(buf);
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
	 * Gets the mixer info.
	 *
	 * @return the mixer info
	 */
	public Mixer.Info getMixerInfo() {
		return mixerInfo;
	}

	/**
	 * Gets the audio chunk size.
	 *
	 * @return the audio chunk size
	 */
	public int getAudioChunkSize() {
		return audioChunkSize;
	}

	/**
	 * Sets the audio chunk size.
	 *
	 * @param audioChunkSize
	 *          the audio chunk size
	 */
	public void setAudioChunkSize(int audioChunkSize) {
		if (isStreaming()) throw new IllegalStateException("Cannot set audio chunk size when streaming");
		if (audioChunkSize <= 0) throw new IllegalArgumentException("Audio chunk size must be > 0");

		this.audioChunkSize = audioChunkSize;

		initQueue();
	}

	private void initQueue() {
		queue = new ArrayBlockingQueue<>(100 * getAudioChunkSize());
	}

	private void initDefaultChunkProcessorAndFooter() {
		DefaultAudioChunkProcessor dacp = new DefaultAudioChunkProcessor(getAudioFormat());

		setMediaChunkProcessor(dacp);

		MediaFooterMessage mfm = new MediaFooterMessage(MediaStreamType.AUDIO, dacp.getMediaHash());

		setMediaFooter(new MediaFooter(mfm));
	}

	/**
	 * Gets the transformer.
	 *
	 * @return the transformer
	 */
	public AudioTransformer getTransformer() {
		return transformer;
	}

	/**
	 * Sets the transformer.
	 *
	 * @param transformer
	 *          the transformer
	 */
	public void setTransformer(AudioTransformer transformer) {
		this.transformer = transformer;
	}

}
