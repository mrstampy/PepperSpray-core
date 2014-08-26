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
package com.github.mrstampy.pprspray.core.handler;

import java.net.InetSocketAddress;

import com.github.mrstampy.kitchensync.message.inbound.AbstractInboundKiSyHandler;
import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;
import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunk;
import com.github.mrstampy.pprspray.core.streamer.chunk.event.ChunkEventBus;
import com.github.mrstampy.pprspray.core.streamer.util.MediaStreamerUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractInboundMediaHandler.
 */
public abstract class AbstractInboundMediaHandler extends AbstractInboundKiSyHandler<byte[]> {

	private static final long serialVersionUID = -575695328821545145L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.kitchensync.message.inbound.KiSyInboundMesssageHandler
	 * #canHandleMessage(java.lang.Object)
	 */
	@Override
	public boolean canHandleMessage(byte[] message) {
		return MediaStreamerUtils.isMediaType(message, getType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.kitchensync.message.inbound.KiSyInboundMesssageHandler
	 * #getExecutionOrder()
	 */
	@Override
	public int getExecutionOrder() {
		return DEFAULT_EXECUTION_ORDER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.kitchensync.message.inbound.AbstractInboundKiSyHandler
	 * #onReceive(java.lang.Object,
	 * com.github.mrstampy.kitchensync.netty.channel.KiSyChannel,
	 * java.net.InetSocketAddress)
	 */
	@Override
	protected final void onReceive(byte[] message, KiSyChannel channel, InetSocketAddress sender) throws Exception {
		onReceiveImpl(message, channel, sender);
	}

	/**
	 * On receive impl.
	 *
	 * @param <AMC>
	 *          the generic type
	 * @param message
	 *          the message
	 * @param channel
	 *          the channel
	 * @param sender
	 *          the sender
	 * @throws Exception
	 *           the exception
	 */
	protected <AMC extends AbstractMediaChunk> void onReceiveImpl(byte[] message, KiSyChannel channel,
			InetSocketAddress sender) throws Exception {
		AMC chunk = createChunk(message);

		chunk.setChannelPort(channel.getPort());
		chunk.setSender(sender);

		ChunkEventBus.post(chunk);
	}

	/**
	 * Creates the chunk.
	 *
	 * @param <AMC>
	 *          the generic type
	 * @param message
	 *          the message
	 * @return the amc
	 */
	protected abstract <AMC extends AbstractMediaChunk> AMC createChunk(byte[] message);

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	protected abstract MediaStreamType getType();

}
