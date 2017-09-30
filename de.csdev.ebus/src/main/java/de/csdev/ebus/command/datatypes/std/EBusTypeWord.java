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
public class EBusTypeWord extends EBusTypeGenericReplaceValue {

    public static String WORD = "word";
    public static String UINT = "uint";

    private static String[] supportedTypes = new String[] { WORD, UINT };

    public EBusTypeWord() {
        replaceValue = new byte[] { (byte) 0xFF, (byte) 0xFF };
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

        BigDecimal value = types.decode(EBusTypeInteger.INTEGER, data);
        if (value == null) {
            return null;
        }
        return BigDecimal.valueOf((short) (value.intValue() & 0xffff));
    }

    @Override
    public byte[] encodeInt(Object data) throws EBusTypeException {
        BigDecimal b = NumberUtils.toBigDecimal(data);
        byte[] result = types.encode(EBusTypeInteger.INTEGER, b.intValue() & 0xFFFF);
        applyByteOrder(result);
        return result;
    }

    @Override
    public String toString() {
        return "EBusTypeWord [replaceValue=" + EBusUtils.toHexDumpString(replaceValue).toString() + "]";
    }

}
