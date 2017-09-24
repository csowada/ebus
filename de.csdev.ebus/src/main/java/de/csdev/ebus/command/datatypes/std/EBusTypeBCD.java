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

import de.csdev.ebus.command.datatypes.EBusTypeGenericReplaceValue;
import de.csdev.ebus.utils.EBusUtils;
import de.csdev.ebus.utils.NumberUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeBCD extends EBusTypeGenericReplaceValue {

    public static String BCD = "bcd";

    private static String[] supportedTypes = new String[] { BCD };

    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    public EBusTypeBCD() {
        replaceValue = new byte[] { (byte) 0xFF };
    }

    @Override
    public BigDecimal decodeInt(byte[] data) {
        return BigDecimal.valueOf((byte) ((data[0] >> 4) * 10 + (data[0] & (byte) 0x0F)));
    }

    @Override
    public byte[] encodeInt(Object data) {
        BigDecimal b = NumberUtils.toBigDecimal(data);
        return new byte[] { (byte) (((b.intValue() / 10) << 4) | b.intValue() % 10) };
    }

    @Override
    public String toString() {
        return "EBusTypeBCD [replaceValue=" + EBusUtils.toHexDumpString(replaceValue).toString() + "]";
    }

}
