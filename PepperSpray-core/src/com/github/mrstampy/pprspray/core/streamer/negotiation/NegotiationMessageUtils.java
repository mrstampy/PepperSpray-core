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
import io.netty.buffer.Unpooled;

import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.util.MediaStreamerUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class NegotiationMessageUtils.
 */
public class NegotiationMessageUtils {

	/**
	 * Gets the negotiation message.
	 *
	 * @param mediaHash
	 *          the media hash
	 * @param requestedType
	 *          the requested type
	 * @return the negotiation message
	 */
	public static ByteBuf getNegotiationMessage(int mediaHash, MediaStreamType requestedType) {
		int headerLength = MediaStreamerUtils.DEFAULT_HEADER_LENGTH + 4;

		ByteBuf buf = Unpooled.buffer(headerLength);

		MediaStreamerUtils.writeHeader(buf, MediaStreamType.NEGOTIATION, headerLength, mediaHash, 0);

		buf.writeBytes(requestedType.ordinalBytes());

		return buf;
	}

	/**
	 * Gets the negotiation ack message.
	 *
	 * @param mediaHash
	 *          the media hash
	 * @param accepted
	 *          the accepted
	 * @return the negotiation ack message
	 */
	public static ByteBuf getNegotiationAckMessage(int mediaHash, boolean accepted) {
		int headerLength = MediaStreamerUtils.DEFAULT_HEADER_LENGTH;

		ByteBuf buf = Unpooled.buffer(headerLength + 1);

		MediaStreamerUtils.writeHeader(buf, MediaStreamType.NEGOTIATION_ACK, headerLength, mediaHash, 0);

		buf.writeBoolean(accepted);

		return buf;
	}

	private NegotiationMessageUtils() {
	}

}
