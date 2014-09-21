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
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.pprspray.core.handler.AbstractInboundMediaHandler;
import com.github.mrstampy.pprspray.core.handler.AudioMediaHandler;
import com.github.mrstampy.pprspray.core.handler.BinaryMediaHandler;
import com.github.mrstampy.pprspray.core.handler.FileMediaHandler;
import com.github.mrstampy.pprspray.core.handler.JsonMediaHandler;
import com.github.mrstampy.pprspray.core.handler.NegotiationAckHandler;
import com.github.mrstampy.pprspray.core.handler.NegotiationHandler;
import com.github.mrstampy.pprspray.core.handler.TextMediaHandler;
import com.github.mrstampy.pprspray.core.handler.WebcamMediaHandler;
import com.github.mrstampy.pprspray.core.receiver.AbstractChunkReceiver;
import com.github.mrstampy.pprspray.core.streamer.audio.DefaultAudioChunk;
import com.github.mrstampy.pprspray.core.streamer.binary.DefaultBinaryChunk;
import com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunk;
import com.github.mrstampy.pprspray.core.streamer.file.DefaultFileChunk;
import com.github.mrstampy.pprspray.core.streamer.footer.MediaFooterChunk;
import com.github.mrstampy.pprspray.core.streamer.negotiation.NegotiationAckChunk;
import com.github.mrstampy.pprspray.core.streamer.negotiation.NegotiationChunk;
import com.github.mrstampy.pprspray.core.streamer.text.DefaultJsonChunk;
import com.github.mrstampy.pprspray.core.streamer.text.DefaultTextChunk;
import com.github.mrstampy.pprspray.core.streamer.webcam.DefaultWebcamChunk;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;

/**
 * {@link AbstractMediaChunk}s are posted to the event bus via
 * {@link AbstractInboundMediaHandler} implementations.
 */
public class ChunkEventBus {
	private static final Logger log = LoggerFactory.getLogger(ChunkEventBus.class);

	private static final AsyncEventBus BUS = new AsyncEventBus("Chunk Arrival Event Bus", Executors.newCachedThreadPool());

	private static Map<Integer, AbstractChunkReceiver<?>> receivers = new ConcurrentHashMap<Integer, AbstractChunkReceiver<?>>();

	/**
	 * Post.
	 *
	 * @param chunk
	 *          the chunk
	 * @see AudioMediaHandler
	 */
	public static void post(DefaultAudioChunk chunk) {
		BUS.post(chunk);
	}

	/**
	 * Post.
	 *
	 * @param chunk
	 *          the chunk
	 * @see BinaryMediaHandler
	 */
	public static void post(DefaultBinaryChunk chunk) {
		BUS.post(chunk);
	}

	/**
	 * Post.
	 *
	 * @param chunk
	 *          the chunk
	 * @see FileMediaHandler
	 */
	public static void post(DefaultFileChunk chunk) {
		BUS.post(chunk);
	}

	/**
	 * Post.
	 *
	 * @param chunk
	 *          the chunk
	 * @see JsonMediaHandler
	 */
	public static void post(DefaultJsonChunk chunk) {
		BUS.post(chunk);
	}

	/**
	 * Post.
	 *
	 * @param chunk
	 *          the chunk
	 * @see TextMediaHandler
	 */
	public static void post(DefaultTextChunk chunk) {
		BUS.post(chunk);
	}

	/**
	 * Post.
	 *
	 * @param chunk
	 *          the chunk
	 * @see WebcamMediaHandler
	 */
	public static void post(DefaultWebcamChunk chunk) {
		BUS.post(chunk);
	}

	/**
	 * Post.
	 *
	 * @param chunk
	 *          the chunk
	 * @see NegotiationHandler
	 */
	public static void post(NegotiationChunk chunk) {
		BUS.post(chunk);
	}

	/**
	 * Post.
	 *
	 * @param chunk
	 *          the chunk
	 * @see NegotiationAckHandler
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
	public static void post(MediaFooterChunk footerMessage) {
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
	public static void register(AbstractChunkReceiver<?> receiver) {
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
	public static AbstractChunkReceiver<?> get(int mediaHash) {
		return receivers.get(mediaHash);
	}

	/**
	 * Removes the.
	 *
	 * @param mediaHash
	 *          the media hash
	 * @return the abstract media receiver<?>
	 */
	public static AbstractChunkReceiver<?> remove(int mediaHash) {
		AbstractChunkReceiver<?> receiver = receivers.remove(mediaHash);

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
		for (AbstractChunkReceiver<?> receiver : receivers.values()) {
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
		try {
			BUS.unregister(o);
		} catch (Exception e) {
			log.debug("{} is not registered on the chunk event bus", o, e);
		}
	}

	private ChunkEventBus() {
	}

}
