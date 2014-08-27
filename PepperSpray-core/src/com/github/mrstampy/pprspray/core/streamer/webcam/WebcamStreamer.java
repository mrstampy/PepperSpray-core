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
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;
import com.github.mrstampy.pprspray.core.streamer.AbstractMediaStreamer;
import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;
import com.github.mrstampy.pprspray.core.streamer.footer.MediaFooter;
import com.github.mrstampy.pprspray.core.streamer.footer.MediaFooterMessage;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;

// TODO: Auto-generated Javadoc
/**
 * The Class WebcamStreamer.
 */
public class WebcamStreamer extends AbstractMediaStreamer {

	private static final int DEFAULT_VIDEO_PIPE_SIZE = 1024 * 4000;

	private AtomicBoolean open = new AtomicBoolean(false);

	private Webcam webcam;

	private WebamStreamerListener listener = new WebamStreamerListener();

	private WebcamImageTransformer transformer;

	/**
	 * The Constructor.
	 *
	 * @param webcam
	 *          the webcam
	 * @param channel
	 *          the channel
	 * @param destination
	 *          the destination
	 */
	public WebcamStreamer(Webcam webcam, KiSyChannel channel, InetSocketAddress destination) {
		super(DEFAULT_VIDEO_PIPE_SIZE, channel, destination, MediaStreamType.VIDEO);

		this.webcam = webcam;
		webcam.addWebcamListener(listener);
		open.set(webcam.isOpen());

		initDefaultChunkProcessorAndFooter();
		setTransformer(new DefaultWebcamImageTransformer());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.streamer.AbstractMediaStreamer#start()
	 */
	public void start() {
		if (isStreaming()) return;

		webcam.open();
		super.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.streamer.AbstractMediaStreamer#stop()
	 */
	public void stop() {
		if (!isStreaming()) return;

		super.stop();
		webcam.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.streamer.AbstractMediaStreamer#destroy()
	 */
	public void destroy() {
		super.destroy();
		webcam.removeWebcamListener(listener);
	}

	/**
	 * Gets the transformer.
	 *
	 * @return the transformer
	 */
	public WebcamImageTransformer getTransformer() {
		return transformer;
	}

	/**
	 * Sets the transformer.
	 *
	 * @param transformer
	 *          the transformer
	 */
	public void setTransformer(WebcamImageTransformer transformer) {
		this.transformer = transformer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.streamer.AbstractMediaStreamer#getBytes()
	 */
	@Override
	protected byte[] getBytes() {
		BufferedImage image = webcam.getImage();

		if (image == null) stop();

		if (getTransformer() == null) throw new IllegalStateException("Transformer cannot be null");

		return getTransformer().transform(image);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.pprspray.core.streamer.AbstractMediaStreamer#isStreamable
	 * ()
	 */
	@Override
	protected boolean isStreamable() {
		return open.get();
	}

	private void phooCamClosed() {
		open.set(false);
		stop();
	}

	private void initDefaultChunkProcessorAndFooter() {
		DefaultWebcamChunkProcessor dwcp = new DefaultWebcamChunkProcessor(webcam);
		MediaFooterMessage mfm = new MediaFooterMessage(MediaStreamType.VIDEO, dwcp.getMediaHash());

		setMediaChunkProcessor(dwcp);
		setMediaFooter(new MediaFooter(mfm));
	}

	/**
	 * The Class WebamStreamerListener.
	 */
	protected class WebamStreamerListener implements WebcamListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.github.sarxos.webcam.WebcamListener#webcamOpen(com.github.sarxos.
		 * webcam.WebcamEvent)
		 */
		@Override
		public void webcamOpen(WebcamEvent we) {
			open.set(true);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.github.sarxos.webcam.WebcamListener#webcamClosed(com.github.sarxos
		 * .webcam.WebcamEvent)
		 */
		@Override
		public void webcamClosed(WebcamEvent we) {
			phooCamClosed();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.github.sarxos.webcam.WebcamListener#webcamDisposed(com.github.sarxos
		 * .webcam.WebcamEvent)
		 */
		@Override
		public void webcamDisposed(WebcamEvent we) {
			phooCamClosed();
		}

	}
}
