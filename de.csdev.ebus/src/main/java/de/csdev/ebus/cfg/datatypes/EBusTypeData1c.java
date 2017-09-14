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

import de.csdev.ebus.utils.EBusUtils;
import de.csdev.ebus.utils.NumberUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeData1c extends EBusTypeGenericReplaceValue {

    public static String DATA1C = "data1c";

    private static String[] supportedTypes = new String[] { DATA1C };

    public EBusTypeData1c() {
        replaceValue = new byte[] { (byte) 0xFF };
    }

    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public BigDecimal decodeInt(byte[] data) throws EBusTypeException {
        BigDecimal x = (BigDecimal) types.getType(EBusTypeByte.BYTE).decode(data);
        return x.divide(BigDecimal.valueOf(2));
    }

    @Override
    public byte[] encodeInt(Object data) {
        BigDecimal b = NumberUtils.toBigDecimal(data);
        b = b.multiply(BigDecimal.valueOf(2));
        return new byte[] { (byte) b.intValue() };
    }

    @Override
    public void setTypesParent(EBusTypes types) {
        this.types = types;
    }

    @Override
    public String toString() {
        return "EBusTypeData1c [replaceValue=" + EBusUtils.toHexDumpString(replaceValue).toString() + "]";
    }

}
