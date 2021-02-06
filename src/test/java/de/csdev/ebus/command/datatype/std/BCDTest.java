/**
 * Copyright (c) 2017-2021 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatype.std;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.std.EBusTypeBCD;

public class BCDTest {

    EBusTypeRegistry types;

    @Before
    public void before() throws EBusTypeException {
        types = new EBusTypeRegistry();
    }

    private void check(IEBusType<?> type, byte[] bs, int result) throws EBusTypeException {
        BigDecimal value = (BigDecimal) type.decode(bs);

        assertNotNull(value);
        assertEquals(result, value.intValue());

        byte[] encode = type.encode(value);
        assertArrayEquals(bs, encode);
    }

    private void checkReplaceValue(IEBusType<?> type, byte[] bs) throws EBusTypeException {
        BigDecimal value = (BigDecimal) type.decode(bs);

        assertNull(value);

        byte[] encode = type.encode(value);
        assertArrayEquals(new byte[] { (byte) 0xFF }, encode);
    }

    @Test
    public void test_BCD() throws EBusTypeException {

        IEBusType<BigDecimal> type = types.getType(EBusTypeBCD.TYPE_BCD);

        check(type, new byte[] { (byte) 0x00 }, 0);

        check(type, new byte[] { (byte) 0x01 }, 1);

        check(type, new byte[] { (byte) 0x10 }, 10);

        check(type, new byte[] { (byte) 0x50 }, 50);

        check(type, new byte[] { (byte) 0x80 }, 80);

        check(type, new byte[] { (byte) 0x99 }, 99);

        checkReplaceValue(type, new byte[] { (byte) 0x3D });

        checkReplaceValue(type, new byte[] { (byte) 0xFF });
    }

    @Test
    public void test_BCD_Len2() throws EBusTypeException {
        IEBusType<BigDecimal> type = types.getType(EBusTypeBCD.TYPE_BCD, IEBusType.LENGTH, 2);

        check(type, new byte[] { (byte) 0x12, 0x34 }, 1234);
    }

    @Test
    public void test_BCD_Len3() throws EBusTypeException {

        IEBusType<BigDecimal> type = types.getType(EBusTypeBCD.TYPE_BCD, IEBusType.LENGTH, 3);

        check(type, new byte[] { (byte) 0x12, 0x34, (byte) 0x99 }, 123499);
    }

    @Test
    public void test_BCD_Len3_Rev() throws EBusTypeException {

        IEBusType<BigDecimal> type = types.getType(EBusTypeBCD.TYPE_BCD, IEBusType.LENGTH, 3,
                IEBusType.REVERSED_BYTE_ORDER, true);

        check(type, new byte[] { (byte) 0x12, 0x34, (byte) 0x99 }, 993412);
    }
}
