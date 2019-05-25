/**
 * Copyright (c) 2016-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes.ext;

import de.csdev.ebus.command.datatypes.EBusAbstractType;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeString extends EBusAbstractType<String> {

    public static String TYPE_STRING = "string";

    private static String[] supportedTypes = new String[] { TYPE_STRING };

    private Integer length = 1;

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public String decodeInt(byte[] data) {
        return new String(data);
    }

    @Override
    public byte[] encodeInt(Object data) {

        // return a empty string with defined len
        if (data == null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append(' ');
            }
            return sb.toString().getBytes();
        }

        byte[] b = new byte[length];
        System.arraycopy(data.toString().getBytes(), 0, b, 0, b.length);

        return b;
    }

    @Override
    public int getTypeLength() {
        return length;
    }

    @Override
    public String toString() {
        return "EBusTypeString [length=" + length + "]";
    }
}
