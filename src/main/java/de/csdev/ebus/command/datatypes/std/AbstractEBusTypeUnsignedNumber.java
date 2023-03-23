/**
 * Copyright (c) 2017-2023 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes.std;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import de.csdev.ebus.command.datatypes.EBusAbstractType;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.utils.NumberUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
@NonNullByDefault
public abstract class AbstractEBusTypeUnsignedNumber extends EBusAbstractType<BigDecimal> {

    @Override
    public @Nullable BigDecimal decodeInt(byte @Nullable [] data) throws EBusTypeException {

        byte[] clone = ArrayUtils.clone(data);
        ArrayUtils.reverse(clone);

        return new BigDecimal(new BigInteger(1, clone));
    }

    @Override
    public byte @Nullable [] encodeInt(@Nullable Object data) throws EBusTypeException {

        BigDecimal b = NumberUtils.toBigDecimal(data == null ? 0 : data);

        if (b == null) {
            throw new EBusTypeException("Unable to convert input data to number!");
        }

        long l = b.longValue() & Long.MAX_VALUE;

        int length = getTypeLength();
        byte[] result = new byte[length];
        for (int i = 0; i <= length - 1; i++) {
            result[i] = (byte) (l & 0xFF);
            l >>= 8;
        }

        return result;
    }

}
