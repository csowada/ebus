/**
 * Copyright (c) 2016-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatype.std;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.std.EBusTypeInteger;

public class IntegerTest {

    EBusTypeRegistry types;

    @Before
    public void before() {
        types = new EBusTypeRegistry();
    }

    private void check(IEBusType<?> type, byte[] bs, int result) throws EBusTypeException {
        BigDecimal value = (BigDecimal) type.decode(bs);
        assertEquals(result, value.intValue());

        byte[] encode = type.encode(value.floatValue());
        assertArrayEquals(bs, encode);
    }

    private void checkReplaceValue(IEBusType<?> type, byte[] bs) throws EBusTypeException {
        BigDecimal value = (BigDecimal) type.decode(bs);
        assertNull(value);

        byte[] encode = type.encode(value);
        assertArrayEquals(bs, encode);
    }

    @Test
    public void test_Integer() throws EBusTypeException {

        IEBusType<BigDecimal> type = types.getType(EBusTypeInteger.TYPE_INTEGER);

        check(type, new byte[] { (byte) 0x00, 0x00 }, 0);

        check(type, new byte[] { (byte) 0x01, 0x00 }, 1);

        check(type, new byte[] { (byte) 0xFF, (byte) 0xFF }, -1);

        check(type, new byte[] { (byte) 0xFF, (byte) 0x7F }, 32767);

        check(type, new byte[] { (byte) 0x01, (byte) 0x80 }, -32767);

        checkReplaceValue(type, new byte[] { (byte) 0x00, (byte) 0x80 });

    }

    @Test
    public void test_Integer_Reverse() throws EBusTypeException {

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(IEBusType.REVERSED_BYTE_ORDER, Boolean.TRUE);
        IEBusType<BigDecimal> type = types.getType(EBusTypeInteger.TYPE_INTEGER, properties);

        check(type, new byte[] { (byte) 0x00, 0x00 }, 0);

        check(type, new byte[] { (byte) 0x00, 0x01 }, 1);

        check(type, new byte[] { (byte) 0xFF, (byte) 0xFF }, -1);

        check(type, new byte[] { (byte) 0x7F, (byte) 0xFF }, 32767);

        check(type, new byte[] { (byte) 0x80, (byte) 0x01 }, -32767);

        checkReplaceValue(type, new byte[] { (byte) 0x80, (byte) 0x00 });
    }
}
