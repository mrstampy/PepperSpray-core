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
package com.github.mrstampy.pprspray.core.test.webcam;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.pprspray.core.receiver.AbstractMediaProcessor;
import com.github.mrstampy.pprspray.core.receiver.MediaEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class TestWebcamProcessor.
 */
public class TestWebcamProcessor extends AbstractMediaProcessor {

	private static final Logger log = LoggerFactory.getLogger(TestWebcamProcessor.class);

	/**
	 * The Constructor.
	 *
	 * @param mediaHash
	 *          the media hash
	 * @param local
	 *          the local
	 * @param remote
	 *          the remote
	 */
	public TestWebcamProcessor(int mediaHash, InetSocketAddress local, InetSocketAddress remote) {
		super(mediaHash, local, remote);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.pprspray.core.receiver.AbstractMediaProcessor#
	 * mediaEventImpl(com.github.mrstampy.pprspray.core.receiver.MediaEvent)
	 */
	@Override
	protected void mediaEventImpl(MediaEvent event) throws Exception {
		try {
			BufferedImage image = convertToImage(event);

			log.debug("Image: {}", image);
		} catch (Exception e) {
			log.error("Booo!");
		}
	}

	private BufferedImage convertToImage(MediaEvent event) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(event.getProcessed());

		return ImageIO.read(bais);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.receiver.AbstractMediaProcessor#openImpl
	 * ()
	 */
	@Override
	protected boolean openImpl() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.receiver.AbstractMediaProcessor#closeImpl
	 * ()
	 */
	@Override
	protected boolean closeImpl() {
		return true;
	}

}
