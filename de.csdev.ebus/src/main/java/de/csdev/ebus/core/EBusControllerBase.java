/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public abstract class EBusControllerBase extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(EBusControllerBase.class);

    /** the list for listeners */
    private final List<EBusConnectorEventListener> listeners = new CopyOnWriteArrayList<EBusConnectorEventListener>();

    /** The thread pool to execute events without blocking */
    private ExecutorService threadPool;

    /**
     * Add an eBus listener to receive valid eBus telegrams
     *
     * @param listener
     */
    public void addEBusEventListener(EBusConnectorEventListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove an eBus listener
     *
     * @param listener
     * @return
     */
    public boolean removeEBusEventListener(EBusConnectorEventListener listener) {
        return listeners.remove(listener);
    }

    protected void fireOnConnectionException(final Exception e) {
        if (threadPool == null) {
            logger.warn("ThreadPool not ready!");
            return;
        }

        threadPool.execute(new Runnable() {
            public void run() {
                for (EBusConnectorEventListener listener : listeners) {
                    listener.onConnectionException(e);
                }
            }
        });
    }

    /**
     * Called if a valid eBus telegram was received. Send to event
     * listeners via thread pool to prevent blocking.
     *
     * @param telegram
     */
    protected void fireOnEBusTelegramReceived(final byte[] receivedRawData, final Integer sendQueueId) {

        if (threadPool == null) {
            logger.warn("ThreadPool not ready!");
            return;
        }

        threadPool.execute(new Runnable() {
            public void run() {

                // try {
                byte[] receivedData = null;// EBusUtils.decodeExpandedData(receivedRawData);
                receivedData = receivedRawData;

                if (receivedData != null) {
                    for (EBusConnectorEventListener listener : listeners) {
                        listener.onTelegramReceived(receivedData, sendQueueId);
                    }
                } else {
                    logger.debug("Received telegram was invalid, skip!");
                }

                // } catch (EBusDataException e) {
                //
                // logger.trace("error!", e);
                //
                // for (EBusConnectorEventListener listener : listeners) {
                // listener.onTelegramException(e, sendQueueId);
                // }
                // }

            }
        });
    }

    protected void initThreadPool() {
        // create new thread pool to send received telegrams
        threadPool = Executors.newCachedThreadPool(new EBusWorkerThreadFactory("ebus-send-receive"));
    }

    protected void shutdownThreadPool() {
        // shutdown threadpool
        if (threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdownNow();
            try {
                // wait up to 10sec. for the thread pool
                threadPool.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
            }
        }
    }

}
