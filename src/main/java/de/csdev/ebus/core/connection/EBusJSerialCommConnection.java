/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core.connection;

import java.io.IOException;

import com.fazecast.jSerialComm.SerialPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.core.EBusConsts;
import de.csdev.ebus.utils.CommonsUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusJSerialCommConnection extends AbstractEBusConnection {

    private static final Logger logger = LoggerFactory.getLogger(EBusJSerialCommConnection.class);

    /** The serial object */
    private SerialPort serialPort;

    /** The serial port name */
    private String port;

    public EBusJSerialCommConnection(String port) {
        this.port = port;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.connection.IEBusConnection#open()
     */
    @Override
    public boolean open() throws IOException {

        serialPort = SerialPort.getCommPort(port);
        serialPort.setComPortParameters(2400, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        serialPort.openPort(1000, 1, 1);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

        outputStream = serialPort.getOutputStream();
        inputStream = serialPort.getInputStream();

        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.connection.AbstractEBusConnection#close()
     */
    @Override
    public boolean close() throws IOException {
        if (serialPort == null) {
            return false;
        }

        // run the serial.close in a new not-interrupted thread to
        // prevent an IllegalMonitorStateException error
        Thread shutdownThread = new Thread(() -> {

            CommonsUtils.closeQuietly(inputStream);
            CommonsUtils.closeQuietly(outputStream);

            if (serialPort != null) {
                serialPort.closePort();
                serialPort = null;
            }
        }, "eBUS serial shutdown thread");

        shutdownThread.start();

        try {
            // wait for shutdown
            shutdownThread.join(2000);
        } catch (InterruptedException e) {
            logger.error(EBusConsts.LOG_ERR_DEF, e);
            Thread.currentThread().interrupt();
        }

        return true;
    }
}
