/**
 * Copyright (c) 2017-2023 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public abstract class EBusControllerBase extends Thread implements IEBusController {

    private static final Logger logger = LoggerFactory.getLogger(EBusControllerBase.class);

    private static final String THREADPOOL_NOT_READY = "ThreadPool not ready!";

    /** serial receive buffer */
    protected @NonNull EBusReceiveStateMachine machine = new EBusReceiveStateMachine();

    /** the list for listeners */
    private final @NonNull List<IEBusConnectorEventListener> listeners = new CopyOnWriteArrayList<>();

    /** The thread pool to execute events without blocking */
    private ExecutorService threadPool;

    private ScheduledExecutorService threadPoolWDT;

    private ScheduledFuture<?> watchdogTimer;

    private int watchdogTimerTimeout = 300; // 5min

    protected @NonNull EBusQueue queue = new EBusQueue();

    private @NonNull ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.IEBusController#addToSendQueue(byte[], int)
     */
    @Override
    public @NonNull Integer addToSendQueue(final byte @NonNull [] buffer, final int maxAttemps) throws EBusControllerException {
        if (getConnectionStatus() != ConnectionStatus.CONNECTED) {
            throw new EBusControllerException("Controller not connected, unable to add telegrams to send queue!");
        }

        Integer sendId = queue.addToSendQueue(buffer, maxAttemps);

        if (sendId == null) {
            throw new EBusControllerException("Unable to add telegrams to send queue!");
        }

        return sendId;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.IEBusController#addToSendQueue(byte[])
     */
    @Override
    public @NonNull Integer addToSendQueue(final byte @NonNull [] buffer) throws EBusControllerException {
        if (getConnectionStatus() != ConnectionStatus.CONNECTED) {
            throw new EBusControllerException("Controller not connected, unable to add telegrams to send queue!");
        }

        Integer sendId = queue.addToSendQueue(buffer);

        if (sendId == null) {
            throw new EBusControllerException("Unable to add telegrams to send queue!");
        }

        return sendId;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.IEBusController#addEBusEventListener(de.csdev.ebus.core.IEBusConnectorEventListener)
     */
    @Override
    public void addEBusEventListener(final @NonNull IEBusConnectorEventListener listener) {
        Objects.requireNonNull(listener);
        listeners.add(listener);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.IEBusController#removeEBusEventListener(de.csdev.ebus.core.IEBusConnectorEventListener)
     */
    @Override
    public boolean removeEBusEventListener(final @NonNull IEBusConnectorEventListener listener) {
        Objects.requireNonNull(listener);
        return listeners.remove(listener);
    }

    /**
     * @param e
     */
    protected void fireOnConnectionException(final @NonNull Exception e) {

        Objects.requireNonNull(e);

        if (!isRunning()) {
            return;
        }

        if (threadPool == null || threadPool.isTerminated()) {
            logger.warn(THREADPOOL_NOT_READY);
            return;
        }

        threadPool.execute(() -> {
            for (IEBusConnectorEventListener listener : listeners) {
                if (!Thread.interrupted()) {
                    try {
                        listener.onConnectionException(e);
                    } catch (Exception e1) {
                        logger.error("Error while firing onConnectionException events!", e1);
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
    protected void fireOnEBusTelegramReceived(final byte @NonNull [] receivedData, final Integer sendQueueId) {

        if (!isRunning()) {
            return;
        }

        if (threadPool == null || threadPool.isTerminated()) {
            logger.warn(THREADPOOL_NOT_READY + " Can't fire onTelegramReceived events ...");
            return;
        }

        if (receivedData.length == 0) {
            logger.warn("Telegram data is empty! Can't fire onTelegramReceived events ...");
            return;
        }

        threadPool.execute(() -> {
            for (IEBusConnectorEventListener listener : listeners) {
                if (!Thread.interrupted()) {
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
    protected void fireOnEBusDataException(final @NonNull EBusDataException exception, final Integer sendQueueId) {

        Objects.requireNonNull(exception);

        if (!isRunning()) {
            return;
        }

        if (threadPool == null || threadPool.isTerminated()) {
            logger.warn(THREADPOOL_NOT_READY);
            return;
        }

        threadPool.execute(() -> {
            for (IEBusConnectorEventListener listener : listeners) {
                if (!Thread.interrupted()) {
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
    protected void fireOnEBusConnectionStatusChange(final @NonNull ConnectionStatus status) {

        Objects.requireNonNull(status);

        if (!isRunning()) {
            return;
        }

        if (threadPool == null || threadPool.isTerminated()) {
            logger.warn(THREADPOOL_NOT_READY);
            return;
        }

        threadPool.execute(() -> {
            for (IEBusConnectorEventListener listener : listeners) {
                try {
                    listener.onConnectionStatusChanged(status);
                } catch (Exception e) {
                    logger.error("Error while firing fireOnEBusConnectionStatusChange events!", e);
                }
            }
        });
    }

    /**
     *
     */
    protected void initThreadPool() {
        // create new thread pool to send received telegrams
        // limit the number of threads to 30
        threadPool = new ThreadPoolExecutor(5, 60, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(),
                new EBusWorkerThreadFactory("ebus-receiver", true));

        // create watch dog thread pool
        threadPoolWDT = Executors.newSingleThreadScheduledExecutor(new EBusWorkerThreadFactory("ebus-wdt", false));
    }

    /**
     * @throws InterruptedException
     *
     */
    protected void shutdownThreadPool() throws InterruptedException {
        // shutdown threadpool
        if (threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdownNow();
        }

        if (threadPoolWDT != null && !threadPoolWDT.isShutdown()) {
            threadPoolWDT.shutdownNow();
        }

        if (threadPool != null) {
            // wait up to 10sec. for the thread pool
            threadPool.awaitTermination(10, TimeUnit.SECONDS);
            threadPool = null;
        }

        if (threadPoolWDT != null) {
            // wait up to 10sec. for the thread pool
            threadPoolWDT.awaitTermination(10, TimeUnit.SECONDS);
            threadPoolWDT = null;
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

    protected void dispose() throws InterruptedException {

        listeners.clear();

        if (watchdogTimer != null) {
            watchdogTimer.cancel(true);
            watchdogTimer = null;
        }

        shutdownThreadPool();
    }

    protected void resetWatchdogTimer() {
        Runnable r = EBusControllerBase.this::fireWatchDogTimer;

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
    public void setWatchdogTimerTimeout(final int seconds) {
        watchdogTimerTimeout = seconds;
    }

    protected abstract void fireWatchDogTimer();

    protected void setConnectionStatus(final @NonNull ConnectionStatus status) {

        Objects.requireNonNull(status, "status");


        // only run on a real status change
        if (this.connectionStatus != status) {
            this.connectionStatus = status;
            fireOnEBusConnectionStatusChange(status);
        }
    }

    @Override
    public @NonNull ConnectionStatus getConnectionStatus() {
        return this.connectionStatus;
    }

    @Override
    public void run() {
        throw new IllegalStateException("Method run() should be overwritten!");
    }
}
