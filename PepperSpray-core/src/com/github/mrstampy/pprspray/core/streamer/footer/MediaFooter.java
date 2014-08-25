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

// TODO: Auto-generated Javadoc
/**
 * The Class MediaFooter.
 */
public class MediaFooter implements Footer {

	private MediaFooterMessage footerMessage;

	/**
	 * The Constructor.
	 *
	 * @param footerMessage the footer message
	 */
	public MediaFooter(MediaFooterMessage footerMessage) {
		setFooterMessage(footerMessage);
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.kitchensync.stream.footer.Footer#isFooter(byte[])
	 */
	@Override
	public boolean isFooter(byte[] message) {
		return MediaFooterMessage.isMediaFooter(message, getFooterMessage().getMediaStreamType(), getFooterMessage().getMediaHash());
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.kitchensync.stream.footer.Footer#createFooter()
	 */
	@Override
	public byte[] createFooter() {
		ByteBuf buf = Unpooled.buffer(MediaFooterMessage.FOOTER_LENGTH);

		buf.writeBytes(getFooterMessage().getMediaStreamType().eomBytes());
		buf.writeInt(getFooterMessage().getMediaHash());

		return buf.array();
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.kitchensync.stream.footer.Footer#reset()
	 */
	@Override
	public void reset() {
	}

	/**
	 * Gets the footer message.
	 *
	 * @return the footer message
	 */
	public MediaFooterMessage getFooterMessage() {
		return footerMessage;
	}

	/**
	 * Sets the footer message.
	 *
	 * @param footerMessage the footer message
	 */
	public void setFooterMessage(MediaFooterMessage footerMessage) {
		this.footerMessage = footerMessage;
	}

}
