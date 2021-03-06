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
package com.github.mrstampy.pprspray.core.test.audio;

import javax.sound.sampled.LineUnavailableException;

import com.github.mrstampy.kitchensync.message.inbound.ByteArrayInboundMessageManager;
import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;
import com.github.mrstampy.pprspray.core.handler.AudioMediaHandler;
import com.github.mrstampy.pprspray.core.handler.MediaFooterHandler;
import com.github.mrstampy.pprspray.core.handler.NegotiationAckHandler;
import com.github.mrstampy.pprspray.core.handler.NegotiationHandler;
import com.github.mrstampy.pprspray.core.streamer.audio.AudioStreamer;
import com.github.mrstampy.pprspray.core.streamer.audio.DefaultAudioTransformer;
import com.github.mrstampy.pprspray.core.streamer.negotiation.NegotiationChunk;
import com.github.mrstampy.pprspray.core.test.AbstractTest;
import com.github.mrstampy.pprspray.core.test.LoggingProcessor;
import com.github.mrstampy.pprspray.core.test.TestNegotiationSubscriber;

// TODO: Auto-generated Javadoc
/**
 * This is the first system test class of the PepperSpray-core framework. It has
 * been used to remove wrinkles in the code and demonstrate the concepts of this
 * framework.<br>
 * <br>
 * 
 * This class starts by creating 2 {@link KiSyChannel}s and an
 * {@link AudioStreamer} to stream audio from channel1 to channel2. Auto
 * negotiation is on, so when {@link AudioStreamer#connect()} is invoked a
 * {@link NegotiationChunk} message is sent to channel2. The
 * {@link TestNegotiationSubscriber} creates and registers the necessary objects
 * to receive and process the audio stream and sends an acknowledgement back to
 * channel1, which is awaiting the response. When received audio is streamed
 * from channel1 to channel2.<br>
 * <br>
 * 
 * The {@link LoggingProcessor} created by the {@link TestNegotiationSubscriber}
 * writes events received on channel2 to the log at debug level.<br>
 * <br>
 * 
 * @see DefaultAudioTransformer
 */
public class AudioTest extends AbstractTest {

	private AudioStreamer audioStreamer;

	/**
	 * The Constructor.
	 *
	 * @throws LineUnavailableException
	 *           the line unavailable exception
	 */
	public AudioTest() throws LineUnavailableException {
		super();

		audioStreamer = new AudioStreamer(getChannel1(), getChannel2().localAddress(),
				TestNegotiationSubscriber.AUDIO_FORMAT);
	}

	private void execute() throws LineUnavailableException {
		// enable this to see the chunks arrive on channel2
		// ChunkEventBus.register(new LoggingChunkReceiver());

		audioStreamer.connect();
	}

	/**
	 * These are the classes which deal with inbound messages.
	 * 
	 * @see AudioMediaHandler
	 * @see NegotiationHandler
	 * @see NegotiationAckHandler
	 * @see MediaFooterHandler
	 * @see ByteArrayInboundMessageManager
	 */
	protected void initInboundManager() {
		//@formatter:off
		ByteArrayInboundMessageManager.INSTANCE.addMessageHandlers(
				new AudioMediaHandler(),
				new NegotiationHandler(), 
				new NegotiationAckHandler(),
				new MediaFooterHandler());
		//@formatter:on
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *          the args
	 * @throws Exception
	 *           the exception
	 */
	public static void main(String[] args) throws Exception {
		new AudioTest().execute();
	}

}
