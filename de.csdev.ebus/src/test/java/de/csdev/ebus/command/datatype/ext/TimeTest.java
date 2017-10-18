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

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.ext.EBusTypeTime;
import de.csdev.ebus.utils.EBusDateTime;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class TimeTest {

    private EBusTypeRegistry types;

    @Before
    public void before() {
        types = new EBusTypeRegistry();
    }

    private IEBusType<EBusDateTime> getType(String type, boolean reverseByteOrder) {
        Map<String, Object> properties = new HashMap<String, Object>();

        properties.put(IEBusType.VARIANT, type);

        if (reverseByteOrder) {
            properties.put(IEBusType.REVERSED_BYTE_ORDER, Boolean.TRUE);
        }

        return types.getType(EBusTypeTime.TYPE_TIME, properties);
    }

    @Test
    public void test_TimeStd() throws EBusTypeException {

        GregorianCalendar calendar = new GregorianCalendar(1970, 0, 1, 0, 0, 0);
        IEBusType<EBusDateTime> type = getType(EBusTypeTime.DEFAULT, false);

        // default 00:00:00

        // check encode
        byte[] bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x00, 0x00, 0x00 }, bytes);

        // check decode
        EBusDateTime decode = type.decode(bytes);
        assertEquals(calendar, decode.getCalendar());

        // default 23:30:59

        // check encode
        calendar = new GregorianCalendar(1970, 0, 1, 23, 30, 59);
        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x59, 0x30, 0x23 }, bytes);

        // check decode
        decode = type.decode(bytes);
        assertEquals(calendar, decode.getCalendar());

        // default 23:30:59 - reversed byte order

        // check encode
        type = getType(EBusTypeTime.DEFAULT, true);
        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x23, 0x30, 0x59 }, bytes);

        // check decode
        decode = type.decode(bytes);
        assertEquals(calendar, decode.getCalendar());
    }

    @Test
    public void test_TimeStdShort() throws EBusTypeException {

        GregorianCalendar calendar = new GregorianCalendar(1970, 0, 1, 0, 0, 0);
        IEBusType<EBusDateTime> type = getType(EBusTypeTime.SHORT, false);

        // default 00:00:00

        // check encode
        byte[] bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x00, 0x00 }, bytes);

        // check decode
        EBusDateTime decode = type.decode(bytes);
        assertEquals(calendar, decode.getCalendar());

        // default 23:30:59

        // check encode
        calendar = new GregorianCalendar(1970, 0, 1, 23, 30, 0);
        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x30, 0x23 }, bytes);

        // check decode
        decode = type.decode(bytes);
        assertEquals(calendar, decode.getCalendar());

        // default 23:30:59 - reversed byte order

        // check encode
        type = getType(EBusTypeTime.SHORT, true);
        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x23, 0x30 }, bytes);

        // check decode
        decode = type.decode(bytes);
        assertEquals(calendar, decode.getCalendar());
    }

    @Test
    public void test_TimeHex() throws EBusTypeException {

        GregorianCalendar calendar = new GregorianCalendar(1970, 0, 1, 0, 0, 0);
        IEBusType<EBusDateTime> type = getType(EBusTypeTime.HEX, false);

        // default 00:00:00

        // check encode
        byte[] bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x00, 0x00, 0x00 }, bytes);

        // check decode
        EBusDateTime decode = type.decode(bytes);
        assertEquals(calendar, decode.getCalendar());

        // default 23:30:59

        // check encode
        calendar = new GregorianCalendar(1970, 0, 1, 23, 30, 59);
        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x3B, 0x1E, 0x17 }, bytes);

        // check decode
        decode = type.decode(bytes);
        assertEquals(calendar, decode.getCalendar());

        // default 23:30:59 - reversed byte order

        // check encode
        type = getType(EBusTypeTime.HEX, true);
        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x17, 0x1E, 0x3B }, bytes);

        // check decode
        decode = type.decode(bytes);
        assertEquals(calendar, decode.getCalendar());
    }

    @Test
    public void test_TimeHexShort() throws EBusTypeException {

        GregorianCalendar calendar = new GregorianCalendar(1970, 0, 1, 0, 0, 0);
        IEBusType<EBusDateTime> type = getType(EBusTypeTime.HEX_SHORT, false);

        // default 00:00

        // check encode
        byte[] bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x00, 0x00 }, bytes);

        // check decode
        EBusDateTime decode = type.decode(bytes);
        assertEquals(calendar, decode.getCalendar());

        // default 23:30

        // check encode
        calendar = new GregorianCalendar(1970, 0, 1, 23, 30, 0);
        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x1E, 0x17 }, bytes);

        // check decode
        decode = type.decode(bytes);
        assertEquals(calendar, decode.getCalendar());

        // default 23:30 - reversed byte order

        // check encode
        type = getType(EBusTypeTime.HEX_SHORT, true);
        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x17, 0x1E }, bytes);

        // check decode
        decode = type.decode(bytes);
        assertEquals(calendar, decode.getCalendar());
    }

}
