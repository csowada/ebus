/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatype.ext;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.ext.EBusTypeString;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class StringTest {

    private EBusTypeRegistry types;

    @Before
    public void before() throws EBusTypeException {
        types = new EBusTypeRegistry();
    }

    @Test
    public void test_String() throws EBusTypeException {

        byte[] value = new byte[] { 0x48, 0x65, 0x6c, 0x6c, 0x6f, 0x20, 0x57, 0x6f, 0x72, 0x6c, 0x64, 0x00, 0x00 };

        Map<String, Object> properties = new HashMap<String, Object>();

        properties.put(IEBusType.LENGTH, value.length);
        IEBusType<String> type = types.getType(EBusTypeString.TYPE_STRING, properties);

        String str = type.decode(value);
        assertNotNull(str);
        assertEquals("Hello World", str.trim());

    }

    @Test
    public void test_String2() throws EBusTypeException {

        byte[] value = new byte[] { 0x48, 0x65, 0x6c, 0x6c, 0x6f, 0x20, 0x57, 0x6f, 0x72, 0x6c, 0x64 };

        Map<String, Object> properties = new HashMap<String, Object>();

        properties.put(IEBusType.LENGTH, 11);
        IEBusType<String> type = types.getType(EBusTypeString.TYPE_STRING, properties);

        byte[] bs = type.encode("Hello World");
        assertArrayEquals(value, bs);

    }

}
