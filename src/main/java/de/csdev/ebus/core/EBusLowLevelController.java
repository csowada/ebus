/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.BufferOverflowException;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.core.EBusQueue.QueueEntry;
import de.csdev.ebus.core.connection.IEBusConnection;
import de.csdev.ebus.utils.EBusUtils;

/**
 * Low-level implementation of an eBUS controller that handles direct
 * communication
 * with the physical eBUS interface. This controller manages:
 * - Raw data transmission and reception
 * - Connection management and auto-reconnection
 * - Collision detection and handling
 * - Telegram validation and processing
 * - Synchronization and timing
 *
 * @author Christian Sowada - Initial contribution
 */
public class EBusLowLevelController extends EBusControllerBase {

    private static final Logger logger = LoggerFactory.getLogger(EBusLowLevelController.class);

    /** Maximum number of reconnection attempts before giving up */
    private static final int MAX_RECONNECT_ATTEMPTS = 10;

    /** Base delay in milliseconds between reconnection attempts */
    private static final long BASE_RECONNECT_DELAY = 5000L;

    /** Size of the receive buffer */
    private static final int RECEIVE_BUFFER_SIZE = 100;

    /** Default value for no round trip time measured */
    private static final long NO_ROUNDTRIP_TIME = -1L;

    /** The physical eBUS connection */
    protected @NonNull IEBusConnection connection;

    /** Counter for connection retry attempts */
    private int reConnectCounter = 0;

    /** Last measured send-receive round trip time in nanoseconds */
    private long sendRoundTrip = NO_ROUNDTRIP_TIME;

    /**
     * Creates a new eBUS low-level controller with the specified connection.
     *
     * @param connection The physical eBUS connection to use
     * @throws NullPointerException if connection is null
     */
    public EBusLowLevelController(@NonNull IEBusConnection connection) {
        super();
        this.connection = Objects.requireNonNull(connection, "connection cannot be null");
        logger.debug("Created new eBUS controller with connection type: {}",
                connection.getClass().getSimpleName());
    }

    /**
     * Returns the last measured send-receive round trip time in nanoseconds.
     *
     * @return The last measured round trip time, or -1 if no measurement is
     *         available
     */
    @Override
    public long getLastSendReceiveRoundtripTime() {
        return sendRoundTrip;
    }

    /**
     * Gets the current eBUS connection instance.
     * 
     * @return The current eBUS connection
     * @throws EBusControllerException if the controller is not running
     */
    public @NonNull IEBusConnection getConnection() throws EBusControllerException {
        if (!isRunning()) {
            throw new EBusControllerException("Cannot access connection - controller is not running");
        }
        return connection;
    }

    /**
     * Processes received eBUS data bytes.
     * Handles data reception, state machine updates, and telegram processing.
     * This method is called for each byte received from the eBUS.
     *
     * @param data The received byte from the eBUS
     * @throws IOException if an I/O error occurs during processing
     */
    private void onEBusDataReceived(byte data) throws IOException {
        if (!isRunning()) {
            logger.trace("Skipping data processing - controller interrupted");
            return;
        }

        try {
            // Update state machine with received byte
            machine.update(data);

            if (machine.isWaitingForSlaveAnswer()) {
                logger.trace("Awaiting slave response");
            }

            // Process SYN byte reception
            if (machine.isSync()) {
                processSyncReceived();
            }
        } catch (EBusDataException e) {
            logger.debug("Data exception during processing: {}", e.getMessage());
            fireOnEBusDataException(e, e.getSendId());
        }
    }

    /**
     * Handles the reception of a SYN byte, which marks potential telegram
     * boundaries.
     * This includes sending queued data and processing complete telegrams.
     *
     * @throws IOException if an I/O error occurs during processing
     */
    private void processSyncReceived() throws IOException {
        try {
            // Attempt to send queued data
            send(false);

            // Check send queue status
            queue.checkSendStatus(false);

            // Process complete telegram if available
            if (machine.isTelegramAvailable()) {
                byte[] telegramData = machine.getTelegramData();

                if (logger.isDebugEnabled()) {
                    logger.debug("Complete telegram received: {}",
                            EBusUtils.toHexDumpString(telegramData));
                }

                fireOnEBusTelegramReceived(telegramData, null);
                machine.reset();
            }
        } catch (EBusDataException e) {
            logger.debug("Data exception during sync processing: {}", e.getMessage());
            fireOnEBusDataException(e, e.getSendId());
        }
    }

