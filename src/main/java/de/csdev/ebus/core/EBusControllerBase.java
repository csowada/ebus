/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
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
 * Base implementation of an eBus controller that handles the core functionality
 * of eBus communication, including event handling, connection management,
 * and telegram processing.
 * 
 * This class provides:
 * - Event listener management
 * - Thread pool management for event processing
 * - Connection status handling
 * - Watchdog timer functionality
 * - Queue management for sending/receiving telegrams
 *
 * @author Christian Sowada - Initial contribution
 */
public abstract class EBusControllerBase extends Thread implements IEBusController {

    private static final Logger logger = LoggerFactory.getLogger(EBusControllerBase.class);

    private static final String THREADPOOL_NOT_READY = "ThreadPool not ready!";
    private static final int DEFAULT_CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 60;
    private static final long KEEP_ALIVE_TIME = 60L;
    private static final int DEFAULT_WATCHDOG_TIMEOUT = 300; // 5 minutes
    private static final int THREAD_POOL_TERMINATION_TIMEOUT = 10;

    /** Serial receive buffer for processing incoming data */
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

    /**
     * Adds a telegram to the send queue with a specified maximum number of retry attempts.
     * 
     * @param buffer The telegram data to send
     * @param maxAttempts The maximum number of retry attempts
     * @return A unique identifier for the queued telegram
     * @throws EBusControllerException if the controller is not connected or the queue operation fails
     * @throws NullPointerException if buffer is null
     */
    @Override
    public @NonNull Integer addToSendQueue(final byte @NonNull [] buffer, final int maxAttempts) throws EBusControllerException {
        Objects.requireNonNull(buffer, "buffer cannot be null");
        if (maxAttempts <= 0) {
            throw new IllegalArgumentException("maxAttempts must be greater than 0");
        }
        
        validateConnectionStatus();
        
        Integer sendId = queue.addToSendQueue(buffer, maxAttempts);
        if (sendId == null) {
            throw new EBusControllerException("Failed to add telegram to send queue");
        }
        
        logger.debug("Added telegram to send queue with ID {} and {} max attempts", sendId, maxAttempts);
        return sendId;
    }

    /**
     * Adds a telegram to the send queue using default retry attempts.
     * 
     * @param buffer The telegram data to send
     * @return A unique identifier for the queued telegram
     * @throws EBusControllerException if the controller is not connected or the queue operation fails
     * @throws NullPointerException if buffer is null
     */
    @Override
    public @NonNull Integer addToSendQueue(final byte @NonNull [] buffer) throws EBusControllerException {
        Objects.requireNonNull(buffer, "buffer cannot be null");
        
        validateConnectionStatus();
        
        Integer sendId = queue.addToSendQueue(buffer);
        if (sendId == null) {
            throw new EBusControllerException("Failed to add telegram to send queue");
        }
        
        logger.debug("Added telegram to send queue with ID {}", sendId);
        return sendId;
    }
    
