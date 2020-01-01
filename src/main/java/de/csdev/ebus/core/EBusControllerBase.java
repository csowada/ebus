/**
 * Copyright (c) 2016-2019 by the respective copyright holders.
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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public abstract class EBusControllerBase extends Thread implements IEBusController {

    private static final Logger logger = LoggerFactory.getLogger(EBusControllerBase.class);

    /** serial receive buffer */
    protected EBusReceiveStateMachine machine = new EBusReceiveStateMachine();

    /** the list for listeners */
    private final List<IEBusConnectorEventListener> listeners = new CopyOnWriteArrayList<IEBusConnectorEventListener>();

    /** The thread pool to execute events without blocking */
    private ExecutorService threadPool;

    private ScheduledExecutorService threadPoolWDT;

    private ScheduledFuture<?> watchdogTimer;

    private int watchdogTimerTimeout = 300; // 5min

    protected EBusQueue queue = new EBusQueue();

    private ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.IEBusController#addToSendQueue(byte[], int)
     */
    @Override
    public Integer addToSendQueue(byte[] buffer, int maxAttemps) throws EBusControllerException {
        if (!isRunning()) {
            throw new EBusControllerException();
        }
        return queue.addToSendQueue(buffer, maxAttemps);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.IEBusController#addToSendQueue(byte[])
     */
    @Override
    public Integer addToSendQueue(byte[] buffer) throws EBusControllerException {
        if (!isRunning()) {
            throw new EBusControllerException();
        }
        return queue.addToSendQueue(buffer);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.IEBusController#addEBusEventListener(de.csdev.ebus.core.IEBusConnectorEventListener)
     */
    @Override
    public void addEBusEventListener(IEBusConnectorEventListener listener) {
        listeners.add(listener);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.IEBusController#removeEBusEventListener(de.csdev.ebus.core.IEBusConnectorEventListener)
     */
    @Override
    public boolean removeEBusEventListener(IEBusConnectorEventListener listener) {
        return listeners.remove(listener);
    }

    /**
     * @param e
     */
    protected void fireOnConnectionException(final Exception e) {

        if (!isRunning()) {
            return;
        }

        if (threadPool == null || threadPool.isTerminated()) {
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

        if (!isRunning()) {
            return;
        }

        if (threadPool == null || threadPool.isTerminated()) {
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

        if (!isRunning()) {
            return;
        }

        if (threadPool == null || threadPool.isTerminated()) {
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
     * @param status
     */
    protected void fireOnEBusConnectionStatusChange(ConnectionStatus status) {

        if (!isRunning()) {
            return;
        }

        if (threadPool == null || threadPool.isTerminated()) {
            logger.warn("ThreadPool not ready!");
            return;
        }

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                for (IEBusConnectorEventListener listener : listeners) {
                    try {
                        listener.onConnectionStatusChanged(status);
                    } catch (Exception e) {
                        logger.error("Error while firing fireOnEBusConnectionStatusChange events!", e);
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
        threadPool = Executors.newCachedThreadPool(new EBusWorkerThreadFactory("ebus-receiver", true));

        // create watch dog thread pool
        threadPoolWDT = Executors.newSingleThreadScheduledExecutor(new EBusWorkerThreadFactory("ebus-wdt", false));
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

        if (threadPoolWDT != null && !threadPoolWDT.isShutdown()) {
            threadPoolWDT.shutdownNow();
            try {
                // wait up to 10sec. for the thread pool
                threadPoolWDT.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.IEBusController#isRunning()
     */
    @Override
    public boolean isRunning() {
        return !isInterrupted() && isAlive();
    }

    protected void dispose() {

        listeners.clear();

        if (watchdogTimer != null) {
            watchdogTimer.cancel(true);
            watchdogTimer = null;
        }

        shutdownThreadPool();

        queue = null;
        machine = null;
    }

    protected void resetWatchdogTimer() {

        // logger.info("wdt runn ...");

        Runnable r = new Runnable() {

            @Override
            public void run() {
                EBusControllerBase.this.fireWatchDogTimer();
                // EBusControllerBase.logger.warn("eBUS Watchdog Timer!");
                //
                // try {
                // EBusControllerBase.this.connection.close();
                // } catch (IOException e) {
                // logger.error("error!", e);
                // }
            }

        };

        if (watchdogTimer != null && !watchdogTimer.isCancelled()) {
            watchdogTimer.cancel(true);
        }

        if (!threadPoolWDT.isShutdown()) {
            watchdogTimer = threadPoolWDT.schedule(r, watchdogTimerTimeout, TimeUnit.SECONDS);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.IEBusController#setWatchdogTimerTimeout(int)
     */
    @Override
    public void setWatchdogTimerTimeout(int seconds) {
        watchdogTimerTimeout = seconds;
    }

    protected void fireWatchDogTimer() {
        // EBusControllerBase.logger.warn("eBUS Watchdog Timer!");
        //
        // try {
        // EBusControllerBase.this.connection.close();
        // } catch (IOException e) {
        // logger.error("error!", e);
        // }
    }

    protected void setConnectionStatus(ConnectionStatus status) {
        this.connectionStatus = status;
        fireOnEBusConnectionStatusChange(status);
    }

    @Override
    public ConnectionStatus getConnectionStatus() {
        return this.connectionStatus;
    }
}
