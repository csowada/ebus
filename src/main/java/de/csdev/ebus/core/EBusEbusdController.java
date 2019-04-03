/**
 * Copyright (c) 2016-2018 by the respective copyright holders.
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
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.core.EBusQueue.QueueEntry;
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

    private EBusSenderThread senderThread = null;

    public EBusEbusdController(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public static String buildEbusdSendString(byte[] buffer) {
        final byte[] bs = Arrays.copyOfRange(buffer, 1, buffer.length - 1);

        final StringBuilder sb = new StringBuilder();
        sb.append(" ");
        sb.append("-s ");
        sb.append(EBusUtils.toHexDumpString(buffer[0]));
        sb.append(" ");
        sb.append(EBusUtils.toHexDumpString(bs).toString().replace(" ", ""));

        return sb.toString();
    }

    public class EBusSenderThread extends Thread {

        @Override
        public void run() {
            while (!isInterrupted()) {

                try {
                    EBusEbusdController.this.queue.checkSendStatus();

                    QueueEntry queueEntry = EBusEbusdController.this.queue.getCurrent();

                    if (queueEntry != null) {

                        writer.write(buildEbusdSendString(queueEntry.buffer) + "\n");
                        writer.flush();

                        // remove this entry from queue
                        EBusEbusdController.this.queue.resetSendQueue();
                    }

                } catch (EBusDataException | IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void run() {

        try {

            initThreadPool();
            resetWatchdogTimer();

            // loop until interrupt or reconnector count is -1 (to many retries)
            while (!(isInterrupted() || reConnectCounter == -1)) {

                try {
                    while (!isInterrupted()) {

                        if (socket == null || !socket.isConnected()) {
                            reconnect();
                        }

                        String readLine = reader.readLine();
                        logger.info(" -> " + readLine);

                        if (readLine == null) {
                            logger.error("End of stream has been reached!");
                            reconnect();

                        } else if (StringUtils.startsWith(readLine, "ERR:")) {
                            logger.error(readLine);
                            reconnect();

                        } else if (StringUtils.equals(readLine, "direct mode started")) {
                            logger.info("ebusd direct mode enabled!");
                            directMode = true;

                        } else if (!directMode) {
                            logger.info("Switch ebusd to direct mode ...");
                            // switch to direct mode
                            writer.write("direct\n");

                        } else {

                            ByteBuffer b = null;

                            if (readLine.startsWith("-s")) {
                                String tmp = readLine.substring(3, 5);
                                readLine = tmp + readLine.substring(6);

                                logger.warn(readLine);

                            }
                            if (readLine.startsWith(" ")) {
                                logger.warn("ignore: " + readLine);

                            } else if (readLine.contains(":")) {
                                // Send response

                                String[] split = readLine.split(":");

                                if (split[1].startsWith("done")) {
                                    // Master Master or Broadcast
                                    b = convertEBusdDataToFullTelegram(EBusUtils.toByteArray2(split[0]), null);

                                } else if (split[1].startsWith("ERR")) {
                                    // -s FF X8502203CC1A27:ERR: invalid numeric argument\n

                                } else {
                                    // // Master Slave
                                    b = convertEBusdDataToFullTelegram(EBusUtils.toByteArray2(split[0]),
                                            EBusUtils.toByteArray2(split[1]));
                                }

                            } else if (readLine.contains(" ")) {

                                String[] split = readLine.split(" ");

                                b = convertEBusdDataToFullTelegram(EBusUtils.toByteArray2(split[0]),
                                        EBusUtils.toByteArray2(split[1]));
                            } else {
                                // BC and MM
                                byte[] data = EBusUtils.toByteArray2(readLine);
                                b = convertEBusdDataToFullTelegram(data, null);
                            }

                            // reset with received data
                            resetWatchdogTimer();
                            reConnectCounter = 0;

                            if (b != null) {
                                this.fireOnEBusTelegramReceived(EBusUtils.toByteArray(b), null);
                            }

                        }
                    }

                } catch (IOException e) {
                    logger.error("error!", e);
                }

            } // while loop
        } catch (Exception e) {
            logger.error("error!", e);
        }

        dispose();
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

    @Override
    protected void initThreadPool() {
        super.initThreadPool();

        // create new thread pool to send received telegrams
        // threadPool = Executors.newCachedThreadPool(new EBusWorkerThreadFactory("ebus-sender", true));

        senderThread = new EBusSenderThread();
        senderThread.start();
    }

    private void reconnect() throws IOException {

        if (!isRunning()) {
            logger.trace("Skip reconnect, thread was interrupted ...");
            return;
        }

        logger.info("Try to reconnect to ebusd daemon ...");

        if (reConnectCounter > 10) {
            reConnectCounter = -1;
            this.interrupt();

        } else {

            reConnectCounter++;

            logger.warn("Retry to connect to ebusd daemon in {} seconds ...", 5 * reConnectCounter);
            try {
                if (socket != null) {
                    Thread.sleep(5000 * reConnectCounter);

                    disconnect();
                }

                if (connect()) {
                    resetWatchdogTimer();
                }

            } catch (InterruptedException e) {
                // noop, accept interruptions
            }
        }

    }

    private void disconnect() {
        IOUtils.closeQuietly(reader);
        reader = null;
        IOUtils.closeQuietly(socket);
        socket = null;
    }

    private boolean connect() throws UnknownHostException, IOException {
        socket = new Socket(hostname, port);
        socket.setSoTimeout(20000);
        socket.setKeepAlive(true);

        if (socket.isConnected()) {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new OutputStreamWriter(socket.getOutputStream());
            logger.info("Connected ?! ...");

            // switch to direct mode
            writer.write("direct\n");
            writer.flush();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // noop
            }

            return true;
        }

        return false;
    }

    @Override
    protected void fireWatchDogTimer() {
        logger.warn("eBUS Watchdog Timer!");
        disconnect();
        //
        // try {
        // this.connection.close();
        // } catch (IOException e) {
        // logger.error("error!", e);
        // }
    }

    @Override
    protected void dispose() {

        logger.debug("eBUS connection thread is shuting down ...");

        super.dispose();

        if (senderThread != null) {
            senderThread.interrupt();
        }

        disconnect();
    }

    @Override
    public long getLastSendReceiveRoundtripTime() {
        return 0;
    }
}
