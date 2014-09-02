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
package com.github.mrstampy.pprspray.core.test;

import java.net.InetSocketAddress;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.pprspray.core.receiver.MediaEvent;
import com.github.mrstampy.pprspray.core.receiver.MediaProcessor;
import com.github.mrstampy.pprspray.core.receiver.event.ReceiverEvent;
import com.google.common.eventbus.Subscribe;

// TODO: Auto-generated Javadoc
/**
 * The Class LoggingAudioProcessor.
 */
public class LoggingProcessor implements MediaProcessor {
	private static final Logger log = LoggerFactory.getLogger(LoggingProcessor.class);

	private int loggingTextSize;

	/**
	 * The Constructor.
	 */
	public LoggingProcessor() {
		this(Integer.MAX_VALUE);
	}

	/**
	 * The Constructor.
	 *
	 * @param loggingTextSize
	 *          the logging text size
	 */
	public LoggingProcessor(int loggingTextSize) {
		this.loggingTextSize = loggingTextSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.receiver.MediaProcessor#mediaEvent(com
	 * .github.mrstampy.pprspray.core.receiver.MediaEvent)
	 */
	@Override
	@Subscribe
	public void mediaEvent(MediaEvent event) {
		log.debug("Media event {}, hash {} {}", event.getType(), event.getMediaHash(), createStringIfAppropriate(event));
	}

	private String createStringIfAppropriate(MediaEvent event) {
		switch (event.getType()) {
		case TEXT:
			return getString(event.getProcessed());
		default:
			break;
		}

		return "";
	}

	private String getString(byte[] processed) {
		int size = getLoggingTextSize();
		//@formatter:off
		return processed.length < size 
				? new String(processed) 
				: new String(Arrays.copyOfRange(processed, 0, size));
		//@formatter:on
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.receiver.MediaProcessor#receiverEvent
	 * (com.github.mrstampy.pprspray.core.receiver.event.ReceiverEvent)
	 */
	@Override
	@Subscribe
	public void receiverEvent(ReceiverEvent event) {
		log.debug("Receiver event {}, hash {}", event.getType(), event.getMediaHash());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.receiver.MediaProcessor#isOpen()
	 */
	@Override
	public boolean isOpen() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.receiver.MediaProcessor#open()
	 */
	@Override
	public boolean open() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.receiver.MediaProcessor#destroy()
	 */
	@Override
	public void destroy() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.receiver.MediaProcessor#close()
	 */
	@Override
	public boolean close() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.receiver.MediaProcessor#getMediaHash()
	 */
	@Override
	public int getMediaHash() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.receiver.MediaProcessor#getLocal()
	 */
	@Override
	public InetSocketAddress getLocal() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.receiver.MediaProcessor#getRemote()
	 */
	@Override
	public InetSocketAddress getRemote() {
		return null;
	}

	/**
	 * Gets the logging text size.
	 *
	 * @return the logging text size
	 */
	public int getLoggingTextSize() {
		return loggingTextSize;
	}

}
