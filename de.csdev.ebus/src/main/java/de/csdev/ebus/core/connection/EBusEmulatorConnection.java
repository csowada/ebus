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
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.utils.Emulator;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusEmulatorConnection extends AbstractEBusConnection {

    private static final Logger logger = LoggerFactory.getLogger(EBusEmulatorConnection.class);

    private URL readerURL;

    private Emulator emu;

    public EBusEmulatorConnection(URL readerURL) {
        this.readerURL = readerURL;

        emu = new Emulator();
    }

    public boolean open() throws IOException {
        this.inputStream = emu.getInputStream();

        // emu.write(new byte[] { 1, 2, 3, 45, (byte) 0xAA });
        if (readerURL != null) {
            emu.play(readerURL.openStream());
        }
        // emu.write(new byte[] { 1, 2, 3, 45, (byte) 0xAA });
        // emu.play(readerURL.openStream());
        // emu.write(new byte[] { 2, 1, 1, 1, (byte) 200, 100, 45, (byte) 0xAA });
        // emu.play(readerURL.openStream());
        return true;
    }

    @Override
    public void writeByte(int b) throws IOException {
        byte[] byteArray = { (byte) b };
        emu.write(byteArray);
    }

    public void writeBytes(byte[] byteArray) {
        for (byte b : byteArray) {
            try {
                writeByte(b);
            } catch (IOException e) {
                logger.error("error!", e);
            }
        }
    }

    public void writeBytesDelayed(byte[] byteArray, long delay) {

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            logger.error("error!", e);
        }

        for (byte b : byteArray) {
            try {
                writeByte(b);
            } catch (IOException e) {
                logger.error("error!", e);
            }
        }
    }

    @Override
    public void reset() throws IOException {
        if (emu.getInputStream().available() > 0) {
            byte[] x = new byte[emu.getInputStream().available()];
            emu.getInputStream().read(x);
        }
    }
}
