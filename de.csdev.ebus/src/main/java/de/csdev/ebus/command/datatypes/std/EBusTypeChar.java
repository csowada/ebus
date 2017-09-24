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
public class EBusTypeChar extends EBusTypeGenericReplaceValue {

    public static String CHAR = "char";

    private static String[] supportedTypes = new String[] { CHAR };

    public EBusTypeChar() {
        replaceValue = new byte[] { (byte) 0xFF };
    }

    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public BigDecimal decodeInt(byte[] data) {
        return BigDecimal.valueOf(data[0]);
    }

    @Override
    public byte[] encodeInt(Object data) {
        BigDecimal b = NumberUtils.toBigDecimal(data == null ? 0 : data);
        return new byte[] { (byte) ((byte) b.intValue() & 0xFF) };
    }

    @Override
    public String toString() {
        return "EBusTypeChar [replaceValue=" + EBusUtils.toHexDumpString(replaceValue).toString() + "]";
    }

}
