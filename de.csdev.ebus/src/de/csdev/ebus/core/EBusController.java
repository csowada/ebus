package de.csdev.ebus.core;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.core.EBusQueue.QueueEntry;
import de.csdev.ebus.core.connection.IEBusConnection;
import de.csdev.ebus.telegram.IEBusTelegram;
import de.csdev.ebus.utils.EBusUtils;

public class EBusController extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(EBusController.class);

    private IEBusConnection connection;

    /** serial receive buffer */
    private final ByteBuffer inputBuffer = ByteBuffer.allocate(100);

    /** the list for listeners */
    private final List<EBusConnectorEventListener> listeners = new ArrayList<EBusConnectorEventListener>();

    private EBusQueue queue = new EBusQueue();

    /** counts the re-connection tries */
    private int reConnectCounter = 0;

    /** The thread pool to execute events without blocking */
    private ExecutorService threadPool;

    public EBusController(IEBusConnection connection) {
        this.connection = connection;
    }

    /**
     * Add an eBus listener to receive valid eBus telegrams
     *
     * @param listener
     */
    public void addEBusEventListener(EBusConnectorEventListener listener) {
        listeners.add(listener);
    }

    /**
     * @param buffer
     * @return
     */
    public Integer addToSendQueue(byte[] buffer) {
        return queue.addToSendQueue(buffer);
    }

    /**
     * @return
     */
    public IEBusConnection getConnection() {
        return connection;
    }

    /**
     * Called event if a SYN packet has been received
     *
     * @throws IOException
     */
    private void onEBusSyncReceived() throws IOException {

        send(false);

        // afterwards check for next sending slot
        queue.checkSendStatus();

        if (inputBuffer.position() == 1 && inputBuffer.get(0) == IEBusTelegram.SYN) {
            logger.trace("Auto-SYN byte received");

        } else if (inputBuffer.position() == 2 && inputBuffer.get(0) == IEBusTelegram.SYN) {
            logger.warn("Collision on eBUS detected (SYN DATA SYNC Sequence) ...");

        } else if (inputBuffer.position() < 7) {
            logger.trace("Telegram too small, skip! Buffer: {}", EBusUtils.toHexDumpString(inputBuffer));

        } else {
            byte[] receivedRawData = Arrays.copyOf(inputBuffer.array(), inputBuffer.position());

            // execute event
            onEBusTelegramReceived(receivedRawData, null);
        }

        // reset receive buffer
        inputBuffer.clear();
    }

    /**
     * Called if a valid eBus telegram was received. Send to event
     * listeners via thread pool to prevent blocking.
     *
     * @param telegram
     */
    private void onEBusTelegramReceived(final byte[] receivedRawData, final Integer sendQueueId) {

        if (threadPool == null) {
            logger.warn("ThreadPool not ready!");
            return;
        }

        threadPool.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    byte[] receivedData = EBusUtils.decodeExpandedData(receivedRawData);
                    if (receivedData != null) {
                        for (EBusConnectorEventListener listener : listeners) {
                            listener.onTelegramReceived(receivedData, sendQueueId);
                        }
                    } else {
                        logger.debug("Received telegram was invalid, skip!");
                    }

                } catch (EBusDataException e) {
                    logger.error("error!", e);
                }

            }
        });
    }

    private void reconnect() throws IOException, InterruptedException {
        System.out.println("EBusController.reconnect()");
        this.interrupt();
    }

    /**
     * Remove an eBus listener
     *
     * @param listener
     * @return
     */
    public boolean removeEBusEventListener(EBusConnectorEventListener listener) {
        return listeners.remove(listener);
    }

    /**
     * Resend data if it's the first try or call resetSend()
     *
     * @param secondTry
     * @return
     * @throws IOException
     */
    private boolean resend(boolean secondTry) throws IOException {
        if (!secondTry) {
            send(true);
            return true;

        } else {
            logger.warn("Resend failed, remove data from sending queue ...");
            queue.resetSendQueue();
            return false;
        }
    }

    @Override
    public void run() {

        // create new thread pool to send received telegrams
        threadPool = Executors.newCachedThreadPool(new WorkerThreadFactory("ebus-send-receive"));

        int read = -1;

        byte[] buffer = new byte[100];

        // loop until interrupt or reconnector count is -1 (to many retries)
        while (!isInterrupted() || reConnectCounter == -1) {
            try {

                if (!connection.isOpen()) {
                    reconnect();

                } else {

                    // read byte from connector
                    read = connection.readBytes(buffer);

                    if (read == -1) {
                        logger.debug("eBUS read timeout occured, no data on bus ...");

                    } else {

                        for (int i = 0; i < read; i++) {
                            byte receivedByte = buffer[i];

                            // write received byte to input buffer
                            inputBuffer.put(receivedByte);

                            // the 0xAA byte is a end of a packet
                            if (receivedByte == IEBusTelegram.SYN) {

                                // check if the buffer is empty and ready for
                                // sending data
                                try {
                                    onEBusSyncReceived();
                                } catch (Exception e) {
                                    logger.error("Error while processing event sync received!", e);
                                }
                            }
                        }
                    }
                }

            } catch (IOException e) {
                logger.error("An IO exception has occured! Try to reconnect eBus connector ...", e);

                try {
                    reconnect();
                } catch (IOException e1) {
                    logger.error(e.toString(), e1);
                } catch (InterruptedException e1) {
                    logger.error(e.toString(), e1);
                }

            } catch (BufferOverflowException e) {
                logger.error(
                        "eBUS telegram buffer overflow - not enough sync bytes received! Try to adjust eBus adapter.");
                inputBuffer.clear();

            } catch (InterruptedException e) {
                logger.error(e.toString(), e);
                Thread.currentThread().interrupt();
                inputBuffer.clear();

            } catch (Exception e) {
                logger.error(e.toString(), e);
                inputBuffer.clear();
            }
        }

        // *******************************
        // ** end of thread **
        // *******************************

        // shutdown threadpool
        if (threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdownNow();
            try {
                // wait up to 10sec. for the thread pool
                threadPool.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
            }
        }

        // disconnect the connector e.g. close serial port
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (IOException e) {
            logger.error(e.toString(), e);
        }
    }

    /**
     * Internal send function. Send and read to detect byte collisions.
     *
     * @param secondTry
     * @throws IOException
     */
    private void send(boolean secondTry) throws IOException {

        QueueEntry sendEntry = queue.getCurrent();

        if (sendEntry == null) {
            return;
        }

        byte[] dataOutputBuffers = sendEntry.buffer;
        ByteBuffer sendBuffer = ByteBuffer.allocate(100);

        // count as send attempt
        sendEntry.sendAttempts++;

        int read = 0;
        byte readByte = 0;
        long readWriteDelay = 0;

        // clear input buffer to start by zero
        // resetInputBuffer();
        connection.reset();

        // send command
        for (int i = 0; i < dataOutputBuffers.length; i++) {
            byte b = dataOutputBuffers[i];

            connection.writeByte(b);

            if (i == 0) {

                readByte = (byte) (connection.readByte(true) & 0xFF);
                sendBuffer.put(readByte);

                if (b != readByte) {

                    // written and read byte not identical, that's
                    // a collision
                    if (readByte == IEBusTelegram.SYN) {
                        logger.debug("eBus collision with SYN detected!");
                    } else {
                        logger.debug("eBus collision detected! 0x{}", EBusUtils.toHexDumpString(readByte));
                    }

                    // last send try was a collision
                    if (queue.isLastSendCollisionDetected()) {
                        logger.warn("A second collision occured!");
                        queue.resetSendQueue();
                        return;
                    }
                    // priority class identical
                    else if ((byte) (readByte & 0x0F) == (byte) (b & 0x0F)) {
                        logger.trace("Priority class match, restart after next SYN ...");
                        queue.setLastSendCollisionDetected(true);

                    } else {
                        logger.trace("Priority class doesn't match, blocked for next SYN ...");
                        queue.setBlockNextSend(true);
                    }

                    // stop after a collision
                    return;
                }
            }

        }

        // sending master data finish

        // reset global variables
        queue.setLastSendCollisionDetected(false);
        queue.setBlockNextSend(false);

        // if this telegram a broadcast?
        if (dataOutputBuffers[1] == (byte) 0xFE) {

            logger.trace("Broadcast send ..............");

            // sende master sync
            connection.writeByte(IEBusTelegram.SYN);
            sendBuffer.put(IEBusTelegram.SYN);

        } else {

            readWriteDelay = System.nanoTime();

            // copy input data to result buffer
            sendBuffer.put(dataOutputBuffers, 1, dataOutputBuffers.length - 1);

            // skip next bytes
            connection.readBytes(new byte[dataOutputBuffers.length - 1]);
            // getInputStream().skip(dataOutputBuffers.length - 1);

            readWriteDelay = (System.nanoTime() - readWriteDelay) / 1000;

            logger.trace("readin delay " + readWriteDelay);

            read = connection.readByte(true);
            if (read != -1) {

                byte ack = (byte) (read & 0xFF);
                sendBuffer.put(ack);

                if (ack == IEBusTelegram.ACK_OK) {

                    boolean isMasterAddr = EBusUtils.isMasterAddress(dataOutputBuffers[1]);

                    // if the telegram is a slave telegram we will
                    // get data from slave
                    if (!isMasterAddr) {

                        // len of answer
                        byte nn2 = (byte) (connection.readByte(true) & 0xFF);
                        sendBuffer.put(nn2);

                        byte crc = EBusUtils.crc8_tab(nn2, (byte) 0);

                        if (nn2 > 16) {
                            logger.warn("slave data too long, invalid!");

                            // resend telegram (max. once)
                            if (!resend(secondTry)) {
                                return;
                            }
                        }

                        // read slave data, be aware of 0x0A bytes
                        while (nn2 > 0) {
                            byte d = (byte) (connection.readByte(true) & 0xFF);
                            sendBuffer.put(d);
                            crc = EBusUtils.crc8_tab(d, crc);

                            if (d != (byte) 0xA) {
                                nn2--;
                            }
                        }

                        // read slave crc
                        byte crc2 = (byte) (connection.readByte(true) & 0xFF);
                        sendBuffer.put(crc2);

                        // check slave crc
                        if (crc2 != crc) {
                            logger.warn("Slave CRC wrong, resend!");

                            // Resend telegram (max. once)
                            if (!resend(secondTry)) {
                                return;
                            }
                        }

                        // sende master sync
                        connection.writeByte(IEBusTelegram.ACK_OK);
                        sendBuffer.put(IEBusTelegram.ACK_OK);
                    } // isMasterAddr check

                    // send SYN byte
                    connection.writeByte(IEBusTelegram.SYN);
                    sendBuffer.put(IEBusTelegram.SYN);

                } else if (ack == IEBusTelegram.ACK_FAIL) {

                    // clear uncompleted telegram
                    sendBuffer.clear();

                    // resend telegram (max. once)
                    if (!resend(secondTry)) {
                        return;
                    }

                } else if (ack == IEBusTelegram.SYN) {
                    logger.debug("No answer from slave for telegram: {}", EBusUtils.toHexDumpString(sendBuffer));

                    // clear uncompleted telegram or it will result
                    // in uncomplete but valid telegram!
                    sendBuffer.clear();

                    // resend instead skip ...
                    // resetSend();
                    return;

                } else {
                    // Wow, wrong answer, and now?
                    logger.debug("Received wrong telegram: {}", EBusUtils.toHexDumpString(sendBuffer));

                    // clear uncompleted telegram
                    sendBuffer.clear();

                    // resend telegram (max. once)
                    if (!resend(secondTry)) {
                        return;
                    }
                }
            }
        }

        // after send process the received telegram
        if (sendBuffer.position() > 0) {
            byte[] buffer = Arrays.copyOf(sendBuffer.array(), sendBuffer.position());
            logger.debug("Succcesful send: {}", EBusUtils.toHexDumpString(buffer));
            onEBusTelegramReceived(buffer, sendEntry.id);
        }

        // reset send module
        queue.resetSendQueue();
    }
}
