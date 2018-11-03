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
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusUDPConnection implements IEBusConnection {

    private DatagramChannel channel;

    /** The udp hostname */
    private String hostname;

    /** The udp port */
    private int port;

    // private SocketAddress server;

    /**
     * Constructor
     *
     * @param hostname
     * @param port
     */
    public EBusUDPConnection(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.connection.IEBusConnection#open()
     */
    @Override
    public boolean open() throws IOException {

        channel = DatagramChannel.open();
        channel.connect(new InetSocketAddress(this.hostname, this.port));

        return true;
    }

    @Override
    public boolean isOpen() throws IOException {
        return channel.isConnected();
    }

    @Override
    public boolean isReceiveBufferEmpty() throws IOException {
        return true;
    }

    @Override
    public void reset() throws IOException {
        // noop
    }

    @Override
    public void writeByte(int b) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put((byte) b);
        channel.write(buffer);
    }

    @Override
    public int readByte(boolean lowLatency) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1);
        channel.read(buffer);
        return buffer.get(0);
    }

    @Override
    public int readBytes(byte[] buffer) throws IOException {
        return channel.read(ByteBuffer.wrap(buffer));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.connection.AbstractEBusConnection#close()
     */
    @Override
    public boolean close() throws IOException {

        channel.disconnect().close();
        channel = null;

        return true;
    }

}
