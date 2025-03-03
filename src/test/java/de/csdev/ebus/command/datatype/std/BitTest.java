/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatype.std;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.std.EBusTypeBit;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class BitTest {

    EBusTypeRegistry types;

    @Before
    public void before() throws EBusTypeException {
        types = new EBusTypeRegistry();
    }

    public IEBusType<?> getType(int bitPos) {
        return types.getType(EBusTypeBit.TYPE_BIT, EBusTypeBit.POS, bitPos);
    }

    @Test
    public void test_Byte() throws EBusTypeException {

        IEBusType<?> typeB0 = getType(0);
        IEBusType<?> typeB1 = getType(1);
        IEBusType<?> typeB2 = getType(2);
        IEBusType<?> typeB3 = getType(3);
        IEBusType<?> typeB4 = getType(4);
        IEBusType<?> typeB5 = getType(5);
        IEBusType<?> typeB6 = getType(6);
        IEBusType<?> typeB7 = getType(7);

        byte[] b = new byte[] { (byte) 0xA3 };

        assertEquals(typeB0.decode(b), Boolean.TRUE);
        assertEquals(typeB1.decode(b), Boolean.TRUE);
        assertEquals(typeB2.decode(b), Boolean.FALSE);
        assertEquals(typeB3.decode(b), Boolean.FALSE);
        assertEquals(typeB4.decode(b), Boolean.FALSE);
        assertEquals(typeB5.decode(b), Boolean.TRUE);
        assertEquals(typeB6.decode(b), Boolean.FALSE);
        assertEquals(typeB7.decode(b), Boolean.TRUE);

        // assertTrue((Boolean) typeB0.decode(b));
        // assertTrue((Boolean) typeB1.decode(b));
        // assertFalse((Boolean) typeB2.decode(b));
        // assertFalse((Boolean) typeB3.decode(b));
        // assertFalse((Boolean) typeB4.decode(b));
        // assertTrue((Boolean) typeB5.decode(b));
        // assertFalse((Boolean) typeB6.decode(b));
        // assertTrue((Boolean) typeB7.decode(b));

        b = new byte[] { (byte) 0x00 };

        assertEquals(typeB0.decode(b), Boolean.FALSE);
        assertEquals(typeB1.decode(b), Boolean.FALSE);
        assertEquals(typeB2.decode(b), Boolean.FALSE);
        assertEquals(typeB3.decode(b), Boolean.FALSE);
        assertEquals(typeB4.decode(b), Boolean.FALSE);
        assertEquals(typeB5.decode(b), Boolean.FALSE);
        assertEquals(typeB6.decode(b), Boolean.FALSE);
        assertEquals(typeB7.decode(b), Boolean.FALSE);

        // assertFalse((Boolean) typeB0.decode(b));
        // assertFalse((Boolean) typeB1.decode(b));
        // assertFalse((Boolean) typeB2.decode(b));
        // assertFalse((Boolean) typeB3.decode(b));
        // assertFalse((Boolean) typeB4.decode(b));
        // assertFalse((Boolean) typeB5.decode(b));
        // assertFalse((Boolean) typeB6.decode(b));
        // assertFalse((Boolean) typeB7.decode(b));

        b = new byte[] { (byte) 0xFF };

        assertEquals(typeB0.decode(b), Boolean.TRUE);
        assertEquals(typeB1.decode(b), Boolean.TRUE);
        assertEquals(typeB2.decode(b), Boolean.TRUE);
        assertEquals(typeB3.decode(b), Boolean.TRUE);
        assertEquals(typeB4.decode(b), Boolean.TRUE);
        assertEquals(typeB5.decode(b), Boolean.TRUE);
        assertEquals(typeB6.decode(b), Boolean.TRUE);
        assertEquals(typeB7.decode(b), Boolean.TRUE);

        // assertTrue((Boolean) typeB0.decode(b));
        // assertTrue((Boolean) typeB1.decode(b));
        // assertTrue((Boolean) typeB2.decode(b));
        // assertTrue((Boolean) typeB3.decode(b));
        // assertTrue((Boolean) typeB4.decode(b));
        // assertTrue((Boolean) typeB5.decode(b));
        // assertTrue((Boolean) typeB6.decode(b));
        // assertTrue((Boolean) typeB7.decode(b));
    }
}
