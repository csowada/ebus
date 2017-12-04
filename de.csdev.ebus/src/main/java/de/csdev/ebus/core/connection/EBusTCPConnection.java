/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core.connection;

import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTCPConnection extends AbstractEBusConnection {

    private static final Logger logger = LoggerFactory.getLogger(EBusTCPConnection.class);

    /** The tcp socket */
    private Socket socket;

    /** The tcp hostname */
    private String hostname;

    /** The tcp port */
    private int port;

    /**
     * Constructor
     *
     * @param hostname
     * @param port
     */
    public EBusTCPConnection(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.csdev.ebus.core.connection.IEBusConnection#open()
     */
    @Override
    public boolean open() throws IOException {
        try {
            socket = new Socket(hostname, port);
            socket.setSoTimeout(20000);
            socket.setKeepAlive(true);

            socket.setTcpNoDelay(true);
            socket.setTrafficClass((byte) 0x10);

            // Useful? We try it
            // socket.setReceiveBufferSize(1);
            socket.setSendBufferSize(1);

            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

        } catch (Exception e) {
            logger.warn(e.getMessage());
            return false;
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.csdev.ebus.core.connection.AbstractEBusConnection#close()
     */
    @Override
    public boolean close() throws IOException {

        boolean close = super.close();
        if (close) {
            if (socket != null) {
                socket.close();
                socket = null;
            }

            return true;
        }

        return false;
    }

}
