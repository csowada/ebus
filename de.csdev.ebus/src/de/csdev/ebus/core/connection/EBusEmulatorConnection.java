package de.csdev.ebus.core.connection;

import java.io.File;
import java.io.IOException;

import de.csdev.ebus.utils.Emulator2;

public class EBusEmulatorConnection extends AbstractEBusConnection {

    private File file;
    private Emulator2 emu;

    public EBusEmulatorConnection(File file) {
        this.file = file;

        emu = new Emulator2();
    }

    @Override
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

}
