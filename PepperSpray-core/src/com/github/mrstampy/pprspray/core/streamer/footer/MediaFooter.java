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
package com.github.mrstampy.pprspray.core.streamer.footer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import com.github.mrstampy.kitchensync.stream.footer.Footer;
import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.util.MediaStreamerUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class MediaFooter.
 */
public class MediaFooter implements Footer {

	private MediaStreamType type;
	private int mediaHash;
	private byte[] footer;

	/**
	 * The Constructor.
	 *
	 * @param type
	 *          the type
	 * @param mediaHash
	 *          the media hash
	 */
	public MediaFooter(MediaStreamType type, int mediaHash) {
		this.type = type;
		this.mediaHash = mediaHash;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.kitchensync.stream.footer.Footer#isFooter(byte[])
	 */
	@Override
	public boolean isFooter(byte[] message) {
		return MediaStreamerUtils.isMediaFooter(message, getType(), getMediaHash());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.kitchensync.stream.footer.Footer#createFooter()
	 */
	@Override
	public byte[] createFooter() {
		if (footer == null) footer = buildFooter();

		return footer;
	}

	private byte[] buildFooter() {
		ByteBuf buf = Unpooled.buffer(MediaStreamerUtils.FOOTER_LENGTH);

		buf.writeBytes(getType().eomBytes());
		buf.writeInt(getMediaHash());

		return buf.array();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.kitchensync.stream.footer.Footer#reset()
	 */
	@Override
	public void reset() {
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public MediaStreamType getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type
	 *          the type
	 */
	public void setType(MediaStreamType type) {
		this.type = type;
	}

	/**
	 * Gets the media hash.
	 *
	 * @return the media hash
	 */
	public int getMediaHash() {
		return mediaHash;
	}

	/**
	 * Sets the media hash.
	 *
	 * @param mediaHash
	 *          the media hash
	 */
	public void setMediaHash(int mediaHash) {
		this.mediaHash = mediaHash;
	}

}
