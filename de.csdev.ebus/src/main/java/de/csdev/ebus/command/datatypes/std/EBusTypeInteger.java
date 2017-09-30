/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes.std;

import java.math.BigDecimal;

import de.csdev.ebus.command.datatypes.EBusTypeGenericReplaceValue;
import de.csdev.ebus.utils.EBusUtils;
import de.csdev.ebus.utils.NumberUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeInteger extends EBusTypeGenericReplaceValue {

    public static String INTEGER = "int";

    private static String[] supportedTypes = new String[] { INTEGER };

    public EBusTypeInteger() {
        replaceValue = new byte[] { (byte) 0x00, (byte) 0x80 };
        applyByteOrder(replaceValue);
    }

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLenght() {
        return 2;
    }

    @Override
    public BigDecimal decodeInt(byte[] data) {
        applyByteOrder(data);
        return BigDecimal.valueOf((short) (data[1] << 8 | data[0] & 0xFF));
    }

    @Override
    public byte[] encodeInt(Object data) {
        BigDecimal b = NumberUtils.toBigDecimal(data);

        byte[] result = new byte[] { (byte) b.intValue(), (byte) (b.intValue() >> 8) };
        applyByteOrder(result);

        return result;
    }

    @Override
    public String toString() {
        return "EBusTypeInteger [replaceValue=" + EBusUtils.toHexDumpString(replaceValue).toString() + "]";
    }

}
