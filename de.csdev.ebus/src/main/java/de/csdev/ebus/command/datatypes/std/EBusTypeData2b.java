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

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeGenericReplaceValue;
import de.csdev.ebus.utils.EBusUtils;
import de.csdev.ebus.utils.NumberUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeData2b extends EBusTypeGenericReplaceValue {

    public static String DATA2B = "data2b";

    private static String[] supportedTypes = new String[] { DATA2B };

    public EBusTypeData2b() {
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
    public BigDecimal decodeInt(byte[] data) throws EBusTypeException {

        applyByteOrder(data);

        BigDecimal intValue = types.decode(EBusTypeInteger.INTEGER, data);
        if (intValue == null) {
            return null;
        }
        return intValue.divide(BigDecimal.valueOf(256));
    }

    @Override
    public byte[] encodeInt(Object data) throws EBusTypeException {
        BigDecimal b = NumberUtils.toBigDecimal(data);
        b = b.multiply(BigDecimal.valueOf(256));

        byte[] result = types.encode(EBusTypeInteger.INTEGER, b);
        applyByteOrder(result);

        return result;
    }

    @Override
    public String toString() {
        return "EBusTypeData2b [replaceValue=" + EBusUtils.toHexDumpString(replaceValue).toString() + "]";
    }

}
