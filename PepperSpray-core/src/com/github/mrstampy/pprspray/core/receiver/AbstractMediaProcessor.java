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

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.pprspray.core.receiver.event.ReceiverEvent;
import com.github.mrstampy.pprspray.core.receiver.event.ReceiverEventBus;
import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.google.common.eventbus.Subscribe;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractMediaProcessor.
 */
public abstract class AbstractMediaProcessor {
	private static final Logger log = LoggerFactory.getLogger(AbstractMediaProcessor.class);

	private int mediaHash;

	private AtomicBoolean open = new AtomicBoolean(false);

	/**
	 * The Constructor.
	 *
	 * @param mediaHash
	 *          the media hash
	 */
	protected AbstractMediaProcessor(int mediaHash) {
		this.mediaHash = mediaHash;

		ReceiverEventBus.register(this);
		MediaEventBus.register(this);
	}

	/**
	 * Media event.
	 *
	 * @param event
	 *          the event
	 */
	@Subscribe
	public void mediaEvent(MediaEvent event) {
		if (!event.isApplicable(MediaStreamType.AUDIO, getMediaHash())) return;

		try {
			mediaEventImpl(event);
		} catch (Exception e) {
			log.error("Unexpected exception", e);
		}
	}

	/**
	 * Media event impl.
	 *
	 * @param event
	 *          the event
	 * @throws Exception
	 *           the exception
	 */
	protected abstract void mediaEventImpl(MediaEvent event) throws Exception;

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
	 * Checks if is open.
	 *
	 * @return true, if checks if is open
	 */
	public boolean isOpen() {
		return open.get();
	}

	/**
	 * Sets the open.
	 *
	 * @param b
	 *          the open
	 */
	protected void setOpen(boolean b) {
		open.set(b);
	}

	/**
	 * Open.
	 *
	 * @return true, if open
	 */
	public boolean open() {
		return isOpen() ? true : openImpl();
	}

	/**
	 * Open impl.
	 *
	 * @return true, if open impl
	 */
	protected abstract boolean openImpl();

	/**
	 * Destroy.
	 */
	public void destroy() {
		close();

		MediaEventBus.unregister(this);
		ReceiverEventBus.unregister(this);
	}

	/**
	 * Close.
	 *
	 * @return true, if close
	 */
	public boolean close() {
		return !isOpen() ? true : closeImpl();
	}

	/**
	 * Close impl.
	 *
	 * @return true, if close impl
	 */
	protected abstract boolean closeImpl();

	/**
	 * Gets the media hash.
	 *
	 * @return the media hash
	 */
	public int getMediaHash() {
		return mediaHash;
	}
}
