/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes.ext;

import java.math.BigDecimal;
import java.util.Map;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeGeneric;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.std.EBusTypeWord;
import de.csdev.ebus.utils.NumberUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeMultiWord extends EBusTypeGeneric<BigDecimal> {

    public static String MWORD = "mword";

    public static String BLOCK_MULTIPLIER = "block-mul";

    private static String[] supportedTypes = new String[] { MWORD };

    private int length = 2;
    private BigDecimal multiplier = BigDecimal.valueOf(1000);

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLenght() {
        return length * 2;
    }

    @Override
    public BigDecimal decode(byte[] data) throws EBusTypeException {

        byte[] dataNew = new byte[2];

        int x = this.length - 1;

        BigDecimal valx = new BigDecimal(0);

        for (int i = 0; i <= x; i++) {

            System.arraycopy(data, i * 2, dataNew, 0, dataNew.length);
            dataNew = applyByteOrder(dataNew);
            BigDecimal value = types.decode(EBusTypeWord.WORD, dataNew);

            BigDecimal factor = this.multiplier.pow(i);
            valx = valx.add(value.multiply(factor));
        }

        return valx;
    }

    @Override
    public byte[] encode(Object data) throws EBusTypeException {

        BigDecimal value = NumberUtils.toBigDecimal(data);
        byte[] result = new byte[getTypeLenght()];

        if (value == null) {
            return result;
        }

        int length = this.length - 1;

        for (int i = length; i >= 0; i--) {

            BigDecimal factor = this.multiplier.pow(i);
            BigDecimal[] divideAndRemainder = value.divideAndRemainder(factor);

            byte[] encode = types.encode(EBusTypeWord.WORD, divideAndRemainder[0]);
            encode = applyByteOrder(encode);
            value = divideAndRemainder[1];
            System.arraycopy(encode, 0, result, i * 2, 2);
        }

        return result;
    }

    @Override
    public IEBusType<BigDecimal> getInstance(Map<String, Object> properties) {

        if (properties.containsKey(IEBusType.LENGTH)) {
            EBusTypeMultiWord type = new EBusTypeMultiWord();
            type.types = this.types;

            type.length = (Integer) properties.get(IEBusType.LENGTH);

            if (properties.containsKey(BLOCK_MULTIPLIER)) {
                type.multiplier = NumberUtils.toBigDecimal(properties.get(BLOCK_MULTIPLIER));
                // type.factor = (Integer) properties.get(IEBusType.FACTOR);
            }

            return type;
        }

        return this;
    }

    @Override
    public String toString() {
        return "EBusTypeMultiWord [length=" + length + ", multiplier=" + multiplier + "]";
    }

}
