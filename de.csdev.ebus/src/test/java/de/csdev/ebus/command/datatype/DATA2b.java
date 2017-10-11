package de.csdev.ebus.command.datatype;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.std.EBusTypeData2b;

public class DATA2b {

    EBusTypeRegistry types;

    @Before
    public void before() {
        types = new EBusTypeRegistry();
    }

    private void check(IEBusType<?> type, byte[] bs, float result) throws EBusTypeException {
        BigDecimal value = (BigDecimal) type.decode(bs);
        assertEquals(result, value.floatValue(), 0.1f);

        byte[] encode = type.encode(value.floatValue());
        assertArrayEquals(bs, encode);
    }

    private void checkReplaceValue(IEBusType<?> type, byte[] bs) throws EBusTypeException {
        BigDecimal value = (BigDecimal) type.decode(bs);
        assertNull(value);

        byte[] encode = type.encode(value);
        assertArrayEquals(bs, encode);
    }

    @Test
    public void test_decode_DATA2B_Mhhh() throws EBusTypeException {

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(IEBusType.REVERSED_BYTE_ORDER, Boolean.TRUE);
        IEBusType<BigDecimal> type = types.getType(EBusTypeData2b.DATA2B, properties);

        byte[] encode = type.encode(0.00390625f);
        assertArrayEquals("Encode DATA2B failed!", new byte[] { 0x00, 0x01 }, encode);

        BigDecimal value = type.decode(new byte[] { 0x00, 0x01 });
        assertEquals("Decode DATA2B failed!", 0.00390625f, value.floatValue(), 0.1f);

    }

    @Test
    public void test_decode_DATA2B_New() throws EBusTypeException {

        // Map<String, Object> properties = new HashMap<String, Object>();
        // properties.put(IEBusType.REVERSED_BYTE_ORDER, Boolean.TRUE);
        IEBusType<BigDecimal> type = types.getType(EBusTypeData2b.DATA2B, null);

        check(type, new byte[] { 0x00, 0x00 }, 0f);

        check(type, new byte[] { 0x01, 0x00 }, 0.00390625f);

        check(type, new byte[] { (byte) 0xFF, (byte) 0xFF }, -0.00390625f);

        check(type, new byte[] { (byte) 0x00, (byte) 0xFF }, -1f);

        checkReplaceValue(type, new byte[] { (byte) 0x00, (byte) 0x80 });

        check(type, new byte[] { (byte) 0x01, (byte) 0x80 }, -127.99f);

        check(type, new byte[] { (byte) 0xFF, (byte) 0x7F }, 127.99f);
    }

    @Test
    public void test_decode_DATA2B_New_Reverse() throws EBusTypeException {

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(IEBusType.REVERSED_BYTE_ORDER, Boolean.TRUE);
        IEBusType<BigDecimal> type = types.getType(EBusTypeData2b.DATA2B, properties);

        check(type, new byte[] { 0x00, 0x00 }, 0f);

        check(type, new byte[] { 0x00, 0x01 }, 0.00390625f);

        check(type, new byte[] { (byte) 0xFF, (byte) 0xFF }, -0.00390625f);

        check(type, new byte[] { (byte) 0xFF, (byte) 0x00 }, -1f);

        checkReplaceValue(type, new byte[] { (byte) 0x80, (byte) 0x00 });

        check(type, new byte[] { (byte) 0x80, (byte) 0x01 }, -127.99f);

        check(type, new byte[] { (byte) 0x7F, (byte) 0xFF }, 127.99f);
    }

    @Test
    public void test_decode_DATA2B() throws EBusTypeException {
        BigDecimal value = types.decode(EBusTypeData2b.DATA2B, new byte[] { 0x00, 0x00 });
        assertEquals("Decode DATA2B failed!", 0f, value.floatValue(), 0.1f);

        value = types.decode(EBusTypeData2b.DATA2B, new byte[] { 0x01, 0x00 });
        assertEquals("Decode DATA2B failed!", 0.00390625f, value.floatValue(), 0.1f);

        value = types.decode(EBusTypeData2b.DATA2B, new byte[] { (byte) 0xFF, (byte) 0xFF });
        assertEquals("Decode DATA2B failed!", -0.00390625f, value.floatValue(), 0.1f);

        value = types.decode(EBusTypeData2b.DATA2B, new byte[] { (byte) 0x00, (byte) 0xFF });
        assertEquals("Decode DATA2B failed!", -1f, value.floatValue(), 0.1f);

        value = types.decode(EBusTypeData2b.DATA2B, new byte[] { (byte) 0x00, (byte) 0x80 });
        assertNull("Decode DATA2B failed!", value);

        value = types.decode(EBusTypeData2b.DATA2B, new byte[] { (byte) 0x01, (byte) 0x80 });
        assertEquals("Decode DATA2B failed!", -127.99f, value.floatValue(), 0.1f);

        value = types.decode(EBusTypeData2b.DATA2B, new byte[] { (byte) 0xFF, (byte) 0xF7F });
        assertEquals("Decode DATA2B failed!", 127.99f, value.floatValue(), 0.1f);
    }