    /**
     * Attempts to reconnect to the eBUS adapter using an exponential backoff
     * strategy.
     * After MAX_RECONNECT_ATTEMPTS failed attempts, the controller will be
     * interrupted.
     *
     * @throws IOException          if connection operations fail
     * @throws InterruptedException if the thread is interrupted during reconnection
     */
    private void reconnect() throws IOException, InterruptedException {
        if (!isRunning()) {
            logger.trace("Skip reconnect, thread was interrupted");
            return;
        }

        logger.info("Attempting to reconnect to eBUS adapter");
        setConnectionStatus(ConnectionStatus.CONNECTING);

        if (reConnectCounter > MAX_RECONNECT_ATTEMPTS) {
            logger.error("Maximum reconnection attempts ({}) exceeded, shutting down controller",
                    MAX_RECONNECT_ATTEMPTS);
            reConnectCounter = -1;
            this.interrupt();
            return;
        }

        reConnectCounter++;
        long delayMillis = BASE_RECONNECT_DELAY * reConnectCounter;

        logger.warn("Will retry connection in {} seconds (attempt {}/{})",
                delayMillis / 1000, reConnectCounter, MAX_RECONNECT_ATTEMPTS);

        try {
            Thread.sleep(delayMillis);

            // Close existing connection before attempting to open a new one
            connection.close();

            if (connection.open()) {
                logger.info("Successfully reconnected to eBUS adapter");
                resetWatchdogTimer();
            } else {
                logger.warn("Failed to open connection on attempt {}", reConnectCounter);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        }
    }

    /**
     * Attempts to resend the current telegram if no previous retry has been made.
     * Implements a single-retry policy for failed transmissions.
     *
     * @return true if the telegram will be retried, false if no retry is possible
     */
    private boolean resend() {
        QueueEntry entry = queue.getCurrent();

        if (!isRunning() || entry == null) {
            logger.debug("Cannot resend - controller not running or no current entry");
            return false;
        }

        if (entry.secondTry) {
            logger.warn("Maximum retry attempts reached for telegram ID {}, removing from queue",
                    entry.id);
            queue.resetSendQueue();
            return false;
        }

        entry.secondTry = true;
        logger.debug("Scheduling retry for telegram ID {}", entry.id);
        return true;
    }

    /**
     * Main controller loop that handles the eBUS communication.
     * Manages connection state, data reception, and error recovery.
     */
    @Override
    @SuppressWarnings("java:S3776")
    public void run() {
        logger.info("Starting eBUS low level controller");

        // Initialize thread pools for event handling
        initThreadPool();

        int bytesRead = -1;
        byte[] receiveBuffer = new byte[RECEIVE_BUFFER_SIZE];

        // Initialize connection if needed
        try {
            if (!connection.isOpen()) {
                logger.debug("Opening initial connection to eBUS");
                setConnectionStatus(ConnectionStatus.CONNECTING);
                connection.open();
            }
        } catch (IOException e) {
            logger.error("Failed to establish initial connection: {}", e.getMessage(), e);
            fireOnConnectionException(e);
        }

        resetWatchdogTimer();

        // Main control loop - continue until interrupted or max reconnection attempts
        // exceeded
        while (!Thread.interrupted() && reConnectCounter != -1) {
            try {
                if (!connection.isOpen()) {
                    reconnect();
                } else {
                    // Connection is established
                    setConnectionStatus(ConnectionStatus.CONNECTED);

                    // Read data from the bus
                    bytesRead = connection.readBytes(receiveBuffer);

                    if (bytesRead == -1) {
                        logger.debug("eBUS read timeout occurred, no data on bus");
                        throw new IOException("End of eBUS stream reached");
                    }

                    // Process received bytes
                    for (int i = 0; i < bytesRead; i++) {
                        onEBusDataReceived(receiveBuffer[i]);
                    }

                    // Reset watchdog and connection counter on successful read
                    resetWatchdogTimer();
                    reConnectCounter = 0;
                }

            } catch (InterruptedIOException | InterruptedException e) {
                logger.debug("Controller interrupted, stopping main loop");
                Thread.currentThread().interrupt();

            } catch (IOException e) {
                logger.error("IO exception occurred - attempting to reconnect: {}", e.getMessage(), e);
                fireOnConnectionException(e);

                try {
                    reconnect();
                } catch (IOException reconnectError) {
                    logger.error("Failed to reconnect: {}", reconnectError.getMessage(), reconnectError);
                } catch (InterruptedException interrupted) {
                    logger.debug("Reconnection attempt interrupted");
                    Thread.currentThread().interrupt();
                }

            } catch (BufferOverflowException e) {
                logger.error("eBUS telegram buffer overflow - insufficient sync bytes received. " +
                        "Consider adjusting eBUS adapter settings.");
                // store nano time to measure send receive roundtrip time

            } catch (Exception e) {
                logger.error("Unexpected error in main loop: {}", e.getMessage(), e);
                machine.reset();
            }
        }

        logger.info("eBUS controller main loop terminated, performing cleanup");

        try {
            dispose();
        } catch (InterruptedException e) {
            logger.error("Interrupted during controller cleanup", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Internal send function. Send and read to detect byte collisions.
     *
     * @param secondTry
     * @throws IOException
     */
    private void send(boolean secondTry) throws IOException {

        if (!isRunning()) {
            // this controller is disposed
            logger.trace("Skip send, thread was interrupted ...");
            return;
        }

        if (!connection.isReceiveBufferEmpty()) {
            logger.trace("Receive buffer still not empty, skip ...");
            return;
        }

        QueueEntry sendEntry = queue.getCurrent();

        if (sendEntry == null) {
            return;
        }

        try {

            byte[] dataOutputBuffers = sendEntry.buffer;
            EBusReceiveStateMachine sendMachine = new EBusReceiveStateMachine();

            logger.debug("Send: {} @ {}. attempt", EBusUtils.toHexDumpString(dataOutputBuffers),
                    sendEntry.sendAttempts);

            // start machine
            sendMachine.update(EBusConsts.SYN);

            // count as send attempt
            sendEntry.sendAttempts++;

            if (sendEntry.sendAttempts - 10 > sendEntry.maxAttemps) {
                logger.error("emergency break!!!!");
                queue.resetSendQueue();
                return;
            }

            int read = 0;
            byte readByte = 0;

            // clear input buffer to start by zero
            connection.reset();

            // send command
            byte b = dataOutputBuffers[0];

            if (logger.isTraceEnabled()) {
                logger.trace("Send {}", EBusUtils.toHexDumpString(b));
            }

            // store nao time to messure send receive roundtrip time
            long startTime = System.nanoTime();

            connection.writeByte(b);
            read = connection.readByte(true);
            readByte = (byte) (read & 0xFF);

            // calculate send receive roundtrip time in ns
            sendRoundTrip = System.nanoTime() - startTime;

            // update the state machine
            sendMachine.update(readByte);

            if (read == -1) {
                logger.warn("End of stream reached for first byte. Stop sending attempt ...");
                queue.setBlockNextSend(true);
                return;

            } else if (b != readByte) {

                // written and read byte not identical, that's
                // a collision
                if (readByte == EBusConsts.SYN) {
                    logger.debug("eBUS collision with SYN detected!");
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("eBUS collision detected! 0x{}", EBusUtils.toHexDumpString(readByte));
                    }
                }

                // last send try was a collision
                if (queue.isLastSendCollisionDetected()) {
                    logger.warn("A second collision occured!");
                    queue.resetSendQueue();
                }
                // priority class identical
                else if ((byte) (readByte & 0x0F) == (byte) (b & 0x0F)) {
                    logger.trace("Priority class match, restart after next SYN ...");
                    queue.setLastSendCollisionDetected(true);

                } else {
                    logger.trace("Priority class doesn't match, blocked for next SYN ...");
                    queue.setBlockNextSend(true);
                }

                // stop after a collision
                return;
            }

            // send rest of the buffer
            // time critical - no time to read !!!
            for (int i = 1; i < dataOutputBuffers.length; i++) {
                connection.writeByte(dataOutputBuffers[i]);
            }

            // master data transfer successful

            // reset global variables
            queue.setLastSendCollisionDetected(false);
            queue.setBlockNextSend(false);

            // nor read the written data from buffer
            for (int i = 1; i < dataOutputBuffers.length; i++) {
                read = connection.readByte(true);
                byte b0 = dataOutputBuffers[i];
                byte b1 = (byte) (read & 0xFF);

                if (logger.isTraceEnabled()) {
                    logger.trace("Send 0x{} -> Received 0x{}", EBusUtils.toHexDumpString(b0),
                            EBusUtils.toHexDumpString(b1));
                }

                if (read == -1) {
                    logger.warn("End of stream reached. Stop sending attempt ...");
                    queue.setBlockNextSend(true);
                    return;

                } else if (b0 != b1) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("Received byte 0x{} is not equal to send byte 0x{}! Stop send attempt ...",
                                EBusUtils.toHexDumpString(b1), EBusUtils.toHexDumpString(b0));
                    }

                    queue.setBlockNextSend(true);
                    return;
                }

                sendMachine.update(b1);
            }

            // read slave data if this is a master/slave telegram
            if (sendMachine.isWaitingForSlaveAnswer()) {
                logger.trace("Waiting for slave answer ...");

                logger.warn("eBUS Watchdog Timer triggered!");
                while (!sendMachine.isWaitingForMasterACK() && !sendMachine.isWaitingForMasterSYN()) {
                    read = connection.readByte(true);
                    if (read != -1) {
                        byte ack = (byte) (read & 0xFF);
                        sendMachine.update(ack);
                    }
                }
                logger.trace("Slave answer received ...");
            }

            // sende master ack
            if (sendMachine.isWaitingForMasterACK()) {
                logger.trace("Send Master ACK to Slave ...");
                connection.writeByte(EBusConsts.ACK_OK);
                byte b0 = (byte) (connection.readByte(true) & 0xFF);
                sendMachine.update(b0);
            }

            // sende master sync
            if (sendMachine.isWaitingForMasterSYN()) {
                logger.trace("Send SYN to bus ...");
                connection.writeByte(EBusConsts.SYN);
                byte b0 = (byte) (connection.readByte(true) & 0xFF);
                sendMachine.update(b0);
            }

            // after send process the received telegram
            if (sendMachine.isTelegramAvailable()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Succesful send: {}", sendMachine.toDumpString());
                }

                fireOnEBusTelegramReceived(sendMachine.getTelegramData(), sendEntry.id);
            }

            // reset send module
            queue.resetSendQueue();

        } catch (EBusDataException e) {
            this.fireOnEBusDataException(e, sendEntry.id);

            if (e.getErrorCode().equals(EBusDataException.EBusError.SLAVE_ACK_FAIL)) {
                // directly resend telegram (max. once), not on next send loop
                resend();
            }

        }
    }

