/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes.ext;

import java.math.BigDecimal;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import de.csdev.ebus.command.datatypes.EBusAbstractType;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.std.EBusTypeWord;
import de.csdev.ebus.utils.EBusUtils;
import de.csdev.ebus.utils.NumberUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
@NonNullByDefault
public class EBusTypeMultiWord extends EBusAbstractType<BigDecimal> {

    public static String TYPE_MWORD = "mword";

    public static String BLOCK_MULTIPLIER = "multiplier";

    private static String[] supportedTypes = new String[] { TYPE_MWORD };

    private int length = 2;
    
    @SuppressWarnings({"null"})
    private BigDecimal multiplier = BigDecimal.valueOf(1000);

    @Override
    public String @NonNull [] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLength() {
        return length * 2;
    }

    @Override
    public @Nullable BigDecimal decodeInt(byte @Nullable [] data) throws EBusTypeException {

        byte[] dataNew = new byte[2];

        int x = this.length - 1;

        BigDecimal valx = new BigDecimal(0);

        for (int i = 0; i <= x; i++) {

            System.arraycopy(data, i * 2, dataNew, 0, dataNew.length);

            BigDecimal value = types.decode(EBusTypeWord.TYPE_WORD, dataNew);

            if (value == null) {
                throw new EBusTypeException("Unable to convert data to type WORD!");
            }

            BigDecimal factor = this.multiplier.pow(i);
            valx = valx.add(value.multiply(factor));
        }

        return valx;
    }

    @Override
    public byte[] encodeInt(@Nullable Object data) throws EBusTypeException {

        BigDecimal value = NumberUtils.toBigDecimal(data);

        if (value == null) {
            throw new EBusTypeException("Unable to convert input data to number!");
        }

        byte[] result = new byte[getTypeLength()];

        int length = this.length - 1;

        for (int i = length; i >= 0; i--) {

            BigDecimal factor = this.multiplier.pow(i);
            BigDecimal[] divideAndRemainder = value.divideAndRemainder(factor);

            byte[] encode = types.encode(EBusTypeWord.TYPE_WORD, divideAndRemainder[0]);

            value = divideAndRemainder[1];
            System.arraycopy(encode, 0, result, i * 2, 2);
        }

        return result;
    }

    @Override
    public String toString() {
        return "EBusTypeMultiWord [length=" + length + ", multiplier=" + multiplier + ", replaceValue="
                + EBusUtils.toHexDumpString(getReplaceValue()).toString() + "]";
    }

}
