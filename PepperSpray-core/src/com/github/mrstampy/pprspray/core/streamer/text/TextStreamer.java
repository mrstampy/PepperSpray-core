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
package com.github.mrstampy.pprspray.core.streamer.text;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Scheduler;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;
import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.binary.BinaryStreamer;
import com.github.mrstampy.pprspray.core.streamer.footer.MediaFooter;
import com.github.mrstampy.pprspray.core.streamer.footer.MediaFooterMessage;

// TODO: Auto-generated Javadoc
/**
 * The Class TextStreamer.
 */
public class TextStreamer extends BinaryStreamer {
	private static final Logger log = LoggerFactory.getLogger(TextStreamer.class);

	private static final int DEFAULT_TEXT_PIPE_SIZE = 1024 * 1000;

	private TextTransformer transformer;

	private Scheduler svc = Schedulers.from(Executors.newSingleThreadExecutor());

	/**
	 * The Constructor.
	 *
	 * @param channel
	 *          the channel
	 * @param destination
	 *          the destination
	 */
	public TextStreamer(KiSyChannel channel, InetSocketAddress destination) {
		super(DEFAULT_TEXT_PIPE_SIZE, channel, destination, MediaStreamType.TEXT);
		initDefaultChunkProcessorAndFooter();
		setAckRequired(true);
		setTransformer(new DefaultTextTransformer());
	}

	/**
	 * Stream.
	 *
	 * @param text
	 *          the text
	 */
	public void stream(final String text) {
		if (StringUtils.isEmpty(text)) return;

		log.debug("Streaming text {}", text);

		final TextTransformer tt = getTransformer();

		if (tt == null) throw new IllegalStateException("TextTransformer cannot be null");

		svc.createWorker().schedule(new Action0() {

			@Override
			public void call() {
				try {
					tt.transform(text, TextStreamer.this);
				} catch (Exception e) {
					log.error("Unexpected exception", e);
				}
			}
		});
	}

	/**
	 * Gets the transformer.
	 *
	 * @return the transformer
	 */
	public TextTransformer getTransformer() {
		return transformer;
	}

	/**
	 * Sets the transformer.
	 *
	 * @param transformer
	 *          the transformer
	 */
	public void setTransformer(TextTransformer transformer) {
		this.transformer = transformer;
	}

	private void initDefaultChunkProcessorAndFooter() {
		DefaultTextChunkProcessor dtcp = new DefaultTextChunkProcessor();

		setMediaChunkProcessor(dtcp);

		MediaFooterMessage mfm = new MediaFooterMessage(MediaStreamType.TEXT, dtcp.getMediaHash());

		setMediaFooter(new MediaFooter(mfm));
	}

}
