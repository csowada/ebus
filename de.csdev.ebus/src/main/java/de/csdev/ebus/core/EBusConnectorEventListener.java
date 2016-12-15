/**
 * Copyright (c) 2010-2016 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core;

/**
 * This listener is called if the connector received a valid eBUS telegram.
 *
 * @author Christian Sowada
 * @since 2.0.0
 */
public interface EBusConnectorEventListener {

    /**
     * A new valid telegram has been received.
     *
     * @param telegram
     */
    public void onTelegramReceived(byte[] receivedData, Integer sendQueueId);

}
