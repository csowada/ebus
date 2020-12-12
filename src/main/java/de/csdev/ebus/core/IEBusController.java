/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core;

import org.eclipse.jdt.annotation.NonNull;

public interface IEBusController {

    @NonNull
    Integer addToSendQueue(byte @NonNull [] buffer, int maxAttemps) throws EBusControllerException;

    /**
     * @param buffer
     * @return
     * @throws EBusControllerException
     */
    @NonNull
    Integer addToSendQueue(byte @NonNull [] buffer) throws EBusControllerException;

    /**
     * Add an eBUS listener to receive valid eBus telegrams
     *
     * @param listener
     */
    void addEBusEventListener(@NonNull IEBusConnectorEventListener listener);

    /**
     * Remove an eBUS listener
     *
     * @param listener
     * @return
     */
    boolean removeEBusEventListener(@NonNull IEBusConnectorEventListener listener);

    boolean isRunning();

    void setWatchdogTimerTimeout(int seconds);

    long getLastSendReceiveRoundtripTime();

    void start();

    boolean isInterrupted();

    void interrupt();

    enum ConnectionStatus {
        DISCONNECTED,
        CONNECTING,
        CONNECTED
    }

    ConnectionStatus getConnectionStatus();
}