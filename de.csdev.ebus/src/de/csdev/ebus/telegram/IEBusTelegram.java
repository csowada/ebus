package de.csdev.ebus.telegram;

import java.nio.ByteBuffer;

public interface IEBusTelegram {

    /** The SYN byte */
    public final static byte SYN = (byte) 0xAA;

    /** The ACK OK answer byte */
    public final static byte ACK_OK = (byte) 0x00;

    /** The ACK FAIL answer byte */
    public final static byte ACK_FAIL = (byte) 0xFF;

    /** The Broadcast address */
    public final static byte BROADCAST_ADDRESS = (byte) 0xFE;

    /** Telegram type broadcast */
    public static final byte TYPE_BROADCAST = 1;

    /** Telegram type Master-Slave */
    public static final byte TYPE_MASTER_SLAVE = 2;

    /** Telegram type Master-Master */
    public static final byte TYPE_MASTER_MASTER = 3;

    /**
     * Get source id
     *
     * @return
     */
    public byte getSource();

    /**
     * Get destionation id
     *
     * @return
     */
    public byte getDestination();

    /**
     * Get command as short
     *
     * @return
     */
    public short getCommand();

    /**
     * Get the master data len
     *
     * @return
     */
    public int getDataLen();

    /**
     * Get master crc
     *
     * @return
     */
    public byte getCRC();

    /**
     * Get the telegram type
     *
     * @return
     * @see EBusTelegramConfiguration.BROADCAST
     * @see EBusTelegramConfiguration.MASTER_SLAVE
     * @see EBusTelegramConfiguration.MASTER_MASTER
     */
    public byte getType();

    /**
     * Get master data as read only ByteBuffer
     *
     * @return
     */
    public ByteBuffer getBuffer();

    /**
     * Get master data as byte array
     *
     * @return
     */
    public byte[] getData();

    /**
     * Get slave data len
     *
     * @return
     */
    public int getSlaveDataLen();

    /**
     * Get slave crc
     *
     * @return
     */
    public int getSlaveCRC();

    /**
     * Get slave data byte array
     *
     * @return
     */
    public byte[] getSlaveData();

}
