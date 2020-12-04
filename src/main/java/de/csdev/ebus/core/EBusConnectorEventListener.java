/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
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
 * @author Christian Sowada - Initial contribution
 *
 */
@NonNullByDefault
public class EBusConnectorEventListener implements IEBusConnectorEventListener {

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.IEBusConnectorEventListener#onTelegramReceived(byte[], java.lang.Integer)
     */
    @Override
    public void onTelegramReceived(byte[] receivedData, @Nullable Integer sendQueueId) {
        // noop
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.IEBusConnectorEventListener#onTelegramException(de.csdev.ebus.core.EBusDataException,
     * java.lang.Integer)
     */
    @Override
    public void onTelegramException(EBusDataException exception, @Nullable Integer sendQueueId) {
        // noop
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.IEBusConnectorEventListener#onConnectionException(java.lang.Exception)
     */
    @Override
    public void onConnectionException(Exception e) {
        // noop
    }

    @Override
    public void onConnectionStatusChanged(ConnectionStatus status) {
        // noop
    }
}
