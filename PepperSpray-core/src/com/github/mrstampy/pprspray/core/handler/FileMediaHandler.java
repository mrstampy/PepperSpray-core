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

import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.file.DefaultFileChunk;

// TODO: Auto-generated Javadoc
/**
 * The Class FileMediaHandler.
 */
public class FileMediaHandler extends AbstractInboundMediaHandler<DefaultFileChunk> {

	private static final long serialVersionUID = -2092069884261330398L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.handler.AbstractInboundMediaHandler#
	 * createChunk(byte[])
	 */
	@Override
	protected DefaultFileChunk createChunk(byte[] message) {
		return new DefaultFileChunk(message);
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
		return MediaStreamType.FILE;
	}

}
