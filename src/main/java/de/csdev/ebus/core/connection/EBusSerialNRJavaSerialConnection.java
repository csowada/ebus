/**
 * Copyright (c) 2017-2021 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core.connection;

import java.io.IOException;
import java.util.TooManyListenersException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.core.EBusConsts;
import de.csdev.ebus.utils.CommonsUtils;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.UnsupportedCommOperationException;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusSerialNRJavaSerialConnection extends AbstractEBusConnection {

    private static final Logger logger = LoggerFactory.getLogger(EBusSerialNRJavaSerialConnection.class);

    /** The serial object */
    private SerialPort serialPort;

    /** The serial port name */
    private String port;

    public EBusSerialNRJavaSerialConnection(String port) {
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

            final CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(port);

            if (portIdentifier != null) {

                serialPort = portIdentifier.open("de.csdev.ebus", 2000);
                serialPort.setSerialPortParams(2400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);

                serialPort.enableReceiveThreshold(1);
                serialPort.disableReceiveTimeout();

                // set buffers to 1 for low latency
                serialPort.setOutputBufferSize(1);
                serialPort.setInputBufferSize(50);

                outputStream = serialPort.getOutputStream();
                inputStream = serialPort.getInputStream();

                outputStream.flush();
                if (inputStream.markSupported()) {
                    inputStream.reset();
                }

                // use event to let readByte wait until data is available, optimize cpu usage
                serialPort.addEventListener(event -> {
                    if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                        synchronized (inputStream) {
                            inputStream.notifyAll();
                        }
                    }
                });

                serialPort.notifyOnDataAvailable(true);

                return true;
            }

        } catch (NoSuchPortException e) {
            logger.error("Unable to connect to serial port {}", port);

        } catch (PortInUseException e) {
            logger.error("Serial port {} is already in use", port);

        } catch (UnsupportedCommOperationException e) {
            logger.error(e.toString(), e);

        } catch (TooManyListenersException e) {
            logger.error("Too many listeners error!", e);
        }

        serialPort = null;
        return false;
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

            if (outputStream != null) {
                try {
                    outputStream.flush();
                } catch (IOException e) {
                    // noop
                }
                CommonsUtils.closeQuietly(outputStream);
            }

            if (serialPort != null) {

                serialPort.notifyOnDataAvailable(false);
                serialPort.removeEventListener();

                serialPort.close();
                serialPort = null;
            }

            inputStream = null;
            outputStream = null;
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

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.connection.AbstractEBusConnection#readByte(boolean)
     */
    @Override
    public int readByte(boolean lowLatency) throws IOException {
        if (lowLatency) {
            return inputStream.read();
        } else {
            if (inputStream.available() > 0) {
                return inputStream.read();

            } else {
                synchronized (inputStream) {
                    try {
                        inputStream.wait(3000);
                        return inputStream.read();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return -1;
                }
            }
        }
    }
}
