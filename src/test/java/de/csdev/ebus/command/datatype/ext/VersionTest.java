/**
 * Copyright (c) 2017-2021 by the respective copyright holders.
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
import de.csdev.ebus.command.datatypes.ext.EBusTypeVersion;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class VersionTest {

    EBusTypeRegistry types;

    @Before
    public void before() throws EBusTypeException {
        types = new EBusTypeRegistry();
    }

    @Test
    public void test_Version() throws EBusTypeException {

        byte[] testValue = new byte[] { 0x02, 0x27 };
        IEBusType<?> type = types.getType(EBusTypeVersion.TYPE_VERSION);

        // decode
        Object decode = type.decode(testValue);
        assertEquals("Decode Version failed!", BigDecimal.valueOf(227, 2), decode);

        // encode
        byte[] encode = type.encode(decode);
        assertArrayEquals(testValue, encode);
    }

    @Test
    public void test_Version2() throws EBusTypeException {

        byte[] testValue = new byte[] { 0x02, 0x01 };
        IEBusType<?> type = types.getType(EBusTypeVersion.TYPE_VERSION);

        // decode
        Object decode = type.decode(testValue);
        assertEquals("Decode Version failed!", BigDecimal.valueOf(201, 2), decode);

        // encode
        byte[] encode = type.encode(decode);
        assertArrayEquals(testValue, encode);
    }

}
