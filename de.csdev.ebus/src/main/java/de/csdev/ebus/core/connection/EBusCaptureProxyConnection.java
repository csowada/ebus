package de.csdev.ebus.core.connection;

import java.io.IOException;

import de.csdev.ebus.utils.EmulatorCapture;

public class EBusCaptureProxyConnection implements IEBusConnection {

    private IEBusConnection proxyConnection;

    private EmulatorCapture captureWriter;

    public EBusCaptureProxyConnection(IEBusConnection proxyConnection, EmulatorCapture captureWriter) {
        this.proxyConnection = proxyConnection;
        this.captureWriter = captureWriter;
    }

    public boolean open() throws IOException {
        return proxyConnection.open();
    }

    public boolean close() throws IOException {
        captureWriter.close();
        return proxyConnection.close();
    }

    public boolean isOpen() throws IOException {
        return proxyConnection.isOpen();
    }

    public int readByte(boolean lowLatency) throws IOException {
        int readByte = proxyConnection.readByte(lowLatency);

        if (readByte != -1) {
            captureWriter.write(new byte[] { (byte) (readByte & 0xFF) });
        }

        return readByte;
    }

    public boolean isReceiveBufferEmpty() throws IOException {
        return proxyConnection.isReceiveBufferEmpty();
    }

    public int readBytes(byte[] buffer) throws IOException {
        int readBytes = proxyConnection.readBytes(buffer);
        captureWriter.write(buffer, readBytes);
        return readBytes;
    }

    public void writeByte(int b) throws IOException {
        proxyConnection.writeByte(b);
    }

    public void reset() throws IOException {
        proxyConnection.reset();
    }

}
