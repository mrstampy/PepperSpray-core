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
package com.github.mrstampy.pprspray.core.streamer.negotiation;

import io.netty.buffer.ByteBuf;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Mixer;

import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;
import com.github.mrstampy.pprspray.core.receiver.MediaEventBus;
import com.github.mrstampy.pprspray.core.receiver.MediaProcessor;
import com.github.mrstampy.pprspray.core.receiver.audio.AudioReceiver;
import com.github.mrstampy.pprspray.core.receiver.audio.DefaultAudioProcessor;
import com.github.mrstampy.pprspray.core.receiver.binary.BinaryReceiver;
import com.github.mrstampy.pprspray.core.receiver.file.FileReceiver;
import com.github.mrstampy.pprspray.core.receiver.text.TextReceiver;
import com.github.mrstampy.pprspray.core.receiver.webcam.WebcamReceiver;
import com.github.mrstampy.pprspray.core.streamer.util.MediaStreamerUtils;

/**
 * The Class AcceptingNegotationSubscriber accepts all requests for connections.
 * It should be considered as a reference implementation - experimental.
 */
public class AcceptingNegotationSubscriber extends AbstractNegotiationSubscriber {

	private AudioFormat audioFormat;
	private Mixer.Info mixerInfo;

	/**
	 * The Constructor. The audio format object is used by
	 * {@link DefaultAudioProcessor} instances by default and should be considered
	 * experimental for audio.
	 *
	 * @param audioFormat
	 *          the audio format
	 */
	public AcceptingNegotationSubscriber(AudioFormat audioFormat) {
		this(audioFormat, null);
	}

	/**
	 * The Constructor. The audio format and mixer info objects are used by
	 * {@link DefaultAudioProcessor} instances by default and should be considered
	 * experimental for audio.
	 *
	 * @param audioFormat
	 *          the audio format
	 * @param mixerInfo
	 *          the mixer info
	 */
	public AcceptingNegotationSubscriber(AudioFormat audioFormat, Mixer.Info mixerInfo) {
		super();
		this.audioFormat = audioFormat;
		this.mixerInfo = mixerInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.streamer.negotiation.
	 * AbstractNegotiationSubscriber
	 * #negotiationRequestedImpl(com.github.mrstampy.pprspray
	 * .core.streamer.negotiation.NegotiationChunk)
	 */
	@Override
	protected void negotiationRequestedImpl(NegotiationChunk event) {
		KiSyChannel channel = MediaStreamerUtils.getChannel(event.getLocal());

		createReceiver(event);

		registerMediaProcessor(event);

		ByteBuf ack = NegotiationMessageUtils.getNegotiationAckMessage(event.getMediaHash(), true);

		channel.send(ack.array(), event.getRemote());

		postNegotiationEvent(true, event);
	}

	/**
	 * Register media processor.
	 *
	 * @param event
	 *          the event
	 */
	protected void registerMediaProcessor(NegotiationChunk event) {
		MediaProcessor o = getMediaProcessor(event);

		if (o == null) return;

		MediaEventBus.register(o);
	}

	/**
	 * Gets the media processor.
	 *
	 * @param event
	 *          the event
	 * @return the media processor
	 */
	protected MediaProcessor getMediaProcessor(NegotiationChunk event) {
		switch (event.getRequestedType()) {
		case AUDIO:
			return new DefaultAudioProcessor(event.getMediaHash(), event.getLocal(), event.getRemote(), audioFormat,
					mixerInfo);
		default:
			return null;
		}
	}

	/**
	 * Creates the receiver.
	 *
	 * @param requestedType
	 *          the requested type
	 * @param mediaHash
	 *          the media hash
	 */
	protected void createReceiver(NegotiationChunk event) {
		int mediaHash = event.getMediaHash();

		switch (event.getRequestedType()) {
		case AUDIO:
			new AudioReceiver(mediaHash);
			break;
		case BINARY:
			new BinaryReceiver(mediaHash);
			break;
		case FILE:
			new FileReceiver(mediaHash);
			break;
		case TEXT:
			new TextReceiver(mediaHash);
			break;
		case VIDEO:
			new WebcamReceiver(mediaHash);
			break;
		default:
			break;
		}
	}

}
