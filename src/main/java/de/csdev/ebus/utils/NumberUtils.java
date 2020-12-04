/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.utils;

import java.math.BigDecimal;

import org.eclipse.jdt.annotation.Nullable;

/**
 * A utility class for numbers.
 *
 * @author Christian Sowada - Initial contribution
 *
 */
public class NumberUtils {

    /**
     * Convert number object to BigDecimal
     *
     * @param obj Any kind of primitive datatype
     * @return A converted BigDecimal
     */
    public static @Nullable BigDecimal toBigDecimal(@Nullable Object obj) {

        if (obj instanceof Integer) {
            return BigDecimal.valueOf((Integer) obj);

        } else if (obj instanceof Long) {
            return BigDecimal.valueOf((Long) obj);

        } else if (obj instanceof Short) {
            return BigDecimal.valueOf((Short) obj);

        } else if (obj instanceof Byte) {
            return BigDecimal.valueOf((Byte) obj);

        } else if (obj instanceof Double) {
            return BigDecimal.valueOf((Double) obj);

        } else if (obj instanceof Float) {
            return BigDecimal.valueOf((Float) obj);

        } else if (obj instanceof BigDecimal) {
            return (BigDecimal) obj;
        }

        return null;
    }

    /**
     * @param data
     * @return
     */
    public static @Nullable Byte convertDec2Bcd(int data) {

        if (data > 99) {
            return null;
        }

        return (byte) (((data / 10) << 4) | data % 10);
    }

    /**
     * @param bcd
     * @return
     */
    public static @Nullable Byte convertBcd2Dec(byte bcd) {
        byte high = (byte) (bcd >> 4 & 0x0F);
        byte low = (byte) (bcd & 0x0F);

        // nibbles out of rang 0-9
        if (high > 9 || low > 9) {
            return null;
        }

        return (byte) (high * 10 + low);
    }

}
