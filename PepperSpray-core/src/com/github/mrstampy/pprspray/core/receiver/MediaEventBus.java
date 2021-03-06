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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunk;
import com.google.common.eventbus.AsyncEventBus;

/**
 * The Class MediaEventBus contains references to active {@link MediaProcessor}
 * s. The {@link AbstractChunkReceiver} invokes the {@link #post(MediaEvent)}
 * method when the end of a discrete unit of data has been received (
 * {@link AbstractChunkReceiver#endOfMessage(com.github.mrstampy.pprspray.core.streamer.footer.MediaFooterChunk)}
 * ). The various {@link AbstractMediaChunk}s received are reconstituted and
 * used to create the {@link MediaEvent} posted.
 * 
 * @see AbstractChunkReceiver
 * @see MediaProcessor
 */
public class MediaEventBus {
	private static final Logger log = LoggerFactory.getLogger(MediaEventBus.class);

	private static final Map<Integer, MediaProcessor> mediaProcessors = new ConcurrentHashMap<>();
	private static final AsyncEventBus BUS = new AsyncEventBus("Media Event Bus", Executors.newCachedThreadPool());

	/**
	 * Post.
	 *
	 * @param event
	 *          the event
	 */
	public static void post(MediaEvent event) {
		BUS.post(event);
	}

	/**
	 * Register.
	 *
	 * @param o
	 *          the o
	 */
	public static void register(MediaProcessor o) {
		BUS.register(o);
		mediaProcessors.put(o.getMediaHash(), o);
	}

	/**
	 * Unregister.
	 *
	 * @param o
	 *          the o
	 */
	public static void unregister(MediaProcessor o) {
		if (o == null) return;
		
		try {
			mediaProcessors.remove(o.getMediaHash());
			BUS.unregister(o);
		} catch (Exception e) {
			log.debug("{} is not registered on the media event bus", o, e);
		}
	}

	/**
	 * Gets the.
	 *
	 * @param mediaHash
	 *          the media hash
	 * @return the media processor
	 */
	public static MediaProcessor get(int mediaHash) {
		return mediaProcessors.get(mediaHash);
	}

	/**
	 * Contains.
	 *
	 * @param mediaHash
	 *          the media hash
	 * @return true, if contains
	 */
	public static boolean contains(int mediaHash) {
		return mediaProcessors.containsKey(mediaHash);
	}

	/**
	 * Removes the.
	 *
	 * @param mediaHash
	 *          the media hash
	 */
	public static void remove(int mediaHash) {
		MediaProcessor mp = mediaProcessors.remove(mediaHash);
		if (mp == null) return;

		mp.destroy();
	}

	/**
	 * Clear.
	 */
	public static void clear() {
		List<MediaProcessor> list = new ArrayList<>(mediaProcessors.values());
		for (MediaProcessor mp : list) {
			mp.destroy();
		}
	}

	private MediaEventBus() {
		// TODO Auto-generated constructor stub
	}

}
