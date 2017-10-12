/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatype.v2;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.v2.EBusTypeNumber;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class StandardTestDummy {

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

        return types.getType(EBusTypeNumber.CHAR, properties);
    }

    @Test
    public void test_CHAR1() throws EBusTypeException {

        IEBusType<?> type = getType(1, false);

        // decode min value: 0
        BigDecimal value = (BigDecimal) type.decode(new byte[] { (byte) 0x00 });
        assertEquals(BigDecimal.valueOf(0), value);

        // encode min value: 0
        byte[] encode = type.encode(value);
        assertArrayEquals(new byte[] { (byte) 0x00 }, encode);

        // decode value: 1
        value = (BigDecimal) type.decode(new byte[] { (byte) 0x01 });
        assertEquals(BigDecimal.valueOf(1), value);

        // decode value: 1
        encode = type.encode(value);
        assertArrayEquals(new byte[] { (byte) 0x01 }, encode);

        // decode value: 1
        value = (BigDecimal) type.decode(new byte[] { (byte) 0x81 });
        assertEquals(BigDecimal.valueOf(-127), value);

        // decode value: 1
        encode = type.encode(value);
        assertArrayEquals(new byte[] { (byte) 0x81 }, encode);

        // decode max value: 65.534
        value = (BigDecimal) type.decode(new byte[] { (byte) 0xFE });
        assertEquals(BigDecimal.valueOf(254), value);

        // decode max value: 65.534
        encode = type.encode(value);
        assertArrayEquals(new byte[] { (byte) 0xFE }, encode);

        // decode FFFF, is replace value
        value = (BigDecimal) type.decode(new byte[] { (byte) 0xFF });
        assertNull(value);

        // encode null - is replace value
        encode = type.encode(null);
        assertArrayEquals(new byte[] { (byte) 0xFF }, encode);
    }

    @Test
    public void test_CHAR2() throws EBusTypeException {

        IEBusType<?> type = getType(2, false);

        // decode min value: 0
        BigDecimal value = (BigDecimal) type.decode(new byte[] { (byte) 0x00, 0x00 });
        assertEquals(BigDecimal.valueOf(0), value);

        // encode min value: 0
        byte[] encode = type.encode(value);
        assertArrayEquals(new byte[] { (byte) 0x00, 0x00 }, encode);

        // decode value: 1
        value = (BigDecimal) type.decode(new byte[] { (byte) 0x01, 0x00 });
        assertEquals(BigDecimal.valueOf(1), value);

        // decode value: 1
        encode = type.encode(value);
        assertArrayEquals(new byte[] { (byte) 0x01, 0x00 }, encode);

        // decode max value: 65.534
        value = (BigDecimal) type.decode(new byte[] { (byte) 0xFE, (byte) 0xFF });
        assertEquals(BigDecimal.valueOf(65534), value);

        // decode max value: 65.534
        encode = type.encode(value);
        assertArrayEquals(new byte[] { (byte) 0xFE, (byte) 0xFF }, encode);

        // decode FFFF, is replace value
        value = (BigDecimal) type.decode(new byte[] { (byte) 0xFF, (byte) 0xFF });
        assertNull(value);

        // encode null - is replace value
        encode = type.encode(null);
        assertArrayEquals(new byte[] { (byte) 0xFF, (byte) 0xFF }, encode);
    }

    @Test
    public void test_CHAR2_Reverse() throws EBusTypeException {

        IEBusType<?> type = getType(2, true);

        // decode min value: 0
        BigDecimal value = (BigDecimal) type.decode(new byte[] { (byte) 0x00, 0x00 });
        assertEquals(BigDecimal.valueOf(0), value);

        // encode min value: 0
        byte[] encode = type.encode(value);
        assertArrayEquals(new byte[] { (byte) 0x00, 0x00 }, encode);

        // decode value: 1
        value = (BigDecimal) type.decode(new byte[] { (byte) 0x00, 0x01 });
        assertEquals(BigDecimal.valueOf(1), value);

        // decode value: 1
        encode = type.encode(value);
        assertArrayEquals(new byte[] { (byte) 0x00, 0x01 }, encode);

        // decode max value: 65.534
        value = (BigDecimal) type.decode(new byte[] { (byte) 0xFF, (byte) 0xFE });
        assertEquals(BigDecimal.valueOf(65534), value);

        // decode max value: 65.534
        encode = type.encode(value);
        assertArrayEquals(new byte[] { (byte) 0xFF, (byte) 0xFE }, encode);

        // decode FFFF, is replace value
        value = (BigDecimal) type.decode(new byte[] { (byte) 0xFF, (byte) 0xFF });
        assertNull(value);

        // encode null - is replace value
        encode = type.encode(null);
        assertArrayEquals(new byte[] { (byte) 0xFF, (byte) 0xFF }, encode);

    }

    @Test
    public void test_ByteDataImutable() throws EBusTypeException {

        IEBusType<?> type = getType(2, true);

        byte[] data = new byte[] { (byte) 0xFF, (byte) 0xFE };
        type.decode(data);

        assertArrayEquals(new byte[] { (byte) 0xFF, (byte) 0xFE }, data);
    }

    @Test
    public void test_CustomReplaceValue() throws EBusTypeException {

        // 2 byte char
        byte[] data1 = new byte[] { (byte) 0x80, (byte) 0x00 };
        EBusTypeNumber type = (EBusTypeNumber) getType(2, false);

        // replace value as decimal
        type.setReplaceValue(128);
        assertNull(type.decode(data1));

        // replace value as byte array
        type.setReplaceValue(new byte[] { (byte) 0x80, (byte) 0x00 });
        assertNull(type.decode(data1));

        // 2 byte char in reverted order
        byte[] data2 = new byte[] { (byte) 0x00, (byte) 0x80 };
        type = (EBusTypeNumber) getType(2, true);

        // replace value as decimal
        type.setReplaceValue(128);
        assertNull(type.decode(data2));

        // replace value as byte array
        type.setReplaceValue(new byte[] { (byte) 0x00, (byte) 0x80 });
        assertNull(type.decode(data2));
    }

}