    /**
     * Validates that the controller is in a connected state.
     * 
     * @throws EBusControllerException if the controller is not connected
     */
    private void validateConnectionStatus() throws EBusControllerException {
        if (getConnectionStatus() != ConnectionStatus.CONNECTED) {
            throw new EBusControllerException("Controller is not connected - current status: " + getConnectionStatus());
        }
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
     * Notifies all registered listeners about a connection exception.
     * The notification is dispatched asynchronously via the thread pool.
     * If the controller is not running or the thread pool is not available,
     * the event will not be fired.
     *
     * @param exception The connection exception that occurred
     * @throws NullPointerException if exception is null
     */
    protected void fireOnConnectionException(final @NonNull Exception exception) {
        Objects.requireNonNull(exception, "exception cannot be null");

        if (!isRunning() || !validateThreadPool()) {
            logger.warn("Cannot fire connection exception event - controller not running or thread pool not ready");
            return;
        }

        threadPool.execute(() -> {
            String exceptionName = exception.getClass().getSimpleName();
            logger.debug("Firing connection exception event for {} to {} listeners", 
                exceptionName, listeners.size());

            for (IEBusConnectorEventListener listener : listeners) {
                if (Thread.interrupted()) {
                    logger.debug("Connection exception event processing interrupted");
                    break;
                }
                try {
                    listener.onConnectionException(exception);
                } catch (Exception e) {
                    logger.error("Error in connection exception listener: {}", e.getMessage(), e);
                }
            }
        });
    }

    /**
     * Fires a telegram received event to all registered listeners.
     * The event is dispatched asynchronously via the thread pool to prevent blocking.
     * Validates the state and data before firing the event.
     *
     * @param receivedData The received telegram data, must not be null or empty
     * @param sendQueueId The optional queue ID if this was a response to a sent telegram
     */
    protected void fireOnEBusTelegramReceived(final byte @NonNull [] receivedData, final Integer sendQueueId) {
        Objects.requireNonNull(receivedData, "receivedData cannot be null");

        if (!isRunning() || !validateThreadPool() || receivedData.length == 0) {
            logger.warn("Cannot fire telegram received event - controller not running or invalid data");
            return;
        }

        threadPool.execute(() -> {
            for (IEBusConnectorEventListener listener : listeners) {
                if (Thread.interrupted()) {
                    break;
                }
                try {
                    listener.onTelegramReceived(receivedData, sendQueueId);
                } catch (Exception e) {
                    logger.error("Error in telegram received event listener", e);
                }
            }
        });
    }

    /**
     * Notifies all registered listeners about a data exception that occurred during
     * telegram processing. The notification is dispatched asynchronously via the thread pool.
     *
     * @param exception The data exception that occurred
     * @param sendQueueId The optional queue ID if this was related to a sent telegram
     * @throws NullPointerException if exception is null
     */
    protected void fireOnEBusDataException(final @NonNull EBusDataException exception, final Integer sendQueueId) {
        Objects.requireNonNull(exception, "exception cannot be null");

        if (!isRunning() || !validateThreadPool()) {
            logger.warn("Cannot fire data exception event - controller not running or thread pool not ready");
            return;
        }

        threadPool.execute(() -> {
            String exceptionMessage = exception.getMessage();
            logger.debug("Firing data exception event to {} listeners. Error: {}", 
                listeners.size(), exceptionMessage);

            for (IEBusConnectorEventListener listener : listeners) {
                if (Thread.interrupted()) {
                    logger.debug("Data exception event processing interrupted");
                    break;
                }
                try {
                    listener.onTelegramException(exception, sendQueueId);
                } catch (Exception e) {
                    logger.error("Error in data exception listener: {}", e.getMessage(), e);
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
    /**
     * Initializes the thread pools used for event processing and watchdog timer.
     * Creates a main thread pool for processing received telegrams and events,
     * and a single-threaded scheduled executor for the watchdog timer.
     */
    protected void initThreadPool() {
        // Create new thread pool to send received telegrams
        threadPool = new ThreadPoolExecutor(
            DEFAULT_CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            new EBusWorkerThreadFactory("ebus-receiver", true)
        );

        // Create watch dog thread pool
        threadPoolWDT = Executors.newSingleThreadScheduledExecutor(
            new EBusWorkerThreadFactory("ebus-wdt", false)
        );
    }

    /**
     * Shuts down both the main thread pool and watchdog timer thread pool.
     * Attempts graceful shutdown first, then forces shutdown if needed.
     * Waits for pool termination with a timeout.
     *
     * @throws InterruptedException if interrupted while waiting for thread pools to terminate
     */
    protected void shutdownThreadPool() throws InterruptedException {
        // Shutdown main thread pool
        if (threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdownNow();
            threadPool.awaitTermination(THREAD_POOL_TERMINATION_TIMEOUT, TimeUnit.SECONDS);
            threadPool = null;
        }

        // Shutdown watchdog thread pool
        if (threadPoolWDT != null && !threadPoolWDT.isShutdown()) {
            threadPoolWDT.shutdownNow();
            threadPoolWDT.awaitTermination(THREAD_POOL_TERMINATION_TIMEOUT, TimeUnit.SECONDS);
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

    /**
     * Disposes of this controller instance, cleaning up all resources.
     * This method:
     * - Clears all event listeners
     * - Cancels the watchdog timer
     * - Shuts down thread pools
     * - Resets internal state
     *
     * This method should be called when the controller is no longer needed.
     *
     * @throws InterruptedException if interrupted while shutting down thread pools
     */
    protected void dispose() throws InterruptedException {
        logger.debug("Disposing eBus controller...");
        
        synchronized (this) {
            // Clear all listeners first to prevent new events during shutdown
            if (!listeners.isEmpty()) {
                logger.debug("Removing {} event listeners", listeners.size());
                listeners.clear();
            }

            // Cancel watchdog timer
            if (watchdogTimer != null) {
                logger.debug("Cancelling watchdog timer");
                watchdogTimer.cancel(true);
                watchdogTimer = null;
            }

            // Reset connection status
            connectionStatus = ConnectionStatus.DISCONNECTED;

            // Clear the send queue
            queue = new EBusQueue();

            // Shutdown thread pools
            logger.debug("Shutting down thread pools");
            shutdownThreadPool();
        }
        
        logger.debug("eBus controller disposed successfully");
    }

    /**
     * Resets the watchdog timer with the current timeout value.
     * Cancels any existing timer and schedules a new one if the thread pool is active.
     * This method is thread-safe.
     */
    protected void resetWatchdogTimer() {
        synchronized (this) {
            if (threadPoolWDT == null || threadPoolWDT.isShutdown()) {
                logger.debug("Cannot reset watchdog timer - thread pool is not active");
                return;
            }

            // Cancel existing timer if present
            if (watchdogTimer != null && !watchdogTimer.isCancelled()) {
                watchdogTimer.cancel(true);
            }

            // Schedule new timer
            watchdogTimer = threadPoolWDT.schedule(
                this::fireWatchDogTimer,
                watchdogTimerTimeout,
                TimeUnit.SECONDS
            );
        }
    }

    /**
     * Sets the timeout duration for the watchdog timer.
     * 
     * @param seconds the timeout duration in seconds
     * @throws IllegalArgumentException if seconds is less than or equal to 0
     */
    @Override
    public void setWatchdogTimerTimeout(final int seconds) {
        if (seconds <= 0) {
            throw new IllegalArgumentException("Watchdog timeout must be greater than 0 seconds");
        }
        this.watchdogTimerTimeout = seconds;
    }

    /**
     * Called when the watchdog timer expires.
     * Subclasses must implement this method to handle watchdog timeouts.
     */
    protected abstract void fireWatchDogTimer();

    /**
     * Validates that the thread pool is ready to execute tasks.
     * 
     * @return true if the thread pool is initialized and not terminated
     */
    protected boolean validateThreadPool() {
        return threadPool != null && !threadPool.isTerminated();
    }

    /**
     * Updates the connection status and fires a status change event if needed.
     * Thread-safe implementation that ensures events are only fired for actual status changes.
     *
     * @param status the new connection status to set
     * @throws NullPointerException if status is null
     */
    protected void setConnectionStatus(final @NonNull ConnectionStatus status) {
        Objects.requireNonNull(status, "status cannot be null");

        // Only fire event on actual status change
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
        throw new IllegalStateException("Method run() must be overridden by subclasses");
    }
}
