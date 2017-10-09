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
import java.util.Map;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeGenericReplaceValue;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.utils.EBusUtils;
import de.csdev.ebus.utils.NumberUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeChar2 extends EBusTypeGenericReplaceValue {

    public static String CHAR = "char2";

    private static String[] supportedTypes = new String[] { CHAR };

    private int length = 1;

    public EBusTypeChar2() {
        this(1);
    }

    public EBusTypeChar2(int length) {
        this.length = length;
        replaceValue = new byte[length];
        for (int i = 0; i < replaceValue.length; i++) {
            replaceValue[i] = (byte) 0xFF;
        }
    }

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLenght() {
        return length;
    }

    @Override
    public BigDecimal decodeInt(byte[] data) throws EBusTypeException {

        if (data.length != getTypeLenght()) {
            throw new EBusTypeException("Decode byte-array has the wrong length!");
        }

        applyByteOrder(data);

        long result = 0;
        for (int i = length; i > 0; i--) {
            result <<= 8;
            result |= (data[i - 1] & 0xFF);
        }

        return BigDecimal.valueOf(result);
    }

    @Override
    public byte[] encodeInt(Object data) {

        BigDecimal b = NumberUtils.toBigDecimal(data == null ? 0 : data);
        long l = b.longValue();

        byte[] result = new byte[length];
        for (int i = 0; i <= length - 1; i++) {
            result[i] = (byte) (l & 0xFF);
            l >>= 8;
        }

        applyByteOrder(result);

        return result;
    }

    @Override
    public IEBusType<BigDecimal> getInstance(Map<String, Object> properties) {

        if (properties.containsKey(IEBusType.LENGTH)) {

            int len = (Integer) properties.get(IEBusType.LENGTH);

            EBusTypeChar2 type = (EBusTypeChar2) otherInstances.get(len);
            if (type == null) {
                type = new EBusTypeChar2(len);
                type.applyNewInstanceProperties(type, properties);
                otherInstances.put(length, type);
            }

            return type;
        }

        return this;
    }

    @Override
    public String toString() {
        return "EBusTypeChar2 [replaceValue=" + EBusUtils.toHexDumpString(replaceValue).toString() + "]";
    }

}
