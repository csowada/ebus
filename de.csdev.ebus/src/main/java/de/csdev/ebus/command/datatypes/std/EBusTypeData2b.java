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
public class EBusTypeData2b extends EBusTypeNumber {

    public static String DATA2B = "data2";

    private static String[] supportedTypes = new String[] { DATA2B };

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
    	BigDecimal decodeInt = super.decodeInt(data);
        return decodeInt.divide(BigDecimal.valueOf(256));
    }

    @Override
    public byte[] encodeInt(Object data) throws EBusTypeException {
        BigDecimal b = NumberUtils.toBigDecimal(data);
        return super.encodeInt(b.multiply(BigDecimal.valueOf(256)));
    }

    @Override
    public String toString() {
        return "EBusTypeData2b [replaceValue=" + EBusUtils.toHexDumpString(replaceValue).toString() + "]";
    }

}
