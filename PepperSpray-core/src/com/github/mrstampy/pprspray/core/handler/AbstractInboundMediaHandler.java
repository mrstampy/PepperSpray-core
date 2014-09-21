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
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Scheduler;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import com.github.mrstampy.kitchensync.message.inbound.AbstractInboundKiSyHandler;
import com.github.mrstampy.kitchensync.message.inbound.ByteArrayInboundMessageManager;
import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;
import com.github.mrstampy.kitchensync.stream.StreamerAckRegister;
import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunk;
import com.github.mrstampy.pprspray.core.streamer.chunk.event.ChunkEventBus;
import com.github.mrstampy.pprspray.core.streamer.util.MediaStreamerUtils;

/**
 * The Class AbstractInboundMediaHandler is the superclass for all
 * {@link AbstractInboundKiSyHandler}s. It provides the structure to convert an
 * array of bytes (an inbound message) to its appropriate
 * {@link AbstractMediaChunk} implementation. The resulting object is then
 * posted on the {@link ChunkEventBus}.
 *
 * @param <AMC>
 *          the generic type
 * @see ByteArrayInboundMessageManager#addMessageHandlers(com.github.mrstampy.kitchensync.message.inbound.KiSyInboundMesssageHandler...)
 */
public abstract class AbstractInboundMediaHandler<AMC extends AbstractMediaChunk> extends
		AbstractInboundKiSyHandler<byte[]> {
	private static final Logger log = LoggerFactory.getLogger(AbstractInboundMediaHandler.class);

	private static final long serialVersionUID = -575695328821545145L;

	private Scheduler svc = Schedulers.from(Executors.newCachedThreadPool());

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
	protected final void onReceive(final byte[] message, final KiSyChannel channel, final InetSocketAddress sender)
			throws Exception {
		svc.createWorker().schedule(new Action0() {

			@Override
			public void call() {
				try {
					AMC chunk = createChunk(message);

					chunk.setChannelPort(channel.getPort());
					chunk.setRemote(sender);
					chunk.setLocal(channel.localAddress());

					if (log.isTraceEnabled()) log.trace("Received chunk {}", chunk);

					if (chunk.isAckRequired()) sendAck(message, channel, sender, chunk);

					post(chunk);
				} catch (Exception e) {
					log.error("Unexpected exception", e);
				}
			}
		});
	}

	/**
	 * Send ack.
	 *
	 * @param message
	 *          the message
	 * @param channel
	 *          the channel
	 * @param sender
	 *          the sender
	 * @param chunk
	 *          the chunk
	 */
	protected void sendAck(byte[] message, final KiSyChannel channel, final InetSocketAddress sender, AMC chunk) {
		long sumOfBytes = StreamerAckRegister.convertToLong(message);
		channel.send(StreamerAckRegister.createAckResponse(sumOfBytes), sender);
	}

	/**
	 * Post.
	 *
	 * @param chunk
	 *          the chunk
	 */
	protected void post(AMC chunk) {
		ChunkEventBus.post(chunk);
	}

	/**
	 * Creates the chunk.
	 *
	 * @param message
	 *          the message
	 * @return the amc
	 */
	protected abstract AMC createChunk(byte[] message);

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	protected abstract MediaStreamType getType();

}
