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

import com.github.mrstampy.kitchensync.message.inbound.ByteArrayInboundMessageManager;
import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;
import com.github.mrstampy.pprspray.core.handler.MediaFooterHandler;
import com.github.mrstampy.pprspray.core.handler.NegotiationAckHandler;
import com.github.mrstampy.pprspray.core.handler.NegotiationHandler;
import com.github.mrstampy.pprspray.core.handler.WebcamMediaHandler;
import com.github.mrstampy.pprspray.core.receiver.AbstractMediaReceiver;
import com.github.mrstampy.pprspray.core.receiver.MediaProcessor;
import com.github.mrstampy.pprspray.core.streamer.negotiation.NegotiationChunk;
import com.github.mrstampy.pprspray.core.streamer.negotiation.NegotiationEventBus;
import com.github.mrstampy.pprspray.core.test.channel.ByteArrayChannel;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractTest.
 */
public abstract class AbstractTest {
	private KiSyChannel channel1;
	private KiSyChannel channel2;

	/**
	 * The Constructor.
	 */
	protected AbstractTest() {
		initChannels();
		initNegotiationEventBus();
		initInboundManager();
	}

	/**
	 * These are the classes which deal with inbound messages.
	 * 
	 * @see WebcamMediaHandler
	 * @see NegotiationHandler
	 * @see NegotiationAckHandler
	 * @see MediaFooterHandler
	 * @see ByteArrayInboundMessageManager
	 */
	protected abstract void initInboundManager();

	/**
	 * Inits the channels.
	 */
	protected void initChannels() {
		channel1 = initChannel();
		channel2 = initChannel();
	}

	/**
	 * Inits the channel.
	 *
	 * @return the ki sy channel
	 */
	protected KiSyChannel initChannel() {
		ByteArrayChannel channel = new ByteArrayChannel();

		channel.bind();

		return channel;
	}

	/**
	 * This sets the class used to respond to {@link NegotiationChunk} messages.
	 * It needs to set up {@link MediaProcessor}s and
	 * {@link AbstractMediaReceiver}s appropriately. It is a key concept of the
	 * framework.
	 * 
	 * @see NegotiationEventBus
	 */
	protected void initNegotiationEventBus() {
		NegotiationEventBus.register(new TestNegotiationSubscriber());
	}

	/**
	 * Gets the channel1.
	 *
	 * @return the channel1
	 */
	public KiSyChannel getChannel1() {
		return channel1;
	}

	/**
	 * Gets the channel2.
	 *
	 * @return the channel2
	 */
	public KiSyChannel getChannel2() {
		return channel2;
	}

}
