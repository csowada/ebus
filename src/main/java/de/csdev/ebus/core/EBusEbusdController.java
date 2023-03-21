/**
 * Copyright (c) 2017-2023 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.core.EBusQueue.QueueEntry;
import de.csdev.ebus.utils.CommonsUtils;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusEbusdController extends EBusControllerBase {

    private static final Logger logger = LoggerFactory.getLogger(EBusEbusdController.class);

    /** The tcp socket */
    private Socket socket;

    /** The tcp hostname */
    private String hostname;

    /** The tcp port */
    private int port;

    private BufferedReader reader;

    private Writer writer;

    /** counts the re-connection tries */
    private int reConnectCounter = 0;

    /** is already in direct mode ? */
    private boolean directMode = false;

    private Thread senderThread = null;

    /** current send id (per thread iteration) */
    private Integer currentSendId = null;

    public EBusEbusdController(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public static String buildEbusdSendString(byte[] buffer) {
        final byte[] bs = Arrays.copyOfRange(buffer, 1, buffer.length - 1);

        final StringBuilder sb = new StringBuilder();
        sb.append("-s ");
        sb.append(EBusUtils.toHexDumpString(buffer[0]));
        sb.append(" ");
        sb.append(EBusUtils.toHexDumpString(bs).toString().replace(" ", ""));

        return sb.toString();
    }

    public class EBusSenderThread extends Thread {

        @Override
        public void run() {

            while (!Thread.currentThread().isInterrupted()) {

                try {

                    EBusQueue queue = EBusEbusdController.this.queue;

                    if (queue != null) {

                        QueueEntry queueEntry = queue.getCurrent();

                        if (!queue.isBlockSend() && queueEntry != null) {

                            // count as send attempt
                            queueEntry.sendAttempts++;

                            if (logger.isTraceEnabled()) {
                                logger.trace("-->>|{}|", buildEbusdSendString(queueEntry.buffer));
                            }
                            
                            writer.write(buildEbusdSendString(queueEntry.buffer) + "\n");
                            writer.flush();

                            // blocks the send thread until we received a valid response from ebusd
                            queue.setBlockSend(true);
                        }

                        // prepare next entry
                        queue.checkSendStatus(true);
                    }

                    Thread.sleep(100);

                } catch (EBusDataException | IOException e) {
                    logger.error(EBusConsts.LOG_ERR_DEF, e);

                } catch (InterruptedException e) {
                    // re-enable the interrupt to stop the while loop
                    Thread.currentThread().interrupt();
                }
            }

            // remove reference
            EBusEbusdController.this.senderThread = null;
        }
    }

    private ByteBuffer parseLine(String readLine) throws IOException, InterruptedException, EBusDataException {

        ByteBuffer b = null;

        try {

            if (logger.isTraceEnabled()) {
                logger.trace("<<--|{}|", readLine);
            }

            if (readLine == null) {
                logger.error("End of stream has been reached!");
                reconnect();

            } else if (StringUtils.startsWith(readLine, "ERR:")) {
                logger.error(readLine);
                reconnect();

            } else if (StringUtils.equals(readLine, "direct mode started")) {
                logger.info("ebusd direct mode enabled!");

                // start sender thread
                startSenderThread();

                directMode = true;

                setConnectionStatus(ConnectionStatus.CONNECTED);

            } else if (readLine.startsWith("version:")) {
                try {
                    String[] split = readLine.split(":");
                    String value = StringUtils.trim(split[1]);

                    logger.info("Use ebusd version: {}", value);

                    String version = StringUtils.trim(value.split(" ")[1]);
                    String[] versionParts = version.split("\\.");
                    if (NumberUtils.isDigits(versionParts[0]) && NumberUtils.isDigits(versionParts[1])) {
                        int versio = (NumberUtils.createInteger(versionParts[0]) * 1000)
                                + NumberUtils.createInteger(versionParts[1]);
                        if (versio < 3004) {
                            logger.warn("Your ebusd version is not supported. Please use a version >= 3.4 !");
                        }
                    }
                } catch (Exception e) {
                    // do not stop because of version check
                    logger.error(EBusConsts.LOG_ERR_DEF, e);
                }

            } else if (directMode) {

                if (readLine.startsWith("-s")) {

                    String tmp = readLine.substring(3, 5) + readLine.substring(6);
                    QueueEntry queueEntry = queue.getCurrent();

                    // maybe the command is direct from ebusd and the binding
                    if (queueEntry != null) {
                        currentSendId = queueEntry.id;

                        // remove this entry from queue
                        queue.resetSendQueue();
                    }

                    if (readLine.contains(":")) {
                        String[] split = tmp.split(":");

                        if (split[1].startsWith("done")) {
                            // Master Master or Broadcast
                            b = convertEBusdDataToFullTelegram(EBusUtils.toByteArray2(split[0]), null);

                        } else if (split[1].startsWith("ERR")) {
                            throw new EBusDataException(StringUtils.trim(split[2]));

                        } else {
                            // // Master Slave
                            b = convertEBusdDataToFullTelegram(EBusUtils.toByteArray2(split[0]),
                                    EBusUtils.toByteArray2(split[1]));
                        }

                    } else {
                        logger.debug("Unknown send response: {}", readLine);
                    }

                } else if (readLine.contains(" ")) {
                    // A master slave telegram
                    String[] split = readLine.split(" ");
                    b = convertEBusdDataToFullTelegram(EBusUtils.toByteArray2(split[0]),
                            EBusUtils.toByteArray2(split[1]));

                } else {
                    // A broadcast or master-master telegram
                    byte[] data = EBusUtils.toByteArray2(readLine);
                    b = convertEBusdDataToFullTelegram(data, null);
                }
            }

        } catch (NumberFormatException | NullPointerException e) {
            logger.error("Error while parsing ebusd response: {}", readLine);
            logger.error("Error Trace!", e);
        }

        return b;
    }

    private void stopSenderThread() {
        if (senderThread != null) {
            logger.warn("Stop sender thread!");
            senderThread.interrupt();
            senderThread = null;
        }
    }

    private void startSenderThread() {

        stopSenderThread();

        senderThread = new EBusWorkerThreadFactory("ebus-sender", false).newThread(new EBusSenderThread());
        senderThread.start();
    }

    @Override
    public void run() {

        try {
            logger.debug("Start ebusd controller thread!");

            initThreadPool();

            resetWatchdogTimer();

            connect();

            // loop until interrupt or reconnector count is -1 (to many retries)
            while (!(isInterrupted() || reConnectCounter == -1)) {

                try {
                    while (!isInterrupted()) {

                        if (socket == null || !socket.isConnected()) {
                            reconnect();
                        }

                        if (senderThread == null || !senderThread.isAlive()) {
                            // should not happened, but maybe it can help
                            startSenderThread();
                        }

                        String readLine = reader.readLine();

                        // reset send id before parsing
                        currentSendId = null;

                        ByteBuffer b = parseLine(readLine);

                        // reset with received data
                        resetWatchdogTimer();
                        reConnectCounter = 0;

                        if (b != null) {
                            this.fireOnEBusTelegramReceived(EBusUtils.toByteArray(b), currentSendId);
                        }

                    }

                } catch (EBusDataException e) {
                    this.fireOnEBusDataException(e, currentSendId);

                } catch (InterruptedIOException e) {
                    // disable the interrupt, can be a simple java.net.SocketTimeoutException: Read
                    // timed out

                } catch (InterruptedException e) {
                    // re-enable the interrupt to stop the while loop
                    Thread.currentThread().interrupt();

                } catch (IOException e) {
                    logger.error(EBusConsts.LOG_ERR_DEF, e);
                    fireOnConnectionException(e);

                    try {
                        reconnect();
                    } catch (IOException e1) {
                        logger.error(e.toString(), e1);
                    }

                }

            } // while loop
        } catch (Exception e) {
            logger.error(EBusConsts.LOG_ERR_DEF, e);
        }

        try {
            dispose();
        } catch (InterruptedException e) {
            logger.error(EBusConsts.LOG_ERR_DEF, e);
            Thread.currentThread().interrupt();
        }

    }

    public ByteBuffer convertEBusdDataToFullTelegram(byte[] masterData, byte[] slaveData) {

        ByteBuffer b = ByteBuffer.allocate(100);

        // add master len + data
        b.put(masterData);

        // master crc
        b.put(EBusUtils.crc8(masterData, masterData.length));

        if (masterData[1] != EBusConsts.BROADCAST_ADDRESS) {
            // slave ack
            b.put(EBusConsts.ACK_OK);
        }

        if (slaveData != null && slaveData.length > 0) {

            // add slave len + data
            b.put(slaveData);

            // slave crc
            b.put(EBusUtils.crc8(slaveData, slaveData.length));

            // master ack
            b.put(EBusConsts.ACK_OK);
        }

        // syn
        b.put(EBusConsts.SYN);

        return b;
    }

    private void reconnect() throws IOException, InterruptedException {

        if (!isRunning()) {
            logger.trace("Skip reconnect, thread was interrupted ...");
            return;
        }

        logger.info("Try to reconnect to ebusd daemon ...");

        // set connection status to connecting
        setConnectionStatus(ConnectionStatus.CONNECTING);

        if (reConnectCounter > 10) {
            reConnectCounter = -1;
            this.interrupt();

        } else {

            reConnectCounter++;

            logger.warn("Retry to connect to ebusd daemon in {} seconds ...", 5 * reConnectCounter);

            Thread.sleep(5000 * reConnectCounter);
            disconnect();

            if (connect()) {
                resetWatchdogTimer();
            }
        }

    }

    private void disconnect() {

        stopSenderThread();

        CommonsUtils.closeQuietly(reader);
        reader = null;

        CommonsUtils.closeQuietly(writer);
        writer = null;

        CommonsUtils.closeQuietly(socket);
        socket = null;

        directMode = false;

        // set connection status to disconnected
        setConnectionStatus(ConnectionStatus.DISCONNECTED);
    }

    private boolean connect() throws UnknownHostException, IOException {
        logger.info("Run connect ...");
        socket = new Socket(hostname, port);
        socket.setSoTimeout(20000);
        socket.setKeepAlive(true);

        if (socket.isConnected()) {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new OutputStreamWriter(socket.getOutputStream());

            try {

                writer.write("info\n");
                writer.flush();

                Thread.sleep(100);

                logger.info("Run direct mode ...");

                // switch to direct mode
                writer.write("direct\n");
                writer.flush();

                Thread.sleep(100);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return true;
        }

        return false;
    }

    @Override
    protected void fireWatchDogTimer() {
        logger.warn("eBUS Watchdog Timer!");
        disconnect();
    }

    @Override
    protected void dispose() throws InterruptedException {

        logger.debug("eBUS connection thread is shutting down ...");

        disconnect();

        super.dispose();
    }

    @Override
    public long getLastSendReceiveRoundtripTime() {
        return 0;
    }
}
