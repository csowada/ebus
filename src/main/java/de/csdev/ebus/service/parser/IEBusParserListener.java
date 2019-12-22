/**
 * Copyright (c) 2016-2019 by the respective copyright holders.
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
     * @param commandChannel
     * @param result The result with all values
     * @param receivedData The raw data
     * @param sendQueueId The sendQueue id if available
     */
    public void onTelegramResolved(IEBusCommandMethod commandChannel, Map<String, Object> result, byte[] receivedData,
            Integer sendQueueId);

    /**
     * A parsed eBUS telegram was received but failed to resolve.
     *
     * @param commandChannel
     * @param receivedData
     * @param sendQueueId
     * @param exceptionMessage
     */
    public void onTelegramResolveFailed(IEBusCommandMethod commandChannel, byte[] receivedData, Integer sendQueueId,
            String exceptionMessage);

}
