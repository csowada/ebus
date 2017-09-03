/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg.datatypes;

import java.math.BigDecimal;

import de.csdev.ebus.utils.NumberUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeByte extends EBusTypeGenericReplaceValue {

    public static String UCHAR = "uchar";
    public static String BYTE = "byte";

    private static String[] supportedTypes = new String[] { BYTE, UCHAR };

    public EBusTypeByte() {
        replaceValue = new byte[] { (byte) 0xFF };
    }

    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public BigDecimal decodeInt(byte[] data) {
        return BigDecimal.valueOf(data[0] & 0xFF);
    }

    @Override
    public byte[] encodeInt(Object data) {

        if (data instanceof byte[]) {
            return (byte[]) data;
        }

        BigDecimal b = NumberUtils.toBigDecimal(data);

        if (b == null) {
            return new byte[] { 0x00 };
        }

        return new byte[] { (byte) b.intValue() };
    }

}
