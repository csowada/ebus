/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatype;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.v2.EBusTypeChar3;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class Char {

    EBusTypeRegistry types;

    @Before
    public void before() {
        types = new EBusTypeRegistry();
    }

    private IEBusType<?> getType(int length, boolean reverseByteOrder) {
        Map<String, Object> properties = new HashMap<String, Object>();

        properties.put(IEBusType.LENGTH, length);

        if (reverseByteOrder) {
            properties.put(IEBusType.REVERSED_BYTE_ORDER, Boolean.TRUE);
        }

        return types.getType(EBusTypeChar3.CHAR, properties);
    }

    private void check(IEBusType<?> type, byte[] bs, int result) throws EBusTypeException {
        BigDecimal value = (BigDecimal) type.decode(bs);
        assertEquals(BigDecimal.valueOf(result), value);

        byte[] encode = type.encode(value);
        assertArrayEquals(bs, encode);
    }

    private void checkReplaceValue(IEBusType<?> type, byte[] bs) throws EBusTypeException {
        BigDecimal value = (BigDecimal) type.decode(bs);
        assertNull(value);

        byte[] encode = type.encode(value);
        assertArrayEquals(bs, encode);
    }

    @Test
    public void test_CHAR_Default() throws EBusTypeException {

        IEBusType<?> type = types.getType(EBusTypeChar3.CHAR, null);

        check(type, new byte[] { (byte) 0x00 }, 0);

        check(type, new byte[] { (byte) 0x7F }, 127);

        checkReplaceValue(type, new byte[] { (byte) 0x80 }); // replace value

        check(type, new byte[] { (byte) 0x81 }, -127);

        check(type, new byte[] { (byte) 0xFE }, -2);

    }

    @Test
    public void test_CHAR1() throws EBusTypeException {
        IEBusType<?> type = getType(1, false);

        check(type, new byte[] { (byte) 0x00 }, 0);

        check(type, new byte[] { (byte) 0x7F }, 127);

        checkReplaceValue(type, new byte[] { (byte) 0x80 }); // replace value

        check(type, new byte[] { (byte) 0x81 }, -127);

        check(type, new byte[] { (byte) 0xFE }, -2);

    }

    @Test
    public void test_CHAR2() throws EBusTypeException {

        IEBusType<?> type = getType(2, false);

        check(type, new byte[] { (byte) 0x00, 0x00 }, 0);

        check(type, new byte[] { (byte) 0x01, 0x00 }, 1);

        check(type, new byte[] { (byte) 0xFF, (byte) 0x7F }, 32767);

        checkReplaceValue(type, new byte[] { (byte) 0x00, (byte) 0x80 }); // replace value

        check(type, new byte[] { (byte) 0x01, (byte) 0x80 }, -32767);

    }

    @Test
    public void test_CHAR2_Reverse() throws EBusTypeException {

        IEBusType<?> type = getType(2, true);

        check(type, new byte[] { (byte) 0x00, 0x00 }, 0);

        check(type, new byte[] { (byte) 0x00, 0x01 }, 1);

        check(type, new byte[] { (byte) 0x7F, (byte) 0xFF }, 32767);

        checkReplaceValue(type, new byte[] { (byte) 0x80, (byte) 0x00 }); // replace value

        check(type, new byte[] { (byte) 0x80, (byte) 0x01 }, -32767);

    }
}
