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

import com.github.mrstampy.kitchensync.message.inbound.ByteArrayInboundMessageManager;
import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.text.DefaultXmlChunk;
import com.github.mrstampy.pprspray.core.streamer.util.MediaStreamerUtils;

/**
 * The Class JsonMediaHandler.
 * 
 * @see ByteArrayInboundMessageManager#addMessageHandlers(com.github.mrstampy.kitchensync.message.inbound.KiSyInboundMesssageHandler...)
 */
public class XmlMediaHandler extends AbstractInboundMediaHandler<DefaultXmlChunk> {

	private static final long serialVersionUID = -2924726176067175463L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.handler.AbstractInboundMediaHandler#
	 * canHandleMessage(byte[])
	 */
	@Override
	public boolean canHandleMessage(byte[] message) {
		return super.canHandleMessage(message) && MediaStreamerUtils.isXmlMessage(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.handler.AbstractInboundMediaHandler#
	 * getExecutionOrder()
	 */
	@Override
	public int getExecutionOrder() {
		return DEFAULT_EXECUTION_ORDER / 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.handler.AbstractInboundMediaHandler#
	 * createChunk(byte[])
	 */
	@Override
	protected DefaultXmlChunk createChunk(byte[] message) {
		return new DefaultXmlChunk(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.handler.AbstractInboundMediaHandler#getType
	 * ()
	 */
	@Override
	protected MediaStreamType getType() {
		return MediaStreamType.TEXT;
	}

}
