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

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Scheduler;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import com.github.mrstampy.pprspray.core.receiver.AbstractMediaReceiver;
import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.audio.DefaultAudioChunk;
import com.github.mrstampy.pprspray.core.streamer.footer.MediaFooterMessage;

// TODO: Auto-generated Javadoc
/**
 * The Class AudioReceiver.
 */
public class AudioReceiver extends AbstractMediaReceiver<DefaultAudioChunk> {
	private static final Logger log = LoggerFactory.getLogger(AudioReceiver.class);

	private ConcurrentSkipListSet<DefaultAudioChunk> chunks = new ConcurrentSkipListSet<>();

	private Scheduler svc = Schedulers.from(Executors.newSingleThreadExecutor());

	private Lock lock = new ReentrantLock();

	/**
	 * The Constructor.
	 *
	 * @param mediaHash
	 *          the media hash
	 */
	public AudioReceiver(int mediaHash) {
		super(MediaStreamType.AUDIO, mediaHash);
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
		if (chunks.isEmpty()) return;

		final DefaultAudioChunk[] array = getCurrentAndClear();

		svc.createWorker().schedule(new Action0() {

			@Override
			public void call() {
				write(array);
			}
		});
	}

	private void write(DefaultAudioChunk[] array) {
		try {
			byte[] b = rehydrateAndTransform(array);

			if (hasTransformed(b)) AudioEventBus.post(new AudioEvent(getMediaHash(), b));
		} catch (Exception e) {
			log.error("Unexpected exception, closing", e);
			close();
		}
	}

	private boolean hasTransformed(byte[] b) {
		return b != null && b.length > 0;
	}

	private DefaultAudioChunk[] getCurrentAndClear() {
		lock.lock();
		try {
			DefaultAudioChunk[] array = chunks.toArray(new DefaultAudioChunk[] {});
			clear();
			return array;
		} finally {
			lock.unlock();
		}
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

		setOpen(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.receiver.AbstractMediaReceiver#close()
	 */
	public void close() {
		if (!isOpen()) return;

		setOpen(false);
	}
}
