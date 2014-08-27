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

// TODO: Auto-generated Javadoc
/**
 * The Interface FileTransformer.
 */
public interface FileTransformer {

	/**
	 * Transform.
	 *
	 * @param file
	 *          the file
	 * @return the byte[]
	 * @throws IOException
	 *           the IO exception
	 */
	byte[] transform(File file) throws IOException;
}
