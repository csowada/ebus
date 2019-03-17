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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.utils.EBusUtils;

public class EBusEbusdControllerOLD extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(EBusLowLevelController.class);

    /** serial receive buffer */
    private EBusReceiveStateMachine machine = new EBusReceiveStateMachine();

    /** The tcp socket */
    private Socket socket;

    /** The tcp hostname */
    private String hostname;

    /** The tcp port */
    private int port;

    private BufferedReader reader;

    EBusCommandRegistry commandRegistry;

    /**
     * Constructor
     *
     * @param hostname
     * @param port
     */
    public EBusEbusdControllerOLD(String hostname, int port, EBusCommandRegistry commandRegistry) {
        this.hostname = hostname;
        this.port = port;
        this.commandRegistry = commandRegistry;
    }

    public void open() throws IOException {
        socket = new Socket(hostname, port);
        socket.setSoTimeout(20000);
        socket.setKeepAlive(true);

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

    }

    @Override
    public void run() {

        try {
            while (!isInterrupted()) {
                String readLine = reader.readLine();

                if (readLine.contains("[bus notice]")) {
                    // System.out.println(readLine);

                    if (readLine.contains("[bus notice] <")) {
                        String data = readLine.split("<")[1];
                        System.out.println(data);

                        if (data.length() > 5) {
                            byte[] byteArray2 = EBusUtils.toByteArray2(data);

                            List<IEBusCommandMethod> find = commandRegistry.find(byteArray2);
                            System.out.println(find);
                        }

                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            // } catch (EBusDataException e) {
            // e.printStackTrace();
        }
    }
}
