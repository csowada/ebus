/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes.v2;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;

import de.csdev.ebus.command.datatypes.EBusAbtstractReplaceValueType;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.utils.EBusUtils;
import de.csdev.ebus.utils.NumberUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeUChar extends EBusAbtstractReplaceValueType<BigDecimal> {

    public static String UCHAR = "uchar";
    public static String BYTE = "byte";

    private static String[] supportedTypes = new String[] { BYTE, UCHAR };

    public EBusTypeUChar() {
        // set default length
        length = 1;
    }

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public byte[] getReplaceValue() {

        if (replaceValue == null || replaceValue.length == 0) {
            replaceValue = new byte[length];
            Arrays.fill(replaceValue, (byte) 0xFF);
        }

        return replaceValue;
    }

    @Override
    public BigDecimal decodeInt(byte[] data) throws EBusTypeException {

        byte[] clone = ArrayUtils.clone(data);
        ArrayUtils.reverse(clone);

        return new BigDecimal(new BigInteger(1, clone));
    }

    @Override
    public byte[] encodeInt(Object data) throws EBusTypeException {

        BigDecimal b = NumberUtils.toBigDecimal(data == null ? 0 : data);
        long l = b.longValue() & Long.MAX_VALUE;

        byte[] result = new byte[length];
        for (int i = 0; i <= length - 1; i++) {
            result[i] = (byte) (l & 0xFF);
            l >>= 8;
        }

        return result;
    }

    @Override
    public String toString() {
        return "EBusTypeByte [replaceValue=" + EBusUtils.toHexDumpString(replaceValue).toString() + "]";
    }

}
