/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatype.ext;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.std.EBusTypeFloat;

public class FloatTest {

    EBusTypeRegistry types;

    @Before
    public void before() {
        types = new EBusTypeRegistry();
    }

    private void check(IEBusType<?> type, byte[] bs, float result) throws EBusTypeException {
        BigDecimal value = (BigDecimal) type.decode(bs);
        assertEquals(result, value.floatValue(), 0.1f);

        byte[] encode = type.encode(value.floatValue());
        assertArrayEquals(bs, encode);
    }

    @Test
    public void test_Integer() throws EBusTypeException {

        IEBusType<BigDecimal> type = types.getType(EBusTypeFloat.TYPE_FLOAT);

        check(type, new byte[] { 0x00, 0x00, 0x44, 0x42 }, 49.0f);

        check(type, new byte[] { 0x00, 0x00, 0x38, 0x42 }, 46.0f);

        // invalid value, result in NaN
        BigDecimal result = type.decode(new byte[] { (byte) 0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF });
        assertNull(result);

        // check replace value
        byte[] encode = type.encode(null);
        assertArrayEquals(new byte[] { (byte) 0x7F, (byte) 0x80, (byte) 0x00, (byte) 0x00 }, encode);
    }

    @Test
    public void test_Integer_Reverse() throws EBusTypeException {

        IEBusType<BigDecimal> type = types.getType(EBusTypeFloat.TYPE_FLOAT, IEBusType.REVERSED_BYTE_ORDER,
                Boolean.TRUE);

        check(type, new byte[] { 0x42, 0x44, 0x00, 0x00 }, 49.0f);

        check(type, new byte[] { 0x42, 0x38, 0x00, 0x00 }, 46.0f);

        // invalid value, result in NaN
        BigDecimal result = type.decode(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x7F });
        assertNull(result);

        // check replace value
        byte[] encode = type.encode(null);
        assertArrayEquals(new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x80, (byte) 0x7F }, encode);
    }
}
