/**
 * Copyright (c) 2016-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes.ext;

import java.nio.ByteBuffer;

import de.csdev.ebus.command.datatypes.EBusAbstractType;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.IEBusComplexType;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeKWCrc extends EBusAbstractType<Byte> implements IEBusComplexType<Byte> {

    public static String TYPE_KW_CRC = "kw-crc";

    public static String POS = "pos";

    public static int pos = 0;

    private static String[] supportedTypes = new String[] { TYPE_KW_CRC };

    public EBusTypeKWCrc() {
        replaceValue = new byte[] { (byte) 0xCC };
    }

    @Override
    public int getTypeLength() {
        return 1;
    }

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public Byte decodeInt(byte[] data) {
        throw new RuntimeException("Not implemented by EBusTypeKWCrc!");
    }

    @Override
    public byte[] encodeInt(Object data) {
        throw new RuntimeException("Not implemented by EBusTypeKWCrc!");
    }

    @Override
    public Byte decodeComplex(byte[] rawData, int pos) throws EBusTypeException {
        return (byte) 0xCC;
    }

    @Override
    public byte[] encodeComplex(Object data) throws EBusTypeException {

        byte[] bytesData = null;

        if (data instanceof ByteBuffer) {
            bytesData = EBusUtils.toByteArray((ByteBuffer) data);
        } else if (data instanceof byte[]) {
            bytesData = (byte[]) data;
        }

        if (bytesData != null) {
            byte b = 0;

            for (int i = pos + 1; i < bytesData.length; i++) {
                // exclude crc pos
                if (i != pos) {
                    b = EBusUtils.crc8(bytesData[i], b, (byte) 0x5C);
                }
            }

            return new byte[] { b };
        }

        return new byte[] { (byte) 0xCC };
    }

    @Override
    public String toString() {
        return "EBusTypeKWCrc []";
    }

}
