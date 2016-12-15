package de.csdev.ebus.core.connection;

import java.io.IOException;

public interface IEBusConnection {

    public boolean open() throws IOException;

    public boolean close() throws IOException;

    public boolean isOpen() throws IOException;

    public int readByte(boolean lowLatency) throws IOException;

    public boolean isReceiveBufferEmpty() throws IOException;

    public int readBytes(byte[] buffer) throws IOException;

    public void writeByte(int b) throws IOException;

    public void reset() throws IOException;
}
