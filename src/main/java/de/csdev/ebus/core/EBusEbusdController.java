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
import java.net.Socket;

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

    public EBusEbusdController(String hostname, int port, int port2) {
        this.hostname = hostname;
        this.port = port;
        this.port2 = port2;
    }

    // private class EBusSender extends Thread {
    // @Override
    // public void run() {
    //
    // }
    // }

    @Override
    public void run() {

        resetWatchdogTimer();

        // loop until interrupt or reconnector count is -1 (to many retries)
        while (!(isInterrupted())) {

            try {
                while (!isInterrupted()) {

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
                                this.fireOnEBusTelegramReceived(receivedData, null);
                                //
                                //
                                // List<IEBusCommandMethod> find = commandRegistry.find(byteArray2);
                                // System.out.println(find);
                            }

                        }

                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        } // while loop

        dispose();
    }

    // @Override
    // protected void initThreadPool() {
    // super.initThreadPool();
    //
    // // create new thread pool to send received telegrams
    // threadPool = Executors.newCachedThreadPool(new EBusWorkerThreadFactory("ebus-sender", true));
    // }

    @Override
    protected void fireWatchDogTimer() {
        logger.warn("eBUS Watchdog Timer!");
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
    }

    @Override
    public long getLastSendReceiveRoundtripTime() {
        return 0;
    }
}
