/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatype.ext;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.ext.EBusTypeMultiWord;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class MultiWordTest {

    EBusTypeRegistry types;

    @Before
    public void before() {
        types = new EBusTypeRegistry();
    }

    @Test
    public void test_MultiWordLength() throws EBusTypeException {

        byte[] testValue = new byte[] { 0x3D, 0x02, (byte) 0x88, 0x01, 0x05, 0x00 };

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(IEBusType.LENGTH, 3);
        IEBusType<?> type = types.getType(EBusTypeMultiWord.MWORD, properties);

        // decode
        Object decode = type.decode(testValue);
        assertEquals("Decode MultiWord failed!", BigDecimal.valueOf(5392573), decode);

        // encode
        byte[] encode = type.encode(decode);
        assertArrayEquals(testValue, encode);
    }

    @Test
    public void test_MultiWord() throws EBusTypeException {

        byte[] testValue = new byte[] { (byte) 0xF9, 0x00, 0x07, 0x00 };

        // decode
        Object decode = types.decode(EBusTypeMultiWord.MWORD, testValue);
        assertEquals("Decode MultiWord failed!", BigDecimal.valueOf(7249), decode);

        // encode
        byte[] encode = types.encode(EBusTypeMultiWord.MWORD, decode);
        assertArrayEquals(testValue, encode);
    }

    // @Test
    public void test_MultiWordFactor() throws EBusTypeException {

        byte[] testValue = new byte[] { 0x3D, 0x02, (byte) 0x88, 0x01, 0x05, 0x00 };

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(IEBusType.LENGTH, 3);
        properties.put(IEBusType.FACTOR, 500);
        IEBusType<?> type = types.getType(EBusTypeMultiWord.MWORD, properties);

        // decode
        Object decode = type.decode(testValue);
        assertEquals("Decode MultiWord failed!", BigDecimal.valueOf(1446573), decode);

        // encode
        byte[] encode = type.encode(decode);
        assertArrayEquals(testValue, encode);
    }

}
