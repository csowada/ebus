/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes.std;

import java.math.BigDecimal;

import de.csdev.ebus.command.datatypes.EBusAbstractType;
import de.csdev.ebus.utils.EBusUtils;
import de.csdev.ebus.utils.NumberUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeBCD extends EBusAbstractType<BigDecimal> {

    public static String TYPE_BCD = "bcd";

    private static String[] supportedTypes = new String[] { TYPE_BCD };

    private int length = 1;

    public EBusTypeBCD() {
        replaceValue = new byte[] { (byte) 0xFF };
    }

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLength() {
        return length;
    }

    @Override
    public BigDecimal decodeInt(byte[] data) {

        BigDecimal result = BigDecimal.valueOf(0);

        for (int i = 0; i < data.length; i++) {
            Byte convertBcd2Dec = NumberUtils.convertBcd2Dec(data[i]);

            if (convertBcd2Dec == null) {
                return null;
            }

            result = result.multiply(BigDecimal.valueOf(100));
            result = result.add(BigDecimal.valueOf(convertBcd2Dec));
        }

        return result;
    }

    @Override
    public byte[] encodeInt(Object data) {

        final BigDecimal hundred = BigDecimal.valueOf(100);
        byte[] result = new byte[getTypeLength()];

        BigDecimal b = NumberUtils.toBigDecimal(data);

        for (int i = 0; i < result.length; i++) {

            BigDecimal[] divideAndRemainder = b.divideAndRemainder(hundred);

            // reassign the quotient
            b = divideAndRemainder[0];

            Byte bcd = NumberUtils.convertDec2Bcd(divideAndRemainder[1].byteValue());

            // put the result into the byte array, revert order
            result[result.length - (i + 1)] = bcd;
        }

        return result;
    }

    @Override
    public String toString() {
        return "EBusTypeBCD [replaceValue=" + EBusUtils.toHexDumpString(getReplaceValue()).toString() + ", length="
                + length + "]";
    }

}
