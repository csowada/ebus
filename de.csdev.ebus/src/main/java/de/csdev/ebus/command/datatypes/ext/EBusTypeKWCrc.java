/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes.ext;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeGeneric;
import de.csdev.ebus.command.datatypes.IEBusComplexType;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeKWCrc extends EBusTypeGeneric<Object> implements IEBusComplexType {

    public static String KW_CRC = "kw-crc";

    private static String[] supportedTypes = new String[] { KW_CRC };

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public Object decode(byte[] data) {
        throw new RuntimeException("Not implmented!");
    }

    @Override
    public byte[] encode(Object data) {
        throw new RuntimeException("Not implmented!");
    }

    @Override
    public <T> T decodeComplex(byte[] rawData, int pos) throws EBusTypeException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte[] encodeComplex(Object data) throws EBusTypeException {
        // TODO Auto-generated method stub
        return new byte[] { 0x11 };
    }

    @Override
    public String toString() {
        return "EBusTypeKWCrc []";
    }

}
