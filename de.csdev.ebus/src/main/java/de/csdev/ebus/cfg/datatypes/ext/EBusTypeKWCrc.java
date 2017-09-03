/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg.datatypes.ext;

import de.csdev.ebus.cfg.datatypes.EBusTypeException;
import de.csdev.ebus.cfg.datatypes.EBusTypeGeneric;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeKWCrc extends EBusTypeGeneric<Object> implements IEBusComplexType {

    public static String KW_CRC = "kw-crc";

    private static String[] supportedTypes = new String[] { KW_CRC };

    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    public Object decode(byte[] data) {
        throw new RuntimeException("Not implmented!");
    }

    public byte[] encode(Object data) {
        throw new RuntimeException("Not implmented!");
    }

    public <T> T decodeComplex(byte[] rawData, int pos) throws EBusTypeException {
        // TODO Auto-generated method stub
        return null;
    }

    public byte[] encodeComplex(Object data) throws EBusTypeException {
        // TODO Auto-generated method stub
        return new byte[] { 0x11 };
    }

}
