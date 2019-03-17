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
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private int port2;

    private BufferedReader reader;

    /** counts the re-connection tries */
    private int reConnectCounter = 0;

    public EBusEbusdController(String hostname, int port, int port2) {
        this.hostname = hostname;
        this.port = port;
        this.port2 = port2;
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

                        if (!socket.isConnected()) {
                            reconnect();
                        }

                        // queue.checkSendStatus();
                        //
                        // if(queue.getCurrent() != null) {
                        // queue.getCurrent().buffer
                        // }

                        String readLine = reader.readLine();

                        if (readLine.contains("[bus notice]")) {
                            // System.out.println(readLine);

                            if (readLine.contains("[bus notice] <")) {
                                String data = readLine.split("<")[1];
                                System.out.println(data);

                                if (data.length() > 5) {
                                    byte[] receivedData = EBusUtils.toByteArray2(data);

                                    // reset with received data
                                    resetWatchdogTimer();
                                    reConnectCounter = 0;

                                    this.fireOnEBusTelegramReceived(receivedData, null);
                                }

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

    @Override
    protected void initThreadPool() {
        super.initThreadPool();

        // create new thread pool to send received telegrams
        // threadPool = Executors.newCachedThreadPool(new EBusWorkerThreadFactory("ebus-sender", true));
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
                Thread.sleep(5000 * reConnectCounter);

                disconnect();

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
        socket = new Socket(hostname, port2);
        socket.setSoTimeout(20000);
        socket.setKeepAlive(true);

        if (socket.isConnected()) {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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

        disconnect();
    }

    @Override
    public long getLastSendReceiveRoundtripTime() {
        return 0;
    }
}