    /**
     * Performs a clean shutdown of the controller.
     * Closes connections, updates status, and releases resources.
     *
     * @throws InterruptedException if interrupted during cleanup
     */
    @Override
    protected void dispose() throws InterruptedException {
        logger.info("Shutting down eBUS controller");

        try {
            // Update status first to prevent new operations
            setConnectionStatus(ConnectionStatus.DISCONNECTED);

            // Clean up base class resources
            super.dispose();

            // Close physical connection
            if (connection != null) {
                logger.debug("Closing eBUS connection");
                try {
                    connection.close();
                } catch (IOException e) {
                    logger.error("Error closing eBUS connection: {}", e.getMessage(), e);
                }
            }

            logger.info("eBUS controller shutdown complete");
        } catch (Exception e) {
            logger.error("Error during controller disposal: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Handles watchdog timer expiration.
     * Forces a connection close to trigger reconnection on timeout.
     */
    @Override
    protected void fireWatchDogTimer() {
        logger.warn("Watchdog timer expired - forcing connection reset");

        try {
            logger.debug("Closing connection due to watchdog timeout");
            connection.close();
        } catch (IOException e) {
            logger.error("Error closing connection on watchdog timeout: {}", e.getMessage(), e);
        }

        // Reset send round trip time measurement
        sendRoundTrip = NO_ROUNDTRIP_TIME;
    }
}
