/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.service.parser;

import java.util.Map;

import de.csdev.ebus.command.IEBusCommandMethod;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public interface IEBusParserListener {

    /**
     * A parsed eBUS telegram was received and successful converted to it's values.
     *
     * @param registryEntry The used configuration to parse the byte data
     * @param result The result with all values
     * @param receivedData The raw data
     * @param sendQueueId The sendQueue id if available
     */
    public void onTelegramResolved(IEBusCommandMethod commandChannel, Map<String, Object> result, byte[] receivedData,
            Integer sendQueueId);

    public void onTelegramResolveFailed(IEBusCommandMethod commandChannel, byte[] receivedData, Integer sendQueueId,
            String exceptionMessage);

}
