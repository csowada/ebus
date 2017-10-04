/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.basic;

import static org.junit.Assert.assertArrayEquals;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.ext.EBusTypeDate;
import de.csdev.ebus.utils.EBusDateTime;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EbusExtDataTypeDateTest {

    /** DateTime 03.09.2017 13:30:59 */
    private static final byte[] DATE_TIME_BYTES = new byte[] { 0x59, 0x30, 0x13, 0x03, 0x09, 0x07, 0x17 };

    private static final byte[] TIME_BYTES = new byte[] { 0x59, 0x30, 0x13 };

    EBusTypeRegistry types;

    @Before
    public void before() {
        types = new EBusTypeRegistry();
    }

    @Test
    public void test_DateTime3() throws EBusTypeException {

        GregorianCalendar calendar = new GregorianCalendar(2017, 11, 24, 13, 30, 59);

        Map<String, Object> properties = new HashMap<String, Object>();

        properties.put(IEBusType.TYPE, EBusTypeDate.STD);
        IEBusType<EBusDateTime> type = types.getType(EBusTypeDate.DATE, properties);

        byte[] bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x24, 0x12, 0x07, 0x17 }, bytes);

        // short

        properties.put(IEBusType.TYPE, EBusTypeDate.SHORT);
        type = types.getType(EBusTypeDate.DATE, properties);

        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x24, 0x12, 0x17 }, bytes);

        // hex

        properties.put(IEBusType.TYPE, EBusTypeDate.HEX);
        type = types.getType(EBusTypeDate.DATE, properties);

        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x18, 0x0c, 0x07, 0x11 }, bytes);

        // Hex short

        properties.put(IEBusType.TYPE, EBusTypeDate.HEX_SHORT);
        type = types.getType(EBusTypeDate.DATE, properties);

        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x18, 0x0c, 0x11 }, bytes);

        // days
        calendar = new GregorianCalendar(1900, 0, 10, 0, 0);
        properties.put(IEBusType.TYPE, EBusTypeDate.DAYS);
        type = types.getType(EBusTypeDate.DATE, properties);

        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x09, 0x00 }, bytes);

        // days 06.06.2079
        calendar = new GregorianCalendar(2079, 5, 5, 0, 0);
        properties.put(IEBusType.TYPE, EBusTypeDate.DAYS);
        type = types.getType(EBusTypeDate.DATE, properties);

        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { (byte) 0xFF, (byte) 0xFF }, bytes);

    }
    //
    // @Test
    // public void test_DateTime2() throws EBusTypeException {
    //
    // Map<String, Object> properties = new HashMap<String, Object>();
    // properties.put(IEBusType.TYPE, EBusTypeDateTime.DATETIME);
    // IEBusType<EBusDateTime> type = types.getType(EBusTypeDateTime.DATETIME, properties);
    //
    // // decode
    // EBusDateTime calendar = type.decode(DATE_TIME_BYTES);
    //
    // assertEquals(59, calendar.getCalendar().get(Calendar.SECOND));
    // assertEquals(30, calendar.getCalendar().get(Calendar.MINUTE));
    // assertEquals(13, calendar.getCalendar().get(Calendar.HOUR_OF_DAY));
    //
    // assertEquals(3, calendar.getCalendar().get(Calendar.DAY_OF_MONTH));
    // assertEquals(9, calendar.getCalendar().get(Calendar.MONTH) + 1);
    // assertEquals(2017, calendar.getCalendar().get(Calendar.YEAR));
    // }
    //
    // @Test
    // public void test_DateTime() throws EBusTypeException {
    //
    // Map<String, Object> properties = new HashMap<String, Object>();
    // properties.put(IEBusType.TYPE, EBusTypeDateTime.TIME);
    // IEBusType<EBusDateTime> type = types.getType(EBusTypeDateTime.DATETIME, properties);
    //
    // // decode
    // EBusDateTime calendar = type.decode(TIME_BYTES);
    //
    // assertEquals(59, calendar.getCalendar().get(Calendar.SECOND));
    // assertEquals(30, calendar.getCalendar().get(Calendar.MINUTE));
    // assertEquals(13, calendar.getCalendar().get(Calendar.HOUR_OF_DAY));
    // }
}
