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

import java.net.InetSocketAddress;
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

import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;
import com.github.mrstampy.kitchensync.util.KiSyUtils;
import com.github.mrstampy.pprspray.core.receiver.audio.DefaultAudioProcessor;
import com.github.mrstampy.pprspray.core.streamer.AbstractMediaStreamer;
import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.footer.MediaFooter;

/**
 * The Class AudioStreamer streams raw data from the audio subsystem to a remote
 * connection. It is to be considered a reference implementation. Audio streamer
 * implementations should stream a particular encoded audio stream ie. mp3,
 * opus, ogg vorbis.
 * 
 * @see DefaultAudioProcessor
 */
public class AudioStreamer extends AbstractMediaStreamer {
	private static final Logger log = LoggerFactory.getLogger(AudioStreamer.class);

	private static final int DEFAULT_AUDIO_CHUNK_SIZE = 1024 * 10;
	private static final int DEFAULT_AUDIO_PIPE_SIZE = 1024 * 2000;

	private AudioFormat audioFormat;
	private Mixer.Info mixerInfo;
	private TargetDataLine dataLine;

	private int audioChunkSize;

	private ByteBuf buf = Unpooled.buffer(10240, 1000 * 10240);
	private AtomicBoolean streamable = new AtomicBoolean(false);

	private Scheduler scheduler = Schedulers.from(Executors.newFixedThreadPool(1));
	private Subscription audioSub;

	private AudioTransformer transformer;

	/**
	 * The Constructor.
	 *
	 * @param channel
	 *          the channel
	 * @param destination
	 *          the destination
	 * @param audioFormat
	 *          the audio format
	 * @param mixerInfo
	 *          the mixer info
	 * @throws LineUnavailableException
	 *           the line unavailable exception
	 */
	public AudioStreamer(KiSyChannel channel, InetSocketAddress destination, AudioFormat audioFormat, Mixer.Info mixerInfo)
			throws LineUnavailableException {
		super(DEFAULT_AUDIO_PIPE_SIZE, channel, destination, MediaStreamType.AUDIO);
		init(audioFormat, mixerInfo);

		initDefaultChunkProcessorAndFooter();
		setTransformer(new DefaultAudioTransformer());
		setAudioChunkSize(DEFAULT_AUDIO_CHUNK_SIZE);
	}

	/**
	 * The Constructor.
	 *
	 * @param channel
	 *          the channel
	 * @param destination
	 *          the destination
	 * @param audioFormat
	 *          the audio format
	 * @throws LineUnavailableException
	 *           the line unavailable exception
	 */
	public AudioStreamer(KiSyChannel channel, InetSocketAddress destination, AudioFormat audioFormat)
			throws LineUnavailableException {
		this(channel, destination, audioFormat, null);
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
	protected void start() {
		buf.clear();
		buf.readerIndex(0);
		buf.writerIndex(0);

		try {
			dataLine.open();
		} catch (LineUnavailableException e) {
			log.error("Unexpected exception", e);
			throw new IllegalStateException("Cannot open line", e);
		}

		dataLine.start();
		log.debug("Starting audio streaming for format {}, info {}", audioFormat, mixerInfo);
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
	}

	private void unsubscribe(Subscription sub) {
		if (sub != null) sub.unsubscribe();
	}

	private void startAudioReading() {
		audioSub = scheduler.createWorker().schedulePeriodically(new Action0() {

			@Override
			public void call() {
				if (isStreaming()) readAudio();
			}
		}, 0, 10, TimeUnit.MILLISECONDS);
	}

	private void readAudio() {
		int available = dataLine.available();

		if (available < 0) stop();
		if (available == 0) return;

		discardSomeIfFull(available);

		byte[] b = new byte[available];

		dataLine.read(b, 0, b.length);
		buf.writeBytes(b);
	}

	private void discardSomeIfFull(int available) {
		try {
			int remaining = buf.writableBytes() - available;
			if (remaining >= 0) return;

			buf.readerIndex(remaining);
			buf.discardReadBytes();
		} catch (IndexOutOfBoundsException ignore) {
		} catch (Exception e) {
			log.error("Unexpected exception", e);
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
		awaitBytes();

		if (!isStreaming()) return null;

		int size = getAudioChunkSize();

		ByteBuf b = buf.readBytes(size);

		buf.discardSomeReadBytes();

		if (getTransformer() == null) throw new IllegalArgumentException("Transformer cannot be null");

		return getTransformer().transform(b);
	}

	private void awaitBytes() {
		int writer = buf.writerIndex();
		int reader = buf.readerIndex();
		while (isStreaming() && writer - reader < getAudioChunkSize()) {
			KiSyUtils.snooze(2);
			writer = buf.writerIndex();
			reader = buf.readerIndex();
		}
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
	}

	private void initDefaultChunkProcessorAndFooter() {
		DefaultAudioChunkProcessor dacp = new DefaultAudioChunkProcessor(getAudioFormat());

		setMediaChunkProcessor(dacp);

		setMediaFooter(new MediaFooter(MediaStreamType.AUDIO, getMediaHash()));
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
