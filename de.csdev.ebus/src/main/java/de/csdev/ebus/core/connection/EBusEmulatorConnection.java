/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core.connection;

import java.io.File;
import java.io.IOException;

import de.csdev.ebus.utils.Emulator;

/**
 * @author Christian Sowada
 *
 */
public class EBusEmulatorConnection extends AbstractEBusConnection {

    private File file;
    private Emulator emu;

    public EBusEmulatorConnection(File file) {
        this.file = file;

        emu = new Emulator();
    }

    public boolean open() throws IOException {
        this.inputStream = emu.getInputStream();

        emu.write(new byte[] { 1, 2, 3, 45, (byte) 0xAA });
        emu.play(file);
        emu.write(new byte[] { 1, 2, 3, 45, (byte) 0xAA });
        emu.play(file);
        emu.write(new byte[] { 2, 1, 1, 1, (byte) 200, 100, 45, (byte) 0xAA });
        emu.play(file);
        return true;
    }

    @Override
    public void writeByte(int b) throws IOException {
        byte[] byteArray = { (byte) b };
        emu.write(byteArray);
    }

    @Override
    public void reset() throws IOException {
        if (emu.getInputStream().available() > 0) {
            byte[] x = new byte[emu.getInputStream().available()];
            emu.getInputStream().read(x);
        }
    }
}
