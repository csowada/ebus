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

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

/**
 * @author Christian Sowada
 *
 */
public class EBusSerialJSSCConnection extends AbstractEBusConnection {

    private static final Logger logger = LoggerFactory.getLogger(EBusSerialJSSCConnection.class);

    /** The serial object */
    private SerialPort serialPort;

    /** The serial port name */
    private String port;

    public EBusSerialJSSCConnection(String port) {
        logger.info("Use JSSC EBus connector !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        this.port = port;
    }

    @Override
    public boolean open() throws IOException {
        serialPort = new SerialPort(port);
        try {
            serialPort.openPort();
            serialPort.setParams(2400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

        } catch (SerialPortException e) {
            logger.error("errro!", e);
            return false;
        }

        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.ebus.connection.AbstractEBusConnector#disconnect()
     */
    @Override
    public boolean close() throws IOException {

        if (serialPort == null) {
            return true;
        }

        // run the serial.close in a new not-interrupted thread to
        // prevent an IllegalMonitorStateException error
        Thread shutdownThread = new Thread(new Runnable() {
            @Override
            public void run() {

                IOUtils.closeQuietly(inputStream);
                IOUtils.closeQuietly(outputStream);

                if (serialPort != null) {
                    try {
                        if (serialPort.isOpened()) {
                            serialPort.closePort();
                        }
                    } catch (SerialPortException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    serialPort = null;
                }
            }
        }, "eBUS serial shutdown thread");

        shutdownThread.start();

        try {
            // wait for shutdown
            shutdownThread.join(2000);
        } catch (InterruptedException e) {
        }

        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openhab.binding.ebus.internal.connection.AbstractEBusWriteConnector#writeByte(int)
     */
    @Override
    public void writeByte(int b) throws IOException {
        try {
            serialPort.writeInt(b);
        } catch (SerialPortException e) {
            throw new IOException(e);
        }
        // no flush, sometimes it blocks infinitely
    }

    @Override
    public boolean isOpen() {
        return serialPort != null ? serialPort.isOpened() : false;
    }

    @Override
    public int readByte(boolean lowLatency) throws IOException {
        try {
            int[] data = serialPort.readIntArray(1, 3000);
            return data[0];

        } catch (SerialPortException e) {
            throw new IOException(e);
        } catch (SerialPortTimeoutException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void reset() throws IOException {
        try {
            serialPort.purgePort(SerialPort.PURGE_TXCLEAR);
            super.reset();

        } catch (SerialPortException e) {
            throw new IOException(e);
        }
    }
}
