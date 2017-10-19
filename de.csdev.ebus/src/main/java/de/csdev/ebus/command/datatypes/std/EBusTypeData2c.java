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
import de.csdev.ebus.utils.EBusUtils;
import de.csdev.ebus.utils.NumberUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeData2c extends EBusTypeNumber {

    public static String TYPE_DATA2C = "data2c";

    private static String[] supportedTypes = new String[] { TYPE_DATA2C };

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLength() {
        return 2;
    }

    @Override
    public BigDecimal decodeInt(byte[] data) throws EBusTypeException {
        BigDecimal decodeInt = super.decodeInt(data);
        return decodeInt.divide(BigDecimal.valueOf(16));
    }

    @Override
    public byte[] encodeInt(Object data) throws EBusTypeException {
        BigDecimal b = NumberUtils.toBigDecimal(data);
        return super.encodeInt(b.multiply(BigDecimal.valueOf(16)));
    }

    @Override
    public String toString() {
        return "EBusTypeData2c [replaceValue=" + EBusUtils.toHexDumpString(getReplaceValue()).toString() + "]";
    }

}
