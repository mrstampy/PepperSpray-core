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
package com.github.mrstampy.pprspray.core.streamer.binary;

import java.util.concurrent.ArrayBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.pprspray.core.streamer.AbstractMediaStreamer;
import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.footer.MediaFooter;
import com.github.mrstampy.pprspray.core.streamer.footer.MediaFooterMessage;

// TODO: Auto-generated Javadoc
/**
 * The Class BinaryStreamer.
 */
public class BinaryStreamer extends AbstractMediaStreamer {
	private static final Logger log = LoggerFactory.getLogger(BinaryStreamer.class);

	private static final int DEFAULT_BINARY_PIPE_SIZE = 1000 * 1024;

	private ArrayBlockingQueue<byte[]> queue = new ArrayBlockingQueue<byte[]>(100);

	/**
	 * The Constructor.
	 */
	public BinaryStreamer() {
		this(DEFAULT_BINARY_PIPE_SIZE);
	}
	
	/**
	 * The Constructor.
	 *
	 * @param defaultPipeSize the default pipe size
	 */
	public BinaryStreamer(int defaultPipeSize) {
		super(defaultPipeSize);
		initDefaultChunkProcessorAndFooter();
		setAckRequired(true);
	}

	/**
	 * Stream.
	 *
	 * @param bytes the bytes
	 */
	public void stream(byte[] bytes) {
		if (bytes == null || bytes.length == 0) return;

		if (!isStreaming()) start();

		add(bytes);
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.pprspray.core.streamer.AbstractMediaStreamer#isStreamable()
	 */
	@Override
	protected boolean isStreamable() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.pprspray.core.streamer.AbstractMediaStreamer#getBytes()
	 */
	@Override
	protected byte[] getBytes() {
		byte[] b = take();

		return b;
	}

	/**
	 * Adds the.
	 *
	 * @param bytes the bytes
	 */
	protected void add(byte[] bytes) {
		if (remainingCapacity() == 0) take();
		queue.add(bytes);
	}

	/**
	 * Remaining capacity.
	 *
	 * @return the int
	 */
	public int remainingCapacity() {
		return queue.remainingCapacity();
	}

	/**
	 * Take.
	 *
	 * @return the byte[]
	 */
	protected byte[] take() {
		try {
			return queue.take();
		} catch (InterruptedException e) {
			log.error("Unexpected exception", e);
		}

		return null;
	}

	private void initDefaultChunkProcessorAndFooter() {
		DefaultBinaryChunkProcessor dbcp = new DefaultBinaryChunkProcessor();
		MediaFooterMessage mfm = new MediaFooterMessage(MediaStreamType.BINARY, dbcp.getMediaHash());

		setMediaChunkProcessor(dbcp);
		setMediaFooter(new MediaFooter(mfm));
	}

}
