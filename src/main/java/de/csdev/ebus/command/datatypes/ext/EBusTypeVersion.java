/**
 * Copyright (c) 2017-2021 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes.ext;

import java.math.BigDecimal;

import org.eclipse.jdt.annotation.Nullable;

import de.csdev.ebus.command.datatypes.EBusAbstractType;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.std.EBusTypeBCD;
import de.csdev.ebus.utils.NumberUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeVersion extends EBusAbstractType<BigDecimal> {

    public static String TYPE_VERSION = "version";

    private static String[] supportedTypes = new String[] { TYPE_VERSION };

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLength() {
        return 2;
    }

    @Override
    public BigDecimal decodeInt(byte @Nullable [] data) throws EBusTypeException {

        if (data == null) {
            throw new IllegalArgumentException();
        }

        byte[] verData = new byte[] { data[0] };
        byte[] revData = new byte[] { data[1] };

        BigDecimal ver = types.decode(EBusTypeBCD.TYPE_BCD, verData);
        BigDecimal rev = types.decode(EBusTypeBCD.TYPE_BCD, revData);

        if (ver != null && rev != null) {
            BigDecimal fraction = rev.divide(BigDecimal.valueOf(100));
            return ver.add(fraction);
        }

        return null;
    }

    @Override
    public byte[] encodeInt(@Nullable Object data) throws EBusTypeException {

        BigDecimal value = NumberUtils.toBigDecimal(data);

        if (value == null) {
            return applyByteOrder(getReplaceValue());
        }

        BigDecimal[] values = value.divideAndRemainder(BigDecimal.ONE);
        values[1] = values[1].multiply(BigDecimal.valueOf(100));

        byte[] encode1 = types.encode(EBusTypeBCD.TYPE_BCD, values[0]);
        byte[] encode2 = types.encode(EBusTypeBCD.TYPE_BCD, values[1]);

        return new byte[] { encode1[0], encode2[0] };
    }

    @Override
    public String toString() {
        return "EBusTypeVersion [length=2]";
    }

}
