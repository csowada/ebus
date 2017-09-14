/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.basic;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.cfg.datatypes.EBusTypeException;
import de.csdev.ebus.cfg.datatypes.EBusTypes;
import de.csdev.ebus.cfg.datatypes.IEBusType;
import de.csdev.ebus.cfg.datatypes.ext.EBusTypeDateTime;
import de.csdev.ebus.cfg.datatypes.ext.EBusTypeMultiWord;
import de.csdev.ebus.utils.EBusDateTime;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EbusExtDataTypeTest {

    /** DateTime 03.09.2017 13:30:59 */
    private static final byte[] DATE_TIME_BYTES = new byte[] { 0x59, 0x30, 0x13, 0x03, 0x09, 0x07, 0x17 };

    private static final byte[] TIME_BYTES = new byte[] { 0x59, 0x30, 0x13 };

    EBusTypes types;

    @Before
    public void before() {
        types = new EBusTypes();
    }

    @Test
    public void test_DateTime3() throws EBusTypeException {

        GregorianCalendar calendar = new GregorianCalendar(2017, 8, 3, 13, 30, 59);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(IEBusType.TYPE, EBusTypeDateTime.DATETIME);
        IEBusType<EBusDateTime> type = types.getType(EBusTypeDateTime.DATETIME, properties);

        byte[] bytes = type.encode(calendar);

        assertArrayEquals(DATE_TIME_BYTES, bytes);

    }

    @Test
    public void test_DateTime2() throws EBusTypeException {

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(IEBusType.TYPE, EBusTypeDateTime.DATETIME);
        IEBusType<EBusDateTime> type = types.getType(EBusTypeDateTime.DATETIME, properties);

        // decode
        EBusDateTime calendar = type.decode(DATE_TIME_BYTES);

        assertEquals(59, calendar.getCalendar().get(Calendar.SECOND));
        assertEquals(30, calendar.getCalendar().get(Calendar.MINUTE));
        assertEquals(13, calendar.getCalendar().get(Calendar.HOUR_OF_DAY));

        assertEquals(3, calendar.getCalendar().get(Calendar.DAY_OF_MONTH));
        assertEquals(9, calendar.getCalendar().get(Calendar.MONTH) + 1);
        assertEquals(2017, calendar.getCalendar().get(Calendar.YEAR));
    }

    @Test
    public void test_DateTime() throws EBusTypeException {

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(IEBusType.TYPE, EBusTypeDateTime.TIME);
        IEBusType<EBusDateTime> type = types.getType(EBusTypeDateTime.DATETIME, properties);

        // decode
        EBusDateTime calendar = type.decode(TIME_BYTES);

        assertEquals(59, calendar.getCalendar().get(Calendar.SECOND));
        assertEquals(30, calendar.getCalendar().get(Calendar.MINUTE));
        assertEquals(13, calendar.getCalendar().get(Calendar.HOUR_OF_DAY));
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

}
