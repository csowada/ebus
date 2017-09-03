/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg.datatypes.ext;

import java.math.BigDecimal;
import java.util.Map;

import de.csdev.ebus.cfg.datatypes.EBusTypeException;
import de.csdev.ebus.cfg.datatypes.EBusTypeGeneric;
import de.csdev.ebus.cfg.datatypes.EBusTypeWord;
import de.csdev.ebus.cfg.datatypes.IEBusType;
import de.csdev.ebus.utils.NumberUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeMultiWord extends EBusTypeGeneric<BigDecimal> {

    public static String MWORD = "mword";

    private static String[] supportedTypes = new String[] { MWORD };

    private int length = 2;
    private int pow = 1000;

    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLenght() {
        return length * 2;
    }

    public BigDecimal decode(byte[] data) throws EBusTypeException {

        byte[] dataNew = new byte[2];

        int x = this.length - 1;

        BigDecimal valx = new BigDecimal(0);

        for (int i = 0; i <= x; i++) {

            System.arraycopy(data, i * 2, dataNew, 0, dataNew.length);
            BigDecimal value = types.decode(EBusTypeWord.WORD, dataNew);

            BigDecimal factor = new BigDecimal(this.pow).pow(i);
            valx = valx.add(value.multiply(factor));
        }

        return valx;
    }

    public byte[] encode(Object data) throws EBusTypeException {

        BigDecimal value = NumberUtils.toBigDecimal(data);
        byte[] result = new byte[getTypeLenght()];

        if (value == null) {
            return result;
        }

        int length = this.length - 1;

        for (int i = length; i >= 0; i--) {

            BigDecimal factor = new BigDecimal(this.pow).pow(i);
            BigDecimal[] divideAndRemainder = value.divideAndRemainder(factor);

            byte[] encode = types.encode(EBusTypeWord.WORD, divideAndRemainder[0]);

            value = divideAndRemainder[1];
            System.arraycopy(encode, 0, result, i * 2, 2);
        }

        return result;
    }

    @Override
    public IEBusType<BigDecimal> getInstance(Map<String, Object> properties) {

        if (properties.containsKey("length")) {
            EBusTypeMultiWord type = new EBusTypeMultiWord();
            type.types = this.types;

            type.length = (Integer) properties.get("length");

            if (properties.containsKey("pow")) {
                type.pow = (Integer) properties.get("pow");
            }

            return type;
        }

        return this;
    }

}
