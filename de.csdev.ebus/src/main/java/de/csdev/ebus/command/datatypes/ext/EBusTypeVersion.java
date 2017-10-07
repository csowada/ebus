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

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeGeneric;
import de.csdev.ebus.command.datatypes.std.EBusTypeBCD;
import de.csdev.ebus.utils.NumberUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeVersion extends EBusTypeGeneric<BigDecimal> {

    public static String VERSION = "version";

    private static String[] supportedTypes = new String[] { VERSION };

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLenght() {
        return 2;
    }

    @Override
    public BigDecimal decode(byte[] data) throws EBusTypeException {

        if (data[0] == 0 && data[1] == 0) {
            return null;
        }

        byte[] verData = new byte[] { data[0] };
        byte[] revData = new byte[] { data[1] };

        BigDecimal ver = types.decode(EBusTypeBCD.BCD, verData);
        BigDecimal rev = types.decode(EBusTypeBCD.BCD, revData);

        if (ver != null && rev != null) {
            BigDecimal fraction = rev.divide(BigDecimal.valueOf(100));
            return ver.add(fraction);
        }

        return null;
    }

    @Override
    public byte[] encode(Object data) throws EBusTypeException {

        BigDecimal value = NumberUtils.toBigDecimal(data);

        if (value == null) {
            return new byte[getTypeLenght()];
        }

        BigDecimal[] values = value.divideAndRemainder(BigDecimal.ONE);
        values[1] = values[1].multiply(BigDecimal.valueOf(100));

        byte[] encode1 = types.encode(EBusTypeBCD.BCD, values[0]);
        byte[] encode2 = types.encode(EBusTypeBCD.BCD, values[1]);

        return new byte[] { encode1[0], encode2[0] };
    }

    @Override
    public String toString() {
        return "EBusTypeVersion [length=2]";
    }

}
