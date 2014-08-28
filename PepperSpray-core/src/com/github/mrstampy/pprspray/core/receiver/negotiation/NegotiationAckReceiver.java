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
package com.github.mrstampy.pprspray.core.receiver.negotiation;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import rx.Scheduler;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import com.github.mrstampy.pprspray.core.receiver.AbstractMediaReceiver;
import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.footer.MediaFooterMessage;
import com.github.mrstampy.pprspray.core.streamer.negotiation.NegotiationAckChunk;
import com.github.mrstampy.pprspray.core.streamer.negotiation.NegotiationMessageUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class NegotiationAckReceiver.
 */
public abstract class NegotiationAckReceiver extends AbstractMediaReceiver<NegotiationAckChunk> {

	private static final NegotiationAckChunk[] MT = new NegotiationAckChunk[] {};

	private static final Scheduler SVC = Schedulers.from(Executors.newCachedThreadPool());

	/**
	 * The Constructor.
	 *
	 * @param mediaHash
	 *          the media hash
	 */
	public NegotiationAckReceiver(int mediaHash) {
		super(MediaStreamType.NEGOTIATION_ACK, mediaHash);

		SVC.createWorker().schedule(new Action0() {

			@Override
			public void call() {
				failed();
			}
		}, 30, TimeUnit.SECONDS);
	}

	/**
	 * Failed.
	 */
	protected void failed() {
		if (!isOpen()) return;

		byte[] failed = NegotiationMessageUtils.getNegotiationAckMessage(getMediaHash(), false).array();

		receiveImpl(new NegotiationAckChunk(failed));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.receiver.AbstractMediaReceiver#receiveImpl
	 * (com.github.mrstampy.pprspray.core.streamer.chunk.AbstractMediaChunk)
	 */
	@Override
	protected void receiveImpl(NegotiationAckChunk chunk) {
		destroy();

		ackReceived(chunk);
	}

	/**
	 * Ack received.
	 *
	 * @param chunk
	 *          the chunk
	 */
	protected abstract void ackReceived(NegotiationAckChunk chunk);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.receiver.AbstractMediaReceiver#
	 * endOfMessageImpl
	 * (com.github.mrstampy.pprspray.core.streamer.footer.MediaFooterMessage)
	 */
	@Override
	protected void endOfMessageImpl(MediaFooterMessage eom) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.receiver.AbstractMediaReceiver#getEmptyArray
	 * ()
	 */
	@Override
	protected NegotiationAckChunk[] getEmptyArray() {
		return MT;
	}

}
