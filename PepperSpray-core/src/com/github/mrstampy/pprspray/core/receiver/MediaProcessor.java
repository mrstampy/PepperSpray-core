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

import com.github.mrstampy.pprspray.core.receiver.event.ReceiverEvent;
import com.github.mrstampy.pprspray.core.receiver.event.ReceiverEventBus;
import com.google.common.eventbus.Subscribe;

/**
 * The Interface MediaProcessor defines the methods to use received, processed
 * media data. It must be registered on the {@link MediaEventBus} to receive the
 * processed media and on the {@link ReceiverEventBus} to respond to state
 * changes from its corresponding {@link AbstractMediaReceiver}.
 * 
 */
public interface MediaProcessor {

	/**
	 * Media event. Annotate with {@link Subscribe}.
	 *
	 * @param event
	 *          the event
	 * @see MediaEventBus
	 */
	void mediaEvent(MediaEvent event);

	/**
	 * Receiver event. Annotate with {@link Subscribe}.
	 *
	 * @param event
	 *          the event
	 * @see ReceiverEventBus
	 */
	void receiverEvent(ReceiverEvent event);

	/**
	 * Checks if is open.
	 *
	 * @return true, if checks if is open
	 */
	boolean isOpen();

	/**
	 * Open.
	 *
	 * @return true, if open
	 */
	boolean open();

	/**
	 * Destroy.
	 */
	void destroy();

	/**
	 * Close.
	 *
	 * @return true, if close
	 */
	boolean close();

	/**
	 * Gets the media hash.
	 *
	 * @return the media hash
	 */
	int getMediaHash();

	/**
	 * Gets the local.
	 *
	 * @return the local
	 */
	InetSocketAddress getLocal();

	/**
	 * Gets the remote.
	 *
	 * @return the remote
	 */
	InetSocketAddress getRemote();
}