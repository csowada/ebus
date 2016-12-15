package de.csdev.ebus.core.connection;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EBusTCPConnection extends AbstractEBusConnection {

    private static final Logger logger = LoggerFactory.getLogger(EBusTCPConnection.class);

    /** The tcp socket */
    private Socket socket;

    /** The tcp hostname */
    private String hostname;

    /** The tcp port */
    private int port;

    /**
     * Constructor
     *
     * @param hostname
     * @param port
     */
    public EBusTCPConnection(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    @Override
    public boolean open() throws IOException {
        try {
            socket = new Socket(hostname, port);
            socket.setSoTimeout(20000);
            socket.setKeepAlive(true);

            socket.setTcpNoDelay(true);
            socket.setTrafficClass((byte) 0x10);

            // Useful? We try it
            // socket.setReceiveBufferSize(1);
            socket.setSendBufferSize(1);

            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

        } catch (ConnectException e) {
            logger.error(e.toString());

        } catch (Exception e) {
            logger.error(e.toString(), e);

        }

        return true;
    }

    @Override
    public boolean close() throws IOException {

        boolean close = super.close();
        if (close) {
            if (socket != null) {
                socket.close();
                socket = null;
            }

            return true;
        }

        return false;
    }

}
