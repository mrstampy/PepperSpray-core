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
package com.github.mrstampy.pprspray.core.streamer.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.binary.BinaryStreamer;
import com.github.mrstampy.pprspray.core.streamer.footer.MediaFooter;
import com.github.mrstampy.pprspray.core.streamer.footer.MediaFooterMessage;

// TODO: Auto-generated Javadoc
/**
 * The Class MediaFileStreamer.
 */
public class MediaFileStreamer extends BinaryStreamer {
	private static final Logger log = LoggerFactory.getLogger(MediaFileStreamer.class);

	private static final int DEFAULT_FILE_PIPE_SIZE = 1024 * 4000;

	/**
	 * The Constructor.
	 */
	public MediaFileStreamer() {
		super(DEFAULT_FILE_PIPE_SIZE);
		initDefaultChunkProcessorAndFooter();
		setAckRequired(true);
	}

	/**
	 * Stream.
	 *
	 * @param file the file
	 * @throws IOException the IO exception
	 */
	public void stream(File file) throws IOException {
		if (file == null) return;

		log.debug("Streaming file {}", file.getAbsolutePath());

		FileInputStream fis = new FileInputStream(file);
		byte[] b = new byte[fis.available()];
		fis.read(b);

		stream(b);

		fis.close();
	}

	private void initDefaultChunkProcessorAndFooter() {
		DefaultFileChunkProcessor dfcp = new DefaultFileChunkProcessor();
		MediaFooterMessage mfm = new MediaFooterMessage(MediaStreamType.FILE, dfcp.getMediaHash());

		setMediaChunkProcessor(dfcp);
		setMediaFooter(new MediaFooter(mfm));
	}

}
