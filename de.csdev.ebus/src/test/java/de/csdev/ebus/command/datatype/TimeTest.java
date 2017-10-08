/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatype;

import static org.junit.Assert.assertArrayEquals;

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

        properties.put(IEBusType.TYPE, type);

        if (reverseByteOrder) {
            properties.put(IEBusType.REVERSED_BYTE_ORDER, Boolean.TRUE);
        }

        return types.getType(EBusTypeTime.TIME, properties);
    }

    @Test
    public void test_TimeStd() throws EBusTypeException {

        GregorianCalendar calendar = new GregorianCalendar(1970, 0, 1, 0, 0, 0);
        IEBusType<EBusDateTime> type = getType(EBusTypeTime.DEFAULT, false);

        byte[] bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x00, 0x00, 0x00 }, bytes);

        calendar = new GregorianCalendar(1970, 0, 1, 23, 30, 59);
        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x59, 0x30, 0x23 }, bytes);

        type = getType(EBusTypeTime.DEFAULT, true);
        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x23, 0x30, 0x59 }, bytes);
    }
    //
    // @Test
    // public void test_DateTimeShort() throws EBusTypeException {
    //
    // GregorianCalendar calendar = new GregorianCalendar(2017, 11, 24, 13, 30, 59);
    // Map<String, Object> properties = new HashMap<String, Object>();
    // // short
    //
    // properties.put(IEBusType.TYPE, EBusTypeDate.SHORT);
    // IEBusType<EBusDateTime> type = types.getType(EBusTypeDate.DATE, properties);
    //
    // byte[] bytes = type.encode(calendar);
    // assertArrayEquals(new byte[] { 0x24, 0x12, 0x17 }, bytes);
    // }
    //
    // @Test
    // public void test_DateTimeHex() throws EBusTypeException {
    //
    // GregorianCalendar calendar = new GregorianCalendar(2017, 11, 24, 13, 30, 59);
    // Map<String, Object> properties = new HashMap<String, Object>();
    // // short
    //
    // properties.put(IEBusType.TYPE, EBusTypeDate.HEX);
    // IEBusType<EBusDateTime> type = types.getType(EBusTypeDate.DATE, properties);
    //
    // byte[] bytes = type.encode(calendar);
    // assertArrayEquals(new byte[] { 0x18, 0x0c, 0x07, 0x11 }, bytes);
    // }
    //
    // @Test
    // public void test_DateTimeHexShort() throws EBusTypeException {
    //
    // GregorianCalendar calendar = new GregorianCalendar(2017, 11, 24, 13, 30, 59);
    // Map<String, Object> properties = new HashMap<String, Object>();
    // // short
    //
    // properties.put(IEBusType.TYPE, EBusTypeDate.HEX_SHORT);
    // IEBusType<EBusDateTime> type = types.getType(EBusTypeDate.DATE, properties);
    //
    // byte[] bytes = type.encode(calendar);
    // assertArrayEquals(new byte[] { 0x18, 0x0c, 0x11 }, bytes);
    // }
    //
    // @Test
    // public void test_DateTimeDays() throws EBusTypeException {
    //
    // GregorianCalendar calendar = new GregorianCalendar(1900, 0, 10, 0, 0);
    //
    // Map<String, Object> properties = new HashMap<String, Object>();
    // // short
    //
    // properties.put(IEBusType.TYPE, EBusTypeDate.DAYS);
    // IEBusType<EBusDateTime> type = types.getType(EBusTypeDate.DATE, properties);
    //
    // byte[] bytes = type.encode(calendar);
    // assertArrayEquals(new byte[] { 0x09, 0x00 }, bytes);
    //
    // // days 06.06.2079
    // calendar = new GregorianCalendar(2079, 5, 6, 0, 0);
    // properties.put(IEBusType.TYPE, EBusTypeDate.DAYS);
    // type = types.getType(EBusTypeDate.DATE, properties);
    //
    // bytes = type.encode(calendar);
    // assertArrayEquals(new byte[] { (byte) 0xFF, (byte) 0xFF }, bytes);
    // }

}
