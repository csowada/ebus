/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import de.csdev.ebus.core.IEBusController.ConnectionStatus;

/**
 * This listener is called if the connector received a valid eBUS telegram.
 *
 * @author Christian Sowada - Initial contribution
 *
 */
@NonNullByDefault
public interface IEBusConnectorEventListener {

    /**
     * A new valid telegram has been received.
     *
     * @param receivedData
     * @param sendQueueId
     */
    public void onTelegramReceived(final byte[] receivedData, final @Nullable Integer sendQueueId);

    /**
     * A new valid telegram has been received.
     *
     * @param exception
     * @param sendQueueId
     */
    public void onTelegramException(final EBusDataException exception, final @Nullable Integer sendQueueId);

    /**
     * A connection exception has occurred
     *
     * @param e
     */
    public void onConnectionException(final Exception e);

    /**
     * The connection status has changed
     *
     * @param status ConnectionStatus.CONNECTING, ConnectionStatus.CONNECTED or ConnectionStatus.DISCONNECTED
     */
    public void onConnectionStatusChanged(final ConnectionStatus status);
}
