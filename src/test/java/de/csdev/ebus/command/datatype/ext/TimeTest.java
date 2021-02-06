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
    public void before() throws EBusTypeException {
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
        assertNotNull(decode);
        assertEquals(calendar, decode.getCalendar());

        // default 23:30:59

        // check encode
        calendar = new GregorianCalendar(1970, 0, 1, 23, 30, 59);
        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x59, 0x30, 0x23 }, bytes);

        // check decode
        decode = type.decode(bytes);
        assertNotNull(decode);
        assertEquals(calendar, decode.getCalendar());

        // default 23:30:59 - reversed byte order

        // check encode
        type = getType(EBusTypeTime.DEFAULT, true);
        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x23, 0x30, 0x59 }, bytes);

        // check decode
        decode = type.decode(bytes);
        assertNotNull(decode);
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
        assertNotNull(decode);
        assertEquals(calendar, decode.getCalendar());

        // default 23:30:59

        // check encode
        calendar = new GregorianCalendar(1970, 0, 1, 23, 30, 0);
        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x30, 0x23 }, bytes);

        // check decode
        decode = type.decode(bytes);
        assertNotNull(decode);
        assertEquals(calendar, decode.getCalendar());

        // default 23:30:59 - reversed byte order

        // check encode
        type = getType(EBusTypeTime.SHORT, true);
        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x23, 0x30 }, bytes);

        // check decode
        decode = type.decode(bytes);
        assertNotNull(decode);
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
        assertNotNull(decode);
        assertEquals(calendar, decode.getCalendar());

        // default 23:30:59

        // check encode
        calendar = new GregorianCalendar(1970, 0, 1, 23, 30, 59);
        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x3B, 0x1E, 0x17 }, bytes);

        // check decode
        decode = type.decode(bytes);
        assertNotNull(decode);
        assertEquals(calendar, decode.getCalendar());

        // default 23:30:59 - reversed byte order

        // check encode
        type = getType(EBusTypeTime.HEX, true);
        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x17, 0x1E, 0x3B }, bytes);

        // check decode
        decode = type.decode(bytes);
        assertNotNull(decode);
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
        assertNotNull(decode);
        assertEquals(calendar, decode.getCalendar());

        // default 23:30

        // check encode
        calendar = new GregorianCalendar(1970, 0, 1, 23, 30, 0);
        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x1E, 0x17 }, bytes);

        // check decode
        decode = type.decode(bytes);
        assertNotNull(decode);
        assertEquals(calendar, decode.getCalendar());

        // default 23:30 - reversed byte order

        // check encode
        type = getType(EBusTypeTime.HEX_SHORT, true);
        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x17, 0x1E }, bytes);

        // check decode
        decode = type.decode(bytes);
        assertNotNull(decode);
        assertEquals(calendar, decode.getCalendar());
    }

    @Test
    public void test_TimeMinutes() throws EBusTypeException {

        GregorianCalendar calendar = new GregorianCalendar(1970, 0, 1, 0, 0, 0);
        IEBusType<EBusDateTime> type = getType(EBusTypeTime.MINUTES, false);

        // default 00:00

        // check encode
        byte[] bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x00, 0x00 }, bytes);

        // check decode
        EBusDateTime decode = type.decode(bytes);
        assertNotNull(decode);
        assertEquals(calendar, decode.getCalendar());

        // default 23:30

        // check encode 1410
        calendar = new GregorianCalendar(1970, 0, 1, 23, 30, 0);
        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { (byte) 0x82, 0x05 }, bytes);

        // check decode
        decode = type.decode(bytes);
        assertNotNull(decode);
        assertEquals(calendar, decode.getCalendar());

        // default 23:30 - reversed byte order

        // check encode
        type = getType(EBusTypeTime.MINUTES, true);
        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x05, (byte) 0x82 }, bytes);

        // check decode
        decode = type.decode(bytes);
        assertNotNull(decode);
        assertEquals(calendar, decode.getCalendar());
    }

    @Test
    public void test_TimeMinutes_Multi_10() throws EBusTypeException {

        GregorianCalendar calendar = new GregorianCalendar(1970, 0, 1, 0, 0, 0);

        IEBusType<EBusDateTime> type = types.getType(EBusTypeTime.TYPE_TIME, IEBusType.VARIANT, EBusTypeTime.MINUTES,
                EBusTypeTime.MINUTE_MULTIPLIER, BigDecimal.valueOf(10));

        // default 00:00

        // check encode
        byte[] bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x00, 0x00 }, bytes);

        // check decode
        EBusDateTime decode = type.decode(bytes);
        assertNotNull(decode);
        assertEquals(calendar, decode.getCalendar());

        // default 23:30

        // check encode
        calendar = new GregorianCalendar(1970, 0, 1, 23, 30, 0);
        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { (byte) 0x8D, (byte) 0x00 }, bytes);

        // check decode
        decode = type.decode(bytes);
        assertNotNull(decode);
        assertEquals(calendar, decode.getCalendar());

        // default 23:30 - reversed byte order

        // check encode
        type = types.getType(EBusTypeTime.TYPE_TIME, IEBusType.VARIANT, EBusTypeTime.MINUTES,
                IEBusType.REVERSED_BYTE_ORDER, Boolean.TRUE, EBusTypeTime.MINUTE_MULTIPLIER, BigDecimal.valueOf(10));

        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x00, (byte) 0x8D }, bytes);

        // check decode
        decode = type.decode(bytes);
        assertNotNull(decode);
        assertEquals(calendar, decode.getCalendar());
    }

    // public void test_TimeMinutes8BitX() throws EBusTypeException {
    //
    // IEBusType<EBusDateTime> type = types.getType(EBusTypeTime.TYPE_TIME, IEBusType.VARIANT,
    // EBusTypeTime.MINUTES_SHORT, EBusTypeTime.MINUTE_MULTIPLIER, 15, "replaceValue",
    // new byte[] { (byte) 0x90 });
    //
    // EBusDateTime decode = type.decode(new byte[] { (byte) 0x90 });
    //
    // System.out.println("TimeTest.test_TimeMinutes8BitX()" + decode.getCalendar().getTime());
    //
    // }

    @Test
    public void test_TimeMinutes8Bit() throws EBusTypeException {

        GregorianCalendar calendar = new GregorianCalendar(1970, 0, 1, 0, 0, 0);
        IEBusType<EBusDateTime> type = getType(EBusTypeTime.MINUTES_SHORT, false);

        type = types.getType(EBusTypeTime.TYPE_TIME, IEBusType.VARIANT, EBusTypeTime.MINUTES_SHORT,
                EBusTypeTime.MINUTE_MULTIPLIER, BigDecimal.valueOf(15));

        // check encode
        byte[] bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x00 }, bytes);

        // check decode
        EBusDateTime decode = type.decode(bytes);
        assertNotNull(decode);
        assertEquals(calendar, decode.getCalendar());

        // default 23:30

        // check encode 1410
        calendar = new GregorianCalendar(1970, 0, 1, 23, 30, 0);
        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { (byte) 0x5E }, bytes);

        // check decode
        decode = type.decode(bytes);
        assertNotNull(decode);
        assertEquals(calendar.getTime(), decode.getCalendar().getTime());

        // default 23:30 - reversed byte order

        // check encode
        type = getType(EBusTypeTime.MINUTES, true);
        bytes = type.encode(calendar);
        assertArrayEquals(new byte[] { 0x05, (byte) 0x82 }, bytes);

        // check decode
        decode = type.decode(bytes);
        assertNotNull(decode);
        assertEquals(calendar, decode.getCalendar());
    }

}
