/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.aaa;

import java.util.Map;

import de.csdev.ebus.cfg.EBusConfigurationTelegram;

/**
 * @author Christian Sowada
 *
 */
public interface EBusParserListener {

	public void onTelegramResolved(EBusConfigurationTelegram registryEntry, Map<String, Object> result, byte[] receivedData, Integer sendQueueId);
	
}
