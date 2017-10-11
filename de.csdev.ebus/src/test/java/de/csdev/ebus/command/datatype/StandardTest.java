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

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.command.datatypes.std.EBusTypeBCD;
import de.csdev.ebus.command.datatypes.std.EBusTypeChar;
import de.csdev.ebus.command.datatypes.std.EBusTypeData1b;
import de.csdev.ebus.command.datatypes.std.EBusTypeData1c;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class StandardTest {

    EBusTypeRegistry types;

    @Before
    public void before() {
        types = new EBusTypeRegistry();
    }

    @Test
    public void test_PRIMARY() throws EBusTypeException {

        BigDecimal decodeBCD = types.decode(EBusTypeBCD.BCD, new byte[] { (byte) 0x50 });
        assertEquals("Decode BCD failed!", BigDecimal.valueOf(50), decodeBCD);

        decodeBCD = types.decode(EBusTypeBCD.BCD, new byte[] { (byte) 0xFF });
        assertNull("xxxx", decodeBCD);

        BigDecimal decodeChar = types.decode(EBusTypeChar.CHAR, new byte[] { (byte) 0xFA });
        assertEquals("Decode CHAR failed!", BigDecimal.valueOf((byte) 0xFA), decodeChar);

    }

    @Test
    public void test_PRIMARY_encode() throws EBusTypeException {

        byte[] encode = types.encode(EBusTypeBCD.BCD, 50, (Object[]) null);
        assertArrayEquals("Encode BCD failed!", new byte[] { 0x50 }, encode);

    }

    @Test
    public void test_decode_DATA1B() throws EBusTypeException {

        BigDecimal decodeDATA1b = types.decode(EBusTypeData1b.DATA1B, new byte[] { (byte) 0x00 });
        assertEquals("Decode DATA1B failed!", 0, decodeDATA1b.intValue());

        decodeDATA1b = types.decode(EBusTypeData1b.DATA1B, new byte[] { (byte) 0x01 });
        assertEquals("Decode DATA1B failed!", 1, decodeDATA1b.intValue());

        decodeDATA1b = types.decode(EBusTypeData1b.DATA1B, new byte[] { (byte) 0x7F });
        assertEquals("Decode DATA1B failed!", 127, decodeDATA1b.intValue());

        decodeDATA1b = types.decode(EBusTypeData1b.DATA1B, new byte[] { (byte) 0x80 });
        assertNull("Decode DATA1B failed!", decodeDATA1b);

        decodeDATA1b = types.decode(EBusTypeData1b.DATA1B, new byte[] { (byte) 0x81 });
        assertEquals("Decode DATA1B failed!", -127, decodeDATA1b.intValue());
    }

    @Test
    public void test__endcode_DATA1B() throws EBusTypeException {

        byte[] encode = types.encode(EBusTypeData1b.DATA1B, 0, (Object[]) null);
        assertArrayEquals("Encode DATA1B failed!", new byte[] { 0x00 }, encode);

        encode = types.encode(EBusTypeData1b.DATA1B, 1, (Object[]) null);
        assertArrayEquals("Encode DATA1B failed!", new byte[] { 0x01 }, encode);

        encode = types.encode(EBusTypeData1b.DATA1B, 127, (Object[]) null);
        assertArrayEquals("Encode DATA1B failed!", new byte[] { 0x7F }, encode);

        encode = types.encode(EBusTypeData1b.DATA1B, -128, (Object[]) null);
        assertArrayEquals("Encode DATA1B failed!", new byte[] { (byte) 0x80 }, encode);

        encode = types.encode(EBusTypeData1b.DATA1B, -127, (Object[]) null);
        assertArrayEquals("Encode DATA1B failed!", new byte[] { (byte) 0x81 }, encode);
    }

    @Test
    public void test_decode_DATA1C() throws EBusTypeException {
        BigDecimal decodeDATA1c = types.decode(EBusTypeData1c.DATA1C, new byte[] { (byte) 0x00 });
        assertEquals("Decode DATA1B failed!", 0f, decodeDATA1c.floatValue(), 0.1f);

        decodeDATA1c = types.decode(EBusTypeData1c.DATA1C, new byte[] { (byte) 0x64 });
        assertEquals("Decode DATA1B failed!", 50f, decodeDATA1c.floatValue(), 0.1f);

        decodeDATA1c = types.decode(EBusTypeData1c.DATA1C, new byte[] { (byte) 0xC8 });
        assertEquals("Decode DATA1B failed!", 100f, decodeDATA1c.floatValue(), 0.1f);
    }

    @Test
    public void test_encode_DATA1C() throws EBusTypeException {

        byte[] encode = types.encode(EBusTypeData1c.DATA1C, 0f, (Object[]) null);
        assertArrayEquals("Encode DATA1C failed!", new byte[] { 0x00 }, encode);

        encode = types.encode(EBusTypeData1c.DATA1C, 50f, (Object[]) null);
        assertArrayEquals("Encode DATA1C failed!", new byte[] { 0x64 }, encode);

        encode = types.encode(EBusTypeData1c.DATA1C, 100f, (Object[]) null);
        assertArrayEquals("Encode DATA1C failed!", new byte[] { (byte) 0xC8 }, encode);
    }

}
