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
package com.github.mrstampy.pprspray.core.streamer.webcam;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sarxos.webcam.util.ImageUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class DefaultWebcamImageTransformer.
 */
public class DefaultWebcamImageTransformer implements WebcamImageTransformer {
	private static final Logger log = LoggerFactory.getLogger(DefaultWebcamImageTransformer.class);

	//@formatter:off
	/**
	 * The Enum ImageFormat.
	 */
	public enum ImageFormat {
		
		/** The png. */
		PNG,
		
		/** The gif. */
		GIF,
		
		/** The jpg. */
		JPG,
		
		/** The bmp. */
		BMP,
		
		/** The wbmp. */
		WBMP;
	}
	//@formatter:on

	private ImageFormat imageFormat;

	private BufferedOutputStream bufOut;
	private ByteArrayOutputStream baos;

	/**
	 * The Constructor.
	 */
	public DefaultWebcamImageTransformer() {
		this(ImageFormat.PNG);
		baos = new ByteArrayOutputStream();
		bufOut = new BufferedOutputStream(baos);
		ImageIO.setUseCache(false);
	}

	/**
	 * The Constructor.
	 *
	 * @param imageFormat
	 *          the image format
	 */
	public DefaultWebcamImageTransformer(ImageFormat imageFormat) {
		if (imageFormat == null) throw new IllegalArgumentException("ImageFormat must be specified");
		this.imageFormat = imageFormat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.streamer.webcam.WebcamImageTransformer
	 * #transform(java.awt.image.BufferedImage)
	 */
	@Override
	public byte[] transform(BufferedImage image) {
		try {
			baos.reset();

			ImageIO.write(image, getImageFormatString(), bufOut);

			return baos.toByteArray();
		} catch (Exception e) {
			log.error("Unexpected exception", e);
		}
		
		return null;
	}

	private String getImageFormatString() {
		switch (getImageFormat()) {
		case BMP:
			return ImageUtils.FORMAT_BMP;
		case GIF:
			return ImageUtils.FORMAT_GIF;
		case JPG:
			return ImageUtils.FORMAT_JPG;
		case PNG:
			return ImageUtils.FORMAT_PNG;
		case WBMP:
			return ImageUtils.FORMAT_WBMP;
		}

		return null;
	}

	/**
	 * Gets the image format.
	 *
	 * @return the image format
	 */
	public ImageFormat getImageFormat() {
		return imageFormat;
	}

}