    @Test
    public void test_decode_DATA2B_Reverse() throws EBusTypeException {

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(IEBusType.REVERSED_BYTE_ORDER, Boolean.TRUE);
        IEBusType<BigDecimal> type = types.getType(EBusTypeData2b.DATA2B, properties);

        BigDecimal value = type.decode(new byte[] { 0x00, 0x00 });
        assertEquals("Decode DATA2B failed!", 0f, value.floatValue(), 0.1f);

        value = type.decode(new byte[] { 0x00, 0x01 });
        assertEquals("Decode DATA2B failed!", 0.00390625f, value.floatValue(), 0.1f);

        value = type.decode(new byte[] { (byte) 0xFF, (byte) 0xFF });
        assertEquals("Decode DATA2B failed!", -0.00390625f, value.floatValue(), 0.1f);

        value = type.decode(new byte[] { (byte) 0xFF, (byte) 0x00 });
        assertEquals("Decode DATA2B failed!", -1f, value.floatValue(), 0.1f);

        value = type.decode(new byte[] { (byte) 0x80, (byte) 0x00 });
        assertNull("Decode DATA2B failed!", value);

        value = type.decode(new byte[] { (byte) 0x80, (byte) 0x01 });
        assertEquals("Decode DATA2B failed!", -127.99f, value.floatValue(), 0.1f);

        value = type.decode(new byte[] { (byte) 0x7F, (byte) 0xFF });
        assertEquals("Decode DATA2B failed!", 127.99f, value.floatValue(), 0.1f);
    }

    @Test
    public void test_encode_DATA2B() throws EBusTypeException {
        byte[] encode = types.encode(EBusTypeData2b.DATA2B, 0f, (Object[]) null);
        assertArrayEquals("Encode DATA2B failed!", new byte[] { 0x00, 0x00 }, encode);

        encode = types.encode(EBusTypeData2b.DATA2B, 0.00390625f, (Object[]) null);
        assertArrayEquals("Encode DATA2B failed!", new byte[] { 0x01, 0x00 }, encode);

        encode = types.encode(EBusTypeData2b.DATA2B, -0.00390625f, (Object[]) null);
        assertArrayEquals("Encode DATA2B failed!", new byte[] { (byte) 0xFF, (byte) 0xFF }, encode);

        encode = types.encode(EBusTypeData2b.DATA2B, -1f, (Object[]) null);
        assertArrayEquals("Encode DATA2B failed!", new byte[] { (byte) 0x00, (byte) 0xFF }, encode);

        encode = types.encode(EBusTypeData2b.DATA2B, -128f, (Object[]) null);
        assertArrayEquals("Encode DATA2B failed!", new byte[] { (byte) 0x00, (byte) 0x80 }, encode);

        encode = types.encode(EBusTypeData2b.DATA2B, -127.999f, (Object[]) null);
        assertArrayEquals("Encode DATA2B failed!", new byte[] { (byte) 0x01, (byte) 0x80 }, encode);

        encode = types.encode(EBusTypeData2b.DATA2B, 127.999f, (Object[]) null);
        assertArrayEquals("Encode DATA2B failed!", new byte[] { (byte) 0xFF, (byte) 0x7F }, encode);
    }

    @Test
    public void test_encode_DATA2B_Reverse() throws EBusTypeException {

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(IEBusType.REVERSED_BYTE_ORDER, Boolean.TRUE);
        IEBusType<BigDecimal> type = types.getType(EBusTypeData2b.DATA2B, properties);

        byte[] encode = type.encode(0f);
        assertArrayEquals("Encode DATA2B failed!", new byte[] { 0x00, 0x00 }, encode);

        encode = type.encode(0.00390625f);
        assertArrayEquals("Encode DATA2B failed!", new byte[] { 0x00, 0x01 }, encode);

        encode = type.encode(-0.00390625f);
        assertArrayEquals("Encode DATA2B failed!", new byte[] { (byte) 0xFF, (byte) 0xFF }, encode);

        encode = type.encode(-1f);
        assertArrayEquals("Encode DATA2B failed!", new byte[] { (byte) 0xFF, (byte) 0x00 }, encode);

        encode = type.encode(-128f);
        assertArrayEquals("Encode DATA2B failed!", new byte[] { (byte) 0x80, (byte) 0x00 }, encode);

        encode = type.encode(-127.999f);
        assertArrayEquals("Encode DATA2B failed!", new byte[] { (byte) 0x80, (byte) 0x01 }, encode);

        encode = type.encode(127.999f);
        assertArrayEquals("Encode DATA2B failed!", new byte[] { (byte) 0x7F, (byte) 0xFF }, encode);
    }

}
