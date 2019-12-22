/**
 * Copyright (c) 2016-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.utils;

import java.nio.ByteBuffer;

import org.apache.commons.lang.StringUtils;

import de.csdev.ebus.core.EBusConsts;

/**
 * A helper class to decode all eBus data types and telegrams.
 *
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusUtils {

    /** calculated crc values */
    final static public byte CRC_TAB_8_VALUE[] = { (byte) 0x00, (byte) 0x9B, (byte) 0xAD, (byte) 0x36, (byte) 0xC1,
            (byte) 0x5A, (byte) 0x6C, (byte) 0xF7, (byte) 0x19, (byte) 0x82, (byte) 0xB4, (byte) 0x2F, (byte) 0xD8,
            (byte) 0x43, (byte) 0x75, (byte) 0xEE, (byte) 0x32, (byte) 0xA9, (byte) 0x9F, (byte) 0x04, (byte) 0xF3,
            (byte) 0x68, (byte) 0x5E, (byte) 0xC5, (byte) 0x2B, (byte) 0xB0, (byte) 0x86, (byte) 0x1D, (byte) 0xEA,
            (byte) 0x71, (byte) 0x47, (byte) 0xDC, (byte) 0x64, (byte) 0xFF, (byte) 0xC9, (byte) 0x52, (byte) 0xA5,
            (byte) 0x3E, (byte) 0x08, (byte) 0x93, (byte) 0x7D, (byte) 0xE6, (byte) 0xD0, (byte) 0x4B, (byte) 0xBC,
            (byte) 0x27, (byte) 0x11, (byte) 0x8A, (byte) 0x56, (byte) 0xCD, (byte) 0xFB, (byte) 0x60, (byte) 0x97,
            (byte) 0x0C, (byte) 0x3A, (byte) 0xA1, (byte) 0x4F, (byte) 0xD4, (byte) 0xE2, (byte) 0x79, (byte) 0x8E,
            (byte) 0x15, (byte) 0x23, (byte) 0xB8, (byte) 0xC8, (byte) 0x53, (byte) 0x65, (byte) 0xFE, (byte) 0x09,
            (byte) 0x92, (byte) 0xA4, (byte) 0x3F, (byte) 0xD1, (byte) 0x4A, (byte) 0x7C, (byte) 0xE7, (byte) 0x10,
            (byte) 0x8B, (byte) 0xBD, (byte) 0x26, (byte) 0xFA, (byte) 0x61, (byte) 0x57, (byte) 0xCC, (byte) 0x3B,
            (byte) 0xA0, (byte) 0x96, (byte) 0x0D, (byte) 0xE3, (byte) 0x78, (byte) 0x4E, (byte) 0xD5, (byte) 0x22,
            (byte) 0xB9, (byte) 0x8F, (byte) 0x14, (byte) 0xAC, (byte) 0x37, (byte) 0x01, (byte) 0x9A, (byte) 0x6D,
            (byte) 0xF6, (byte) 0xC0, (byte) 0x5B, (byte) 0xB5, (byte) 0x2E, (byte) 0x18, (byte) 0x83, (byte) 0x74,
            (byte) 0xEF, (byte) 0xD9, (byte) 0x42, (byte) 0x9E, (byte) 0x05, (byte) 0x33, (byte) 0xA8, (byte) 0x5F,
            (byte) 0xC4, (byte) 0xF2, (byte) 0x69, (byte) 0x87, (byte) 0x1C, (byte) 0x2A, (byte) 0xB1, (byte) 0x46,
            (byte) 0xDD, (byte) 0xEB, (byte) 0x70, (byte) 0x0B, (byte) 0x90, (byte) 0xA6, (byte) 0x3D, (byte) 0xCA,
            (byte) 0x51, (byte) 0x67, (byte) 0xFC, (byte) 0x12, (byte) 0x89, (byte) 0xBF, (byte) 0x24, (byte) 0xD3,
            (byte) 0x48, (byte) 0x7E, (byte) 0xE5, (byte) 0x39, (byte) 0xA2, (byte) 0x94, (byte) 0x0F, (byte) 0xF8,
            (byte) 0x63, (byte) 0x55, (byte) 0xCE, (byte) 0x20, (byte) 0xBB, (byte) 0x8D, (byte) 0x16, (byte) 0xE1,
            (byte) 0x7A, (byte) 0x4C, (byte) 0xD7, (byte) 0x6F, (byte) 0xF4, (byte) 0xC2, (byte) 0x59, (byte) 0xAE,
            (byte) 0x35, (byte) 0x03, (byte) 0x98, (byte) 0x76, (byte) 0xED, (byte) 0xDB, (byte) 0x40, (byte) 0xB7,
            (byte) 0x2C, (byte) 0x1A, (byte) 0x81, (byte) 0x5D, (byte) 0xC6, (byte) 0xF0, (byte) 0x6B, (byte) 0x9C,
            (byte) 0x07, (byte) 0x31, (byte) 0xAA, (byte) 0x44, (byte) 0xDF, (byte) 0xE9, (byte) 0x72, (byte) 0x85,
            (byte) 0x1E, (byte) 0x28, (byte) 0xB3, (byte) 0xC3, (byte) 0x58, (byte) 0x6E, (byte) 0xF5, (byte) 0x02,
            (byte) 0x99, (byte) 0xAF, (byte) 0x34, (byte) 0xDA, (byte) 0x41, (byte) 0x77, (byte) 0xEC, (byte) 0x1B,
            (byte) 0x80, (byte) 0xB6, (byte) 0x2D, (byte) 0xF1, (byte) 0x6A, (byte) 0x5C, (byte) 0xC7, (byte) 0x30,
            (byte) 0xAB, (byte) 0x9D, (byte) 0x06, (byte) 0xE8, (byte) 0x73, (byte) 0x45, (byte) 0xDE, (byte) 0x29,
            (byte) 0xB2, (byte) 0x84, (byte) 0x1F, (byte) 0xA7, (byte) 0x3C, (byte) 0x0A, (byte) 0x91, (byte) 0x66,
            (byte) 0xFD, (byte) 0xCB, (byte) 0x50, (byte) 0xBE, (byte) 0x25, (byte) 0x13, (byte) 0x88, (byte) 0x7F,
            (byte) 0xE4, (byte) 0xD2, (byte) 0x49, (byte) 0x95, (byte) 0x0E, (byte) 0x38, (byte) 0xA3, (byte) 0x54,
            (byte) 0xCF, (byte) 0xF9, (byte) 0x62, (byte) 0x8C, (byte) 0x17, (byte) 0x21, (byte) 0xBA, (byte) 0x4D,
            (byte) 0xD6, (byte) 0xE0, (byte) 0x7B };

    /**
     * Left pad byte array to a byte array with given size.
     *
     * @param source Source array
     * @param size The final byte array size
     * @return The new byte arrray or <code>null</code> if the source array is to large
     */
    public static byte[] leftPadByteArray(byte[] source, int size) {
        byte[] bs = new byte[size];

        if (size < source.length) {
            return null;
        }

        int startPos = size - source.length;
        System.arraycopy(source, 0, bs, startPos, source.length);

        return bs;
    }

    /**
     * CRC calculation
     *
     * @param data The byte to crc check
     * @param crcInit The current crc result or another start value
     * @param poly The polynom
     * @return The crc result
     */
    public static byte crc8(byte data, byte crcInit, byte poly) {

        byte crc;
        byte polynom;
        int i;

        crc = crcInit;
        for (i = 0; i < 8; i++) {

            if ((uint(crc) & 0x80) != 0) {
                polynom = poly;
            } else {
                polynom = (byte) 0;
            }

            crc = (byte) ((uint(crc) & ~0x80) << 1);
            if ((uint(data) & 0x80) != 0) {
                crc = (byte) (uint(crc) | 1);
            }

            crc = (byte) (uint(crc) ^ uint(polynom));
            data = (byte) (uint(data) << 1);
        }
        return crc;
    }

    /**
     * @param data
     * @param len
     * @return
     */
    public static byte crc8(byte[] data, int len) {
        byte uc_crc = 0;
        for (int i = 0; i < len; i++) {
            byte b = data[i];
            uc_crc = crc8_tab(b, uc_crc);
        }
        return uc_crc;
    }

    /**
     * CRC calculation with tab operations
     *
     * @param data The byte to crc check
     * @param crcInit The current crc result or another start value
     * @return The crc result
     */
    public static byte crc8_tab(byte data, byte crcInit) {
        short ci = (short) (crcInit & 0xFF);
        byte crc = (byte) (CRC_TAB_8_VALUE[ci] ^ (data & 0xFF));
        return crc;
    }

    /**
     * Returns the master address based on a slave address if possible. Not all slave address have a valid master
     * address.
     *
     * @param slaveAddress
     * @return
     */
    static public Byte getMasterAddress(byte slaveAddress) {

        if (!isMasterAddress(slaveAddress)) {
            byte masterAddress = (byte) (slaveAddress == (byte) 0x04 ? (byte) 0xFF : slaveAddress - 5);
            if (isMasterAddress(masterAddress)) {
                return masterAddress;
            }
        }

        return null;
    }

    /**
     * Compute the slave address based on the master address.
     *
     * @param masterAddress The master address
     * @return The slave address or null if the master address is invalid
     */
    static public Byte getSlaveAddress(byte masterAddress) {
        if (isMasterAddress(masterAddress)) {
            return (byte) (masterAddress == (byte) 0xFF ? (byte) 0x04 : masterAddress + 5);
        }

        return null;
    }

    /**
     * Check if the address is a valid master address.
     *
     * @param address
     * @return
     */
    public static boolean isMasterAddress(byte address) {

        byte addr = (byte) ((byte) (address >>> 4) & (byte) 0x0F);
        byte prio = (byte) (address & (byte) 0x0F);

        if (isValidAddress(address)) {
            if (addr == (byte) 0x00 || addr == (byte) 0x01 || addr == (byte) 0x03 || addr == (byte) 0x07
                    || addr == (byte) 0x0F) {
                if (prio == (byte) 0x00 || prio == (byte) 0x01 || prio == (byte) 0x03 || prio == (byte) 0x07
                        || prio == (byte) 0x0F) {

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check if the address is in general valid
     *
     * @param address
     * @return
     */
    public static boolean isValidAddress(byte address) {
        if (address == EBusConsts.BROADCAST_ADDRESS || address == EBusConsts.SYN || address == EBusConsts.ESCAPE) {
            return false;
        }
        return true;
    }

    /**
     * FIXME: Badly programmed
     *
     * @param hexDumpString
     * @return
     */
    static public Byte toByte(String hexDumpString) {
        if (StringUtils.isEmpty(hexDumpString)) {
            return null;
        }
        return toByteArray(hexDumpString)[0];
    }

    /**
     * Convert a ByteBuffer to a byte array
     *
     * @param buffer
     * @return
     */
    public static byte[] toByteArray(ByteBuffer buffer) {

        int size = 0;
        if (buffer.position() == 0) {
            size = buffer.limit();
        } else {
            size = buffer.position();
        }

        byte[] data = new byte[size];
        ((ByteBuffer) buffer.duplicate().clear()).get(data);
        return data;
    }

    /**
     * Converts a hex string to byte array
     *
     * @param hexDumpString
     * @return
     */
    static public byte[] toByteArray(String hexDumpString) {
        if (StringUtils.isEmpty(hexDumpString)) {
            return new byte[0];
        }

        String[] elements = hexDumpString.split(" ");
        byte[] result = new byte[elements.length];

        int pos = 0;
        for (String val : elements) {
            result[pos++] = Integer.valueOf(val, 16).byteValue();
        }

        return result;
    }

    /**
     * Converts a hex string to byte array
     *
     * @param hexDumpString
     * @return
     */
    static public byte[] toByteArray2(String hexDumpString) {
        if (StringUtils.isEmpty(hexDumpString)) {
            return new byte[0];
        }

        // String hexDumpString = "A0B";

        if (hexDumpString.length() % 2 != 0) {
            hexDumpString = "0" + hexDumpString;
        }

        // String[] elements = hexDumpString.split(" ");
        byte[] result = new byte[hexDumpString.length() / 2];

        int pos = 0;
        for (int i = 0; i < hexDumpString.length(); i = i + 2) {
            String val = hexDumpString.substring(i, i + 2);
            result[pos++] = Integer.valueOf(val, 16).byteValue();
        }

        return result;
    }

    static public String mergeHexDumpStrings(String... args) {
        String merge = "";
        for (String string : args) {
            if (StringUtils.isNotEmpty(string)) {
                merge += string.length() % 2 == 0 ? string : "0" + string;
            }
        }

        return toHexDumpString(toByteArray2(merge)).toString();
    }

    /**
     * Generates a hex string representative of byte data
     *
     * @param data The source
     * @return The hex string
     */
    static public String toHexDumpString(Byte data) {

        if (data == null) {
            return "";
        }

        return String.format("%02X", (0xFF & data));
    }

    /**
     * Generates a string hex dump from a byte array
     *
     * @param data The source
     * @return The StringBuilder with hex dump
     */
    static public StringBuilder toHexDumpString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        if (data != null && data.length > 0) {
            for (int i = 0; i < data.length; i++) {
                byte c = data[i];
                if (i > 0) {
                    sb.append(' ');
                }
                sb.append(toHexDumpString(c));
            }
        }

        return sb;
    }

    /**
     * Generates a string hex dump from a ByteBuffer
     *
     * @param data The source
     * @return The StringBuilder with hex dump
     */
    static public StringBuilder toHexDumpString(ByteBuffer data) {

        int size = 0;
        if (data.position() == 0) {
            size = data.limit();
        } else {
            size = data.position();
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            byte c = data.get(i);
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(toHexDumpString(c));
        }
        return sb;
    }

    /**
     * Computes a hex string like 0x00
     *
     * @param data
     * @return
     */
    static public String toPrintHexDumpString(Byte data) {
        if (data != null) {
            return "0x" + String.format("%02X", (0xFF & data));
        }
        return null;
    }

    /**
     * Convert to unsigned int
     *
     * @param v
     * @return
     */
    public static int uint(byte v) {
        return v & 0xFF;
    }
}
