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
    private final List<IEBusConnectorEventListener> listeners = new CopyOnWriteArrayList<IEBusConnectorEventListener>();

    /** The thread pool to execute events without blocking */
    private ExecutorService threadPool;

    /**
     * Add an eBUS listener to receive valid eBus telegrams
     *
     * @param listener
     */
    public void addEBusEventListener(IEBusConnectorEventListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove an eBUS listener
     *
     * @param listener
     * @return
     */
    public boolean removeEBusEventListener(IEBusConnectorEventListener listener) {
        return listeners.remove(listener);
    }

    /**
     * @param e
     */
    protected void fireOnConnectionException(final Exception e) {

        if (threadPool == null) {
            logger.warn("ThreadPool not ready!");
            return;
        }

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                for (IEBusConnectorEventListener listener : listeners) {
                    try {
                        listener.onConnectionException(e);
                    } catch (Exception e) {
                        logger.error("Error while firing onConnectionException events!", e);
                    }
                }

            }
        });
    }

    /**
     * Called if a valid eBus telegram was received. Send to event
     * listeners via thread pool to prevent blocking.
     *
     * @param receivedData
     * @param sendQueueId
     */
    protected void fireOnEBusTelegramReceived(final byte[] receivedData, final Integer sendQueueId) {

        if (threadPool == null) {
            logger.warn("ThreadPool not ready!  Can't fire onTelegramReceived events ...");
            return;
        }

        if (receivedData == null || receivedData.length == 0) {
            logger.warn("Telegram data is null or empty! Can't fire onTelegramReceived events ...");
            return;
        }

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                for (IEBusConnectorEventListener listener : listeners) {
                    try {
                        listener.onTelegramReceived(receivedData, sendQueueId);
                    } catch (Exception e) {
                        logger.error("Error while firing onTelegramReceived events!", e);
                    }
                }
            }
        });
    }

    /**
     * @param exception
     * @param sendQueueId
     */
    protected void fireOnEBusDataException(final EBusDataException exception, final Integer sendQueueId) {

        if (threadPool == null) {
            logger.warn("ThreadPool not ready!");
            return;
        }

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                for (IEBusConnectorEventListener listener : listeners) {
                    try {
                        listener.onTelegramException(exception, sendQueueId);
                    } catch (Exception e) {
                        logger.error("Error while firing onTelegramException events!", e);
                    }
                }
            }
        });
    }

    /**
     *
     */
    protected void initThreadPool() {
        // create new thread pool to send received telegrams
        threadPool = Executors.newCachedThreadPool(new EBusWorkerThreadFactory("ebus-receiver"));
    }

    /**
     *
     */
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

    public void dispose() {
        listeners.clear();
        shutdownThreadPool();
    }

}
