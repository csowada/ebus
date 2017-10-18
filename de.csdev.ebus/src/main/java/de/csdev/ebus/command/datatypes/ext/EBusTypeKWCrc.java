/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes.ext;

import de.csdev.ebus.command.datatypes.EBusAbstractType;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.IEBusComplexType;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeKWCrc extends EBusAbstractType<Byte> implements IEBusComplexType<Byte> {

    public static String TYPE_KW_CRC = "kw-crc";

    private static String[] supportedTypes = new String[] { TYPE_KW_CRC };

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
        return new byte[] { (byte) 0xCC };
    }

    @Override
    public String toString() {
        return "EBusTypeKWCrc []";
    }

}
