/**
 * Copyright (c) 2017-2021 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes.std;

import java.math.BigDecimal;

import org.eclipse.jdt.annotation.Nullable;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.utils.EBusUtils;
import de.csdev.ebus.utils.NumberUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeData2b extends AbstractEBusTypeNumber {

    public static String TYPE_DATA2B = "data2b";

    private static String[] supportedTypes = new String[] { TYPE_DATA2B };

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLength() {
        return 2;
    }

    @Override
    public BigDecimal decodeInt(byte @Nullable [] data) throws EBusTypeException {
        BigDecimal decodeInt = super.decodeInt(data);
        return decodeInt.divide(BigDecimal.valueOf(256));
    }

    @Override
    public byte[] encodeInt(@Nullable Object data) throws EBusTypeException {
        BigDecimal b = NumberUtils.toBigDecimal(data);

        if (b == null) {
            throw new EBusTypeException("Unable to convert input data to number!");
        }

        return super.encodeInt(b.multiply(BigDecimal.valueOf(256)));
    }

    @Override
    public String toString() {
        return "EBusTypeData2b [replaceValue=" + EBusUtils.toHexDumpString(getReplaceValue()).toString() + "]";
    }

}
