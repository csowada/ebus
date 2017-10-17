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
import java.math.BigInteger;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;

import de.csdev.ebus.command.datatypes.EBusAbstractReplaceValueType;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.utils.EBusUtils;
import de.csdev.ebus.utils.NumberUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public abstract class EBusTypeUnsignedNumber extends EBusAbstractReplaceValueType<BigDecimal> {

    @Override
    public byte[] getReplaceValue() {

    	int length = getTypeLenght();
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

        int length = getTypeLenght();
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
