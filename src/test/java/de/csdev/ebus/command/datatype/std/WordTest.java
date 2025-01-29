/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
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
import de.csdev.ebus.command.datatypes.std.EBusTypeWord;

public class WordTest {

    EBusTypeRegistry types;

    @Before
    public void before() throws EBusTypeException {
        types = new EBusTypeRegistry();
    }

    private void check(IEBusType<?> type, byte[] bs, int result) throws EBusTypeException {
        BigDecimal value = (BigDecimal) type.decode(bs);

        assertNotNull(value);
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
    public void test_Word() throws EBusTypeException {

        IEBusType<BigDecimal> type = types.getType(EBusTypeWord.TYPE_WORD);

        check(type, new byte[] { (byte) 0x00, 0x00 }, 0);

        check(type, new byte[] { (byte) 0x01, 0x00 }, 1);

        check(type, new byte[] { (byte) 0xFF, 0x00 }, 255);

        check(type, new byte[] { (byte) 0x00, 0x01 }, 256);

        check(type, new byte[] { (byte) 0x00, (byte) 0x80 }, 32768);

        check(type, new byte[] { (byte) 0xFE, (byte) 0xFF }, 65534);

        checkReplaceValue(type, new byte[] { (byte) 0xFF, (byte) 0xFF });

    }

    @Test
    public void test_Word_Reverse() throws EBusTypeException {

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(IEBusType.REVERSED_BYTE_ORDER, Boolean.TRUE);
        IEBusType<BigDecimal> type = types.getType(EBusTypeWord.TYPE_WORD, properties);

        check(type, new byte[] { (byte) 0x00, 0x00 }, 0);

        check(type, new byte[] { (byte) 0x00, 0x01 }, 1);

        check(type, new byte[] { (byte) 0x00, (byte) 0xFF }, 255);

        check(type, new byte[] { (byte) 0x01, 0x00 }, 256);

        check(type, new byte[] { (byte) 0x80, (byte) 0x00 }, 32768);

        check(type, new byte[] { (byte) 0xFF, (byte) 0xFE }, 65534);

        checkReplaceValue(type, new byte[] { (byte) 0xFF, (byte) 0xFF });
    }
}
