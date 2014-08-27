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
import java.io.IOException;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Scheduler;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

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

	private FileTransformer fileTransformer;

	private Scheduler svc = Schedulers.from(Executors.newSingleThreadExecutor());

	/**
	 * The Constructor.
	 */
	public MediaFileStreamer() {
		super(DEFAULT_FILE_PIPE_SIZE);
		initDefaultChunkProcessorAndFooter();
		setAckRequired(true);
		setFileTransformer(new DefaultFileTransformer());
	}

	/**
	 * Stream.
	 *
	 * @param file
	 *          the file
	 * @throws IOException
	 *           the IO exception
	 */
	public void stream(final File file) throws IOException {
		if (file == null) return;

		log.debug("Streaming file {}", file.getAbsolutePath());

		final FileTransformer ft = getFileTransformer();

		if (ft == null) throw new IllegalStateException("FileTransformer cannot be null");

		svc.createWorker().schedule(new Action0() {

			@Override
			public void call() {
				try {
					ft.transform(file, MediaFileStreamer.this);
				} catch (IOException e) {
					log.error("Unexpected exception streaming file {}", file.getAbsolutePath(), e);
				}

			}
		});
	}

	/**
	 * Gets the file transformer.
	 *
	 * @return the file transformer
	 */
	public FileTransformer getFileTransformer() {
		return fileTransformer;
	}

	/**
	 * Sets the file transformer.
	 *
	 * @param fileTransformer
	 *          the file transformer
	 */
	public void setFileTransformer(FileTransformer fileTransformer) {
		this.fileTransformer = fileTransformer;
	}

	private void initDefaultChunkProcessorAndFooter() {
		DefaultFileChunkProcessor dfcp = new DefaultFileChunkProcessor();
		MediaFooterMessage mfm = new MediaFooterMessage(MediaStreamType.FILE, dfcp.getMediaHash());

		setMediaChunkProcessor(dfcp);
		setMediaFooter(new MediaFooter(mfm));
	}

}
