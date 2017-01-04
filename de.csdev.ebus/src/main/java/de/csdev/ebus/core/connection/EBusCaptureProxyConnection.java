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

    @Override
    public boolean open() throws IOException {
        return proxyConnection.open();
    }

    @Override
    public boolean close() throws IOException {
        captureWriter.close();
        return proxyConnection.close();
    }

    @Override
    public boolean isOpen() throws IOException {
        return proxyConnection.isOpen();
    }

    @Override
    public int readByte(boolean lowLatency) throws IOException {
        int readByte = proxyConnection.readByte(lowLatency);

        if (readByte != -1) {
            captureWriter.write(new byte[] { (byte) (readByte & 0xFF) });
        }

        return readByte;
    }

    @Override
    public boolean isReceiveBufferEmpty() throws IOException {
        return proxyConnection.isReceiveBufferEmpty();
    }

    @Override
    public int readBytes(byte[] buffer) throws IOException {
        int readBytes = proxyConnection.readBytes(buffer);
        captureWriter.write(buffer, readBytes);
        return readBytes;
    }

    @Override
    public void writeByte(int b) throws IOException {
        proxyConnection.writeByte(b);
    }

    @Override
    public void reset() throws IOException {
        proxyConnection.reset();
    }

}
