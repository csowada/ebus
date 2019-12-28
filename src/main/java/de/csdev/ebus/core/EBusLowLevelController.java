/**
 * Copyright (c) 2016-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core;

import java.io.IOException;
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
public class EBusLowLevelController extends EBusControllerBase {

    private static final Logger logger = LoggerFactory.getLogger(EBusLowLevelController.class);

    protected IEBusConnection connection;

    /** counts the re-connection tries */
    private int reConnectCounter = 0;

    private long sendRoundTrip = -1;

    public EBusLowLevelController(IEBusConnection connection) {
        super();
        this.connection = connection;
    }

    @Override
    public long getLastSendReceiveRoundtripTime() {
        return sendRoundTrip;
    }

    /**
     * @return
     * @throws EBusControllerException
     */
    public IEBusConnection getConnection() throws EBusControllerException {
        if (!isRunning()) {
            throw new EBusControllerException();
        }
        return connection;
    }

    /**
     * Called event if a packet has been received
     *
     * @throws IOException
     */
    private void onEBusDataReceived(byte data) throws IOException {

        if (!isRunning()) {
            logger.trace("Skip event, thread was interrupted ...");
            return;
        }

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

            // check if a complete and valid telegram is available
            if (machine.isTelegramAvailable()) {

                byte[] telegramData = machine.getTelegramData();

                // execute event
                fireOnEBusTelegramReceived(telegramData, null);
                machine.reset();
            }
        }

    }

    private void reconnect() throws IOException {

        if (!isRunning()) {
            logger.trace("Skip reconnect, thread was interrupted ...");
            return;
        }

        logger.info("Try to reconnect to eBUS adapter ...");

        if (reConnectCounter > 10) {
            reConnectCounter = -1;
            this.interrupt();

        } else {

            reConnectCounter++;

            logger.warn("Retry to connect to eBUS adapter in {} seconds ...", 5 * reConnectCounter);
            try {
                Thread.sleep(5000 * reConnectCounter);

                connection.close();
                if (connection.open()) {
                    resetWatchdogTimer();
                }

            } catch (InterruptedException e) {
                // noop, accept interruptions
            }
        }

    }

    /**
     * Resend data if it's the first try or call resetSend()
     *
     * @param secondTry
     * @return
     * @throws IOException
     */
    private boolean resend() throws IOException {

        if (isRunning() && !queue.getCurrent().secondTry) {
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
        }

        resetWatchdogTimer();

        // loop until interrupt or reconnector count is -1 (to many retries)
        while (!(isInterrupted() || reConnectCounter == -1)) {
            try {

                if (!connection.isOpen()) {
                    reconnect();

                } else {

                    // read byte from connector
                    read = connection.readBytes(buffer);

                    if (read == -1) {
                        logger.debug("eBUS read timeout occured, no data on bus ...");
                        throw new IOException("End of eBUS stream reached!");

                    } else {
                        for (int i = 0; i < read; i++) {
                            onEBusDataReceived(buffer[i]);

                        }

                        // reset with received data
                        resetWatchdogTimer();
                        reConnectCounter = 0;

                    }
                }

            } catch (IOException e) {
                logger.error("An IO exception has occured! Try to reconnect eBUS connector ...", e);
                fireOnConnectionException(e);

                try {
                    reconnect();
                } catch (IOException e1) {
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
        } // while loop

        dispose();
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

            readByte = (byte) (connection.readByte(true) & 0xFF);

            // clacule send receive roundtrip time in ns
            sendRoundTrip = System.nanoTime() - startTime;

            // update the state machine
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

    @Override
    protected void dispose() {

        logger.debug("eBUS connection thread is shuting down ...");

        super.dispose();

        // *******************************
        // ** end of thread **
        // *******************************

        // disconnect the connector e.g. close serial port
        try {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (IOException e) {
            logger.error(e.toString(), e);
        }
    }

    @Override
    protected void fireWatchDogTimer() {
        logger.warn("eBUS Watchdog Timer!");

        try {
            connection.close();
        } catch (IOException e) {
            logger.error("error!", e);
        }
    }

}
