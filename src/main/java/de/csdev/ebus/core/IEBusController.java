/**
 * Copyright (c) 2016-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core;

public interface IEBusController {

    Integer addToSendQueue(byte[] buffer, int maxAttemps) throws EBusControllerException;

    /**
     * @param buffer
     * @return
     * @throws EBusControllerException
     */
    Integer addToSendQueue(byte[] buffer) throws EBusControllerException;

    /**
     * Add an eBUS listener to receive valid eBus telegrams
     *
     * @param listener
     */
    void addEBusEventListener(IEBusConnectorEventListener listener);

    /**
     * Remove an eBUS listener
     *
     * @param listener
     * @return
     */
    boolean removeEBusEventListener(IEBusConnectorEventListener listener);

    boolean isRunning();

    void setWatchdogTimerTimeout(int seconds);

    long getLastSendReceiveRoundtripTime();

    void start();

    boolean isInterrupted();

    void interrupt();
}