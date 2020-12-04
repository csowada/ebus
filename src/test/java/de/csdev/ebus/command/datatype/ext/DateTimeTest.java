/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatype.ext;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.ext.EBusTypeDateTime;
import de.csdev.ebus.command.datatypes.ext.EBusTypeTime;
import de.csdev.ebus.utils.EBusDateTime;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class DateTimeTest {

    /** DateTime 03.09.2017 13:30:59 */
    private static final byte[] DATE_TIME_BYTES = new byte[] { 0x59, 0x30, 0x13, 0x03, 0x09, 0x07, 0x17 };

    private static final byte[] DATE_TIME_BYTES_DATE_SHORT = new byte[] { 0x59, 0x30, 0x13, 0x03, 0x09, 0x17 };

    private static final byte[] DATE_TIME_BYTES_DATE_FIRST = new byte[] { 0x03, 0x09, 0x07, 0x17, 0x59, 0x30, 0x13 };

    EBusTypeRegistry types;

    @Before
    public void before() throws EBusTypeException {
        types = new EBusTypeRegistry();
    }

    @Test
    public void test_DateTime3() throws EBusTypeException {

        GregorianCalendar calendar = new GregorianCalendar(2017, 8, 3, 13, 30, 59);

        Map<String, Object> properties = new HashMap<String, Object>();
        // properties.put(IEBusType.VARIANT, EBusTypeDateTime2.TYPE_DATETIME);
        IEBusType<EBusDateTime> type = types.getType(EBusTypeDateTime.TYPE_DATETIME, properties);

        byte[] bytes = type.encode(calendar);

        assertArrayEquals(DATE_TIME_BYTES, bytes);

    }

    @Test
    public void test_DateTime2() throws EBusTypeException {

        Map<String, Object> properties = new HashMap<String, Object>();
        // properties.put(IEBusType.VARIANT, EBusTypeDateTime2.TYPE_DATETIME);
        IEBusType<EBusDateTime> type = types.getType(EBusTypeDateTime.TYPE_DATETIME, properties);

        // decode
        EBusDateTime calendar = type.decode(DATE_TIME_BYTES);

        assertNotNull(calendar);
        assertEquals(59, calendar.getCalendar().get(Calendar.SECOND));
        assertEquals(30, calendar.getCalendar().get(Calendar.MINUTE));
        assertEquals(13, calendar.getCalendar().get(Calendar.HOUR_OF_DAY));

        assertEquals(3, calendar.getCalendar().get(Calendar.DAY_OF_MONTH));
        assertEquals(9, calendar.getCalendar().get(Calendar.MONTH) + 1);
        assertEquals(2017, calendar.getCalendar().get(Calendar.YEAR));
    }

    @Test
    public void test_DateTime_DateShort() throws EBusTypeException {

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(EBusTypeDateTime.VARIANT_DATE, EBusTypeTime.SHORT);
        IEBusType<EBusDateTime> type = types.getType(EBusTypeDateTime.TYPE_DATETIME, properties);

        // decode
        EBusDateTime calendar = type.decode(DATE_TIME_BYTES_DATE_SHORT);

        assertNotNull(calendar);
        assertEquals(59, calendar.getCalendar().get(Calendar.SECOND));
        assertEquals(30, calendar.getCalendar().get(Calendar.MINUTE));
        assertEquals(13, calendar.getCalendar().get(Calendar.HOUR_OF_DAY));

        assertEquals(3, calendar.getCalendar().get(Calendar.DAY_OF_MONTH));
        assertEquals(9, calendar.getCalendar().get(Calendar.MONTH) + 1);
        assertEquals(2017, calendar.getCalendar().get(Calendar.YEAR));
    }

    @Test
    public void test_DateTime_DateFirst() throws EBusTypeException {

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(EBusTypeDateTime.TIME_FIRST, false);
        IEBusType<EBusDateTime> type = types.getType(EBusTypeDateTime.TYPE_DATETIME, properties);

        // decode
        EBusDateTime calendar = type.decode(DATE_TIME_BYTES_DATE_FIRST);

        assertNotNull(calendar);
        assertEquals(59, calendar.getCalendar().get(Calendar.SECOND));
        assertEquals(30, calendar.getCalendar().get(Calendar.MINUTE));
        assertEquals(13, calendar.getCalendar().get(Calendar.HOUR_OF_DAY));

        assertEquals(3, calendar.getCalendar().get(Calendar.DAY_OF_MONTH));
        assertEquals(9, calendar.getCalendar().get(Calendar.MONTH) + 1);
        assertEquals(2017, calendar.getCalendar().get(Calendar.YEAR));
    }

    @Test
    public void test_DateTime_DateNull() throws EBusTypeException {

        IEBusType<EBusDateTime> type = types.getType(EBusTypeDateTime.TYPE_DATETIME);

        // decode
        EBusDateTime calendar = type.decode(EBusUtils.toByteArray("37 10 06 FF FF FF FF"));

        assertNotNull(calendar);
        assertFalse(calendar.isAnyTime());
        assertEquals(37, calendar.getCalendar().get(Calendar.SECOND));
        assertEquals(10, calendar.getCalendar().get(Calendar.MINUTE));
        assertEquals(6, calendar.getCalendar().get(Calendar.HOUR_OF_DAY));

        assertTrue(calendar.isAnyDate());
        assertEquals(1, calendar.getCalendar().get(Calendar.DAY_OF_MONTH));
        assertEquals(1, calendar.getCalendar().get(Calendar.MONTH) + 1);
        assertEquals(1970, calendar.getCalendar().get(Calendar.YEAR));
    }
}
