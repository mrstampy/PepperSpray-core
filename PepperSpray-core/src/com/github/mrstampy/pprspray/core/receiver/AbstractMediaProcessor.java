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

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.pprspray.core.receiver.event.ReceiverEvent;
import com.github.mrstampy.pprspray.core.receiver.event.ReceiverEventBus;
import com.github.mrstampy.pprspray.core.streamer.util.MediaStreamerUtils;
import com.google.common.eventbus.Subscribe;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractMediaProcessor is a convenience superclass to receive and
 * process events posted on the {@link MediaEventBus} by the corresponding
 * {@link AbstractChunkReceiver} and to respond appropriately to events posted
 * by the corresponding {@link AbstractChunkReceiver} on the
 * {@link ReceiverEventBus}.<br>
 * <br>
 * 
 * The {@link MediaEvent} received is a discrete chunk of data which has been
 * preprocessed (decrypted perhaps?) and is ready for use.
 * 
 * @see MediaEvent
 */
public abstract class AbstractMediaProcessor implements MediaProcessor {
	private static final Logger log = LoggerFactory.getLogger(AbstractMediaProcessor.class);

	private int mediaHash;

	private AtomicBoolean open = new AtomicBoolean(false);

	private InetSocketAddress local;
	private InetSocketAddress remote;

	/**
	 * The Constructor.
	 *
	 * @param mediaHash
	 *          the media hash
	 * @param local
	 *          the local
	 * @param remote
	 *          the remote
	 */
	protected AbstractMediaProcessor(int mediaHash, InetSocketAddress local, InetSocketAddress remote) {
		this.mediaHash = mediaHash;
		this.local = local;
		this.remote = remote;

		ReceiverEventBus.register(this);
		MediaEventBus.register(this);
		open.set(true);
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
		if (!event.isApplicable(event.getType(), getMediaHash())) return;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.receiver.MediaProcessor#isOpen()
	 */
	@Override
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.receiver.MediaProcessor#open()
	 */
	@Override
	public boolean open() {
		boolean open = isOpen() ? true : openImpl();

		setOpen(open);

		return open;
	}

	/**
	 * Open impl.
	 *
	 * @return true, if open impl
	 */
	protected abstract boolean openImpl();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.receiver.MediaProcessor#destroy()
	 */
	@Override
	public void destroy() {
		log.debug("Unregistering receiver for hash {}, local {}, remote {}", getMediaHash(), getLocal(), getRemote());
		MediaEventBus.unregister(this);
		ReceiverEventBus.unregister(this);

		try {
			close();
		} catch (Exception e) {
			log.error("Unexpected exception", e);
		}

		MediaStreamerUtils.sendTerminationEvent(getMediaHash(), getLocal(), getRemote());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.receiver.MediaProcessor#close()
	 */
	@Override
	public boolean close() {
		boolean close = !isOpen() ? true : closeImpl();

		setOpen(!close);

		return close;
	}

	/**
	 * Close impl.
	 *
	 * @return true, if close impl
	 */
	protected abstract boolean closeImpl();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.receiver.MediaProcessor#getMediaHash()
	 */
	public int getMediaHash() {
		return mediaHash;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.receiver.MediaProcessor#getLocal()
	 */
	public InetSocketAddress getLocal() {
		return local;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.receiver.MediaProcessor#getRemote()
	 */
	public InetSocketAddress getRemote() {
		return remote;
	}
}
