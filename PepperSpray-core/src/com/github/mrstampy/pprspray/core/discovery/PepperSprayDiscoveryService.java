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
package com.github.mrstampy.pprspray.core.discovery;

import java.net.InetAddress;
import java.util.List;

import com.github.mrstampy.kitchensync.netty.channel.KiSyChannel;
import com.github.mrstampy.pprspray.core.streamer.MediaStreamType;

// TODO: Auto-generated Javadoc
/**
 * The Interface PepperSprayDiscoveryService.
 *
 * @param <INFO>
 *          the generic type
 */
public interface PepperSprayDiscoveryService<INFO> {

	/**
	 * Register pepper spray service.
	 *
	 * @param channel
	 *          the channel
	 * @return true, if register pepper spray service
	 */
	boolean registerPepperSprayService(KiSyChannel channel);

	/**
	 * Register pepper spray service.
	 *
	 * @param channel
	 *          the channel
	 * @param identifier
	 *          the identifier
	 * @return true, if register pepper spray service
	 */
	boolean registerPepperSprayService(KiSyChannel channel, String identifier);

	/**
	 * Register pepper spray services.
	 *
	 * @param channel
	 *          the channel
	 * @param types
	 *          the types
	 * @return true, if register pepper spray services
	 */
	boolean registerPepperSprayServices(KiSyChannel channel, MediaStreamType... types);

	/**
	 * Unregister pepper spray service.
	 *
	 * @param channel
	 *          the channel
	 * @return true, if unregister pepper spray service
	 */
	boolean unregisterPepperSprayService(KiSyChannel channel);

	/**
	 * Unregister pepper spray services.
	 *
	 * @param channel
	 *          the channel
	 * @param types
	 *          the types
	 * @return true, if unregister pepper spray services
	 */
	boolean unregisterPepperSprayServices(KiSyChannel channel, MediaStreamType... types);

	/**
	 * Unregister pepper spray service.
	 *
	 * @param channel
	 *          the channel
	 * @param identifier
	 *          the name
	 * @return true, if unregister pepper spray service
	 */
	boolean unregisterPepperSprayService(KiSyChannel channel, String identifier);

	/**
	 * Gets the registered pepper spray services.
	 *
	 * @return the registered pepper spray services
	 */
	List<INFO> getRegisteredPepperSprayServices();

	/**
	 * Gets the registered pepper spray services.
	 *
	 * @param address
	 *          the address
	 * @return the registered pepper spray services
	 */
	List<INFO> getRegisteredPepperSprayServices(InetAddress address);

	/**
	 * Gets the registered pepper spray services.
	 *
	 * @param identifier
	 *          the identifier
	 * @return the registered pepper spray services
	 */
	List<INFO> getRegisteredPepperSprayServices(String identifier);

	/**
	 * Gets the registered pepper spray services.
	 *
	 * @param type
	 *          the type
	 * @return the registered pepper spray services
	 */
	List<INFO> getRegisteredPepperSprayServices(MediaStreamType type);

	/**
	 * Creates the service name.
	 *
	 * @param type
	 *          the type
	 * @return the string
	 */
	String createServiceName(MediaStreamType type);
}
