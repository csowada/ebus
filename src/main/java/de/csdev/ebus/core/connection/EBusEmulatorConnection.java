/**
 * Copyright (c) 2016-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core.connection;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.utils.Emulator;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusEmulatorConnection extends AbstractEBusConnection {

    private static final Logger logger = LoggerFactory.getLogger(EBusEmulatorConnection.class);

    private Emulator emu;

    private boolean autoSync = true;

    public EBusEmulatorConnection() {

    }

    public EBusEmulatorConnection(boolean autoSync) {
        this.autoSync = autoSync;
    }

    @Override
    public boolean open() throws IOException {
        emu = new Emulator(1, autoSync);
        this.inputStream = emu.getInputStream();
        return true;
    }

    @Override
    public void writeByte(int b) throws IOException {
        emu.write((byte) b);
    }

    public void writeBytes(byte[] byteArray) {
        emu.write(byteArray);
    }

    public void writeBytesDelayed(byte[] byteArray, long delay) {

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            logger.error("error!", e);
        }

        writeBytes(byteArray);
    }

    @Override
    public void reset() throws IOException {
        if (emu.getInputStream().available() > 0) {
            byte[] data = new byte[emu.getInputStream().available()];
            emu.getInputStream().read(data);
        }
    }

    @Override
    public boolean close() throws IOException {
        emu.close();
        return super.close();
    }

}
