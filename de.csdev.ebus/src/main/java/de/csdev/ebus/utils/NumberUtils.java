/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.utils;

import java.math.BigDecimal;

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
    public static BigDecimal toBigDecimal(Object obj) {

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

    public static Byte convertDec2Bcd(int data) {
    	
    	if(data > 99) {
    		return null;
    	}
    	
    	return (byte) (((data / 10) << 4) | data % 10);
    }
    
    public static Byte convertBcd2Dec(byte data) {
		byte high = (byte) (data >> 4 & 0x0F);
		byte low = (byte) (data & 0x0F);

		// nibbles out of rang 0-9
		if(high > 9 || low > 9) {
			return null;
		}

		return Byte.valueOf((byte) (high *10 + low));
		
//		return BigDecimal.valueOf((byte) ((data[0] >> 4 & 0x0F) * 10 + (data[0] & 0x0F)));
    }
    
}
