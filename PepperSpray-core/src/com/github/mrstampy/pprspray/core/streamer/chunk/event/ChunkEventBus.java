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
package com.github.mrstampy.pprspray.core.streamer.chunk.event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.mrstampy.pprspray.core.receiver.AbstractMediaReceiver;
import com.github.mrstampy.pprspray.core.streamer.audio.DefaultAudioChunk;
import com.github.mrstampy.pprspray.core.streamer.binary.DefaultBinaryChunk;
import com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunk;
import com.github.mrstampy.pprspray.core.streamer.file.DefaultFileChunk;
import com.github.mrstampy.pprspray.core.streamer.footer.MediaFooterMessage;
import com.github.mrstampy.pprspray.core.streamer.negotiation.NegotiationAckChunk;
import com.github.mrstampy.pprspray.core.streamer.negotiation.NegotiationChunk;
import com.github.mrstampy.pprspray.core.streamer.text.DefaultJsonChunk;
import com.github.mrstampy.pprspray.core.streamer.text.DefaultTextChunk;
import com.github.mrstampy.pprspray.core.streamer.webcam.DefaultWebcamChunk;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

// TODO: Auto-generated Javadoc
/**
 * The Class ChunkEventBus.
 */
public class ChunkEventBus {

	private static final EventBus BUS = new EventBus("Chunk Arrival Event Bus");

	private static Map<Integer, AbstractMediaReceiver<?>> receivers = new ConcurrentHashMap<Integer, AbstractMediaReceiver<?>>();

	/**
	 * Post.
	 *
	 * @param chunk
	 *          the chunk
	 */
	public static void post(DefaultAudioChunk chunk) {
		BUS.post(chunk);
	}

	/**
	 * Post.
	 *
	 * @param chunk
	 *          the chunk
	 */
	public static void post(DefaultBinaryChunk chunk) {
		BUS.post(chunk);
	}

	/**
	 * Post.
	 *
	 * @param chunk
	 *          the chunk
	 */
	public static void post(DefaultFileChunk chunk) {
		BUS.post(chunk);
	}

	/**
	 * Post.
	 *
	 * @param chunk
	 *          the chunk
	 */
	public static void post(DefaultJsonChunk chunk) {
		BUS.post(chunk);
	}

	/**
	 * Post.
	 *
	 * @param chunk
	 *          the chunk
	 */
	public static void post(DefaultTextChunk chunk) {
		BUS.post(chunk);
	}

	/**
	 * Post.
	 *
	 * @param chunk
	 *          the chunk
	 */
	public static void post(DefaultWebcamChunk chunk) {
		BUS.post(chunk);
	}

	/**
	 * Post.
	 *
	 * @param chunk
	 *          the chunk
	 */
	public static void post(NegotiationChunk chunk) {
		BUS.post(chunk);
	}

	/**
	 * Post.
	 *
	 * @param chunk
	 *          the chunk
	 */
	public static void post(NegotiationAckChunk chunk) {
		BUS.post(chunk);
	}

	/**
	 * Spill over for any subclasses of {@link AbstractMediaChunk} not covered.
	 *
	 * @param chunk
	 *          the chunk
	 */
	public static void post(AbstractMediaChunk chunk) {
		BUS.post(chunk);
	}

	/**
	 * Post.
	 *
	 * @param footerMessage
	 *          the footer message
	 */
	public static void post(MediaFooterMessage footerMessage) {
		BUS.post(footerMessage);
	}

	/**
	 * Objects are registering for notification of {@link AbstractMediaChunk}s and
	 * corresponding {@link MediaFooterMessage}s as specified in the various post
	 * methods and must implement a method with return of void, accepting a
	 * subclass of {@link AbstractMediaChunk} object as the only parameter, and
	 * annotated with {@link Subscribe}.
	 *
	 * @param o
	 *          the o
	 */
	public static void register(Object o) {
		BUS.register(o);
	}

	/**
	 * Objects are registering for notification of {@link AbstractMediaChunk}s and
	 * corresponding {@link MediaFooterMessage}s as specified in the various post
	 * methods and must implement a method with return of void, accepting a
	 * subclass of {@link AbstractMediaChunk} object as the only parameter, and
	 * annotated with {@link Subscribe}.
	 *
	 * @param receiver
	 *          the receiver
	 */
	public static void register(AbstractMediaReceiver<?> receiver) {
		BUS.register(receiver);

		receivers.put(receiver.getMediaHash(), receiver);
	}

	/**
	 * Gets the.
	 *
	 * @param mediaHash
	 *          the media hash
	 * @return the abstract media receiver<?>
	 */
	public static AbstractMediaReceiver<?> get(int mediaHash) {
		return receivers.get(mediaHash);
	}

	/**
	 * Removes the.
	 *
	 * @param mediaHash
	 *          the media hash
	 * @return the abstract media receiver<?>
	 */
	public static AbstractMediaReceiver<?> remove(int mediaHash) {
		AbstractMediaReceiver<?> receiver = receivers.remove(mediaHash);

		if (receiver == null) return null;

		receiver.destroy();

		return receiver;
	}

	/**
	 * Contains.
	 *
	 * @param mediaHash
	 *          the media hash
	 * @return true, if contains
	 */
	public static boolean contains(int mediaHash) {
		return receivers.containsKey(mediaHash);
	}

	/**
	 * Clear.
	 */
	public static void clear() {
		for (AbstractMediaReceiver<?> receiver : receivers.values()) {
			receiver.destroy();
		}

		receivers.clear();
	}

	/**
	 * Unregister.
	 *
	 * @param o
	 *          the o
	 */
	public static void unregister(Object o) {
		BUS.unregister(o);
	}

	private ChunkEventBus() {
	}

}
