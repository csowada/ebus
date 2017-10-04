/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.core.EBusQueue.QueueEntry;
import de.csdev.ebus.core.connection.IEBusConnection;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusController extends EBusControllerBase {

    private static final Logger logger = LoggerFactory.getLogger(EBusController.class);

    private IEBusConnection connection;

    /** serial receive buffer */
    private EBusReceiveStateMachine machine = new EBusReceiveStateMachine();

    private EBusQueue queue = new EBusQueue();

    /** counts the re-connection tries */
    private int reConnectCounter = 0;

    public EBusController(IEBusConnection connection) {
        this.connection = connection;
    }

    public Integer addToSendQueue(byte[] buffer, int maxAttemps) {
        return queue.addToSendQueue(buffer, maxAttemps);
    }

    /**
     * @param buffer
     * @return
     */
    public Integer addToSendQueue(byte[] buffer) {
        return queue.addToSendQueue(buffer);
    }

    /**
     * @return
     */
    public IEBusConnection getConnection() {
        return connection;
    }

    /**
     * Called event if a packet has been received
     *
     * @throws IOException
     */
    private void onEBusDataReceived(byte data) throws IOException {

        try {
            machine.update(data);
        } catch (EBusDataException e) {
            this.fireOnEBusDataException(e, e.getSendId());
        }

        if (machine.isWaitingForSlaveAnswer()) {
            logger.trace("waiting for slave answer ...");
        }

        // we received a SYN byte
        if (machine.isSync()) {

            // try to send something if the send queue is not empty
            send(false);

            // afterwards check for next sending slot
            try {
                queue.checkSendStatus();
            } catch (EBusDataException e) {
                fireOnEBusDataException(e, e.getSendId());
            }

            // check of a complete and valid telegram is available
            if (machine.isTelegramAvailable()) {

                byte[] telegramData = machine.getTelegramData();

                // execute event
                fireOnEBusTelegramReceived(telegramData, null);
                machine.reset();
            }
        }

    }

    private boolean reconnect() throws IOException, InterruptedException {
        logger.info("Try to reconnect to eBUS adapter ...");

        if (reConnectCounter > 10) {
            return false;
        }

        reConnectCounter++;

        if (!connection.isOpen()) {
            if (connection.open()) {
                reConnectCounter = 0;
            } else {
                logger.warn("Retry to connect to eBUS adapter in {} seconds ...", 5 * reConnectCounter);
                Thread.sleep(5000 * reConnectCounter);
            }
        }

        return true;
    }

    /**
     * Resend data if it's the first try or call resetSend()
     *
     * @param secondTry
     * @return
     * @throws IOException
     */
    private boolean resend() throws IOException {
        if (!queue.getCurrent().secondTry) {
            queue.getCurrent().secondTry = true;
            return true;

        } else {
            logger.warn("Resend failed, remove data from sending queue ...");
            queue.resetSendQueue();
            return false;
        }
    }

    @Override
    public void run() {

        initThreadPool();

        int read = -1;

        byte[] buffer = new byte[100];

        try {
            if (!connection.isOpen()) {
                connection.open();
            }
        } catch (IOException e) {
            fireOnConnectionException(e);
            logger.error("error!", e);
            // interrupt();
        }

        // loop until interrupt or reconnector count is -1 (to many retries)
        while (!isInterrupted() || reConnectCounter == -1) {
            try {

                if (!connection.isOpen()) {
                    if (!reconnect()) {

                        // end thread !!
                        interrupt();
                    }

                } else {

                    // read byte from connector
                    read = connection.readBytes(buffer);

                    if (read == -1) {
                        logger.debug("eBUS read timeout occured, no data on bus ...");
                        Thread.sleep(500);

                    } else {

                        for (int i = 0; i < read; i++) {
                            onEBusDataReceived(buffer[i]);

                        }

                    }
                }
            } catch (InterruptedIOException e) {
                Thread.currentThread().interrupt();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

            } catch (IOException e) {
                fireOnConnectionException(e);
                logger.error("An IO exception has occured! Try to reconnect eBUS connector ...", e);

                try {
                    reconnect();
                } catch (IOException e1) {
                    logger.error(e.toString(), e1);
                } catch (InterruptedException e1) {
                    logger.error(e.toString(), e1);
                }

            } catch (BufferOverflowException e) {
                logger.error(
                        "eBUS telegram buffer overflow - not enough sync bytes received! Try to adjust eBUS adapter.");
                machine.reset();

            } catch (Exception e) {
                logger.error(e.toString(), e);
                machine.reset();
            }
        }

        logger.debug("eBUS connection thread is shuting down ...");

        // *******************************
        // ** end of thread **
        // *******************************

        // shutdown threadpool
        shutdownThreadPool();

        // disconnect the connector e.g. close serial port
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (IOException e) {
            logger.error(e.toString(), e);
        }
    }

    /**
     * Internal send function. Send and read to detect byte collisions.
     *
     * @param secondTry
     * @throws IOException
     */
    private void send(boolean secondTry) throws IOException {

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
            // for (int i = 0; i < dataOutputBuffers.length; i++) {
            byte b = dataOutputBuffers[0];

            logger.trace("Send {}", b);
            connection.writeByte(b);

            // if (i == 0) {

            readByte = (byte) (connection.readByte(true) & 0xFF);
            sendMachine.update(readByte);

            if (b != readByte) {

                // written and read byte not identical, that's
                // a collision
                if (readByte == EBusConsts.SYN) {
                    logger.debug("eBUS collision with SYN detected!");
                } else {
                    logger.debug("eBUS collision detected! 0x{}", EBusUtils.toHexDumpString(readByte));
                }

                // last send try was a collision
                if (queue.isLastSendCollisionDetected()) {
                    logger.warn("A second collision occured!");
                    queue.resetSendQueue();
                    return;
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
            for (int i = 1; i < dataOutputBuffers.length; i++) {
                byte b0 = dataOutputBuffers[i];

                logger.trace("Send {}", EBusUtils.toHexDumpString(b0));
                connection.writeByte(b0);

            }

            // start of transfer successful

            // reset global variables
            queue.setLastSendCollisionDetected(false);
            queue.setBlockNextSend(false);

            // read all send bytes from input buffer
            for (int i = 0; i < dataOutputBuffers.length - 1; i++) {
                byte b0 = (byte) (connection.readByte(true) & 0xFF);
                sendMachine.update(b0);
            }

            // read slave data if this is a master/slave telegram
            if (sendMachine.isWaitingForSlaveAnswer()) {
                logger.trace("Waiting for slave answer ...");

                // read input data until the telegram is complete or fails
                while (!sendMachine.isWaitingForMasterACK()) {
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
                logger.debug("Succesful send: {}", sendMachine.toDumpString());
                fireOnEBusTelegramReceived(sendMachine.getTelegramData(), sendEntry.id);
            }

            // reset send module
            queue.resetSendQueue();

        } catch (EBusDataException e) {

            // logger.error(e.getLocalizedMessage());

            this.fireOnEBusDataException(e, sendEntry.id);

            if (e.getErrorCode().equals(EBusDataException.EBusError.SLAVE_ACK_FAIL)) {
                // directly resend telegram (max. once), not on next send loop
                resend();
            }

        }
    }
}
