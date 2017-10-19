/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatype.ext;

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
public class DateTest {

    private EBusTypeRegistry types;

    @Before
    public void before() {
        types = new EBusTypeRegistry();
    }

    @Test
    public void test_DateTimeStd() throws EBusTypeException {

        GregorianCalendar calendar = new GregorianCalendar(2017, 11, 24, 13, 30, 59);

        Map<String, Object> properties = new HashMap<String, Object>();

        properties.put(IEBusType.VARIANT, EBusTypeDate.DEFAULT);
        IEBusType<EBusDateTime> type = types.getType(EBusTypeDate.TYPE_DATE, properties);

        byte[] bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x24, 0x12, 0x07, 0x17 }, bytes);

    }

    @Test
    public void test_DateTimeShort() throws EBusTypeException {

        GregorianCalendar calendar = new GregorianCalendar(2017, 11, 24, 13, 30, 59);
        Map<String, Object> properties = new HashMap<String, Object>();
        // short

        properties.put(IEBusType.VARIANT, EBusTypeDate.SHORT);
        IEBusType<EBusDateTime> type = types.getType(EBusTypeDate.TYPE_DATE, properties);

        byte[] bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x24, 0x12, 0x17 }, bytes);
    }

    @Test
    public void test_DateTimeHex() throws EBusTypeException {

        GregorianCalendar calendar = new GregorianCalendar(2017, 11, 24, 13, 30, 59);
        Map<String, Object> properties = new HashMap<String, Object>();
        // short

        properties.put(IEBusType.VARIANT, EBusTypeDate.HEX);
        IEBusType<EBusDateTime> type = types.getType(EBusTypeDate.TYPE_DATE, properties);

        byte[] bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x18, 0x0c, 0x07, 0x11 }, bytes);
    }

    @Test
    public void test_DateTimeHexShort() throws EBusTypeException {

        GregorianCalendar calendar = new GregorianCalendar(2017, 11, 24, 13, 30, 59);
        Map<String, Object> properties = new HashMap<String, Object>();
        // short

        properties.put(IEBusType.VARIANT, EBusTypeDate.HEX_SHORT);
        IEBusType<EBusDateTime> type = types.getType(EBusTypeDate.TYPE_DATE, properties);

        byte[] bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x18, 0x0c, 0x11 }, bytes);
    }

    @Test
    public void test_DateTimeDays() throws EBusTypeException {

        GregorianCalendar calendar = new GregorianCalendar(1900, 0, 10, 0, 0);

        Map<String, Object> properties = new HashMap<String, Object>();
        // short

        properties.put(IEBusType.VARIANT, EBusTypeDate.DAYS);
        IEBusType<EBusDateTime> type = types.getType(EBusTypeDate.TYPE_DATE, properties);

        byte[] bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x09, 0x00 }, bytes);

        // days 06.06.2079
        calendar = new GregorianCalendar(2079, 5, 6, 0, 0);
        properties.put(IEBusType.VARIANT, EBusTypeDate.DAYS);
        type = types.getType(EBusTypeDate.TYPE_DATE, properties);

        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { (byte) 0xFF, (byte) 0xFF }, bytes);
    }

}
