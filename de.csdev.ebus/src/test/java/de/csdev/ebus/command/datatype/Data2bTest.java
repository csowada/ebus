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
import de.csdev.ebus.command.datatypes.std.EBusTypeData2c;

public class Data2bTest {

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
    public void test_DATA2C() throws EBusTypeException {

        IEBusType<BigDecimal> type = types.getType(EBusTypeData2c.DATA2C, null);

        check(type, new byte[] { 0x00, 0x00 }, 0f);

        check(type, new byte[] { 0x01, 0x00 }, 0.0625f);

        check(type, new byte[] { (byte) 0xFF, (byte) 0xFF }, -0.0625f);

        check(type, new byte[] { (byte) 0xF0, (byte) 0xFF }, -1f);

        checkReplaceValue(type, new byte[] { (byte) 0x00, (byte) 0x80 });

        check(type, new byte[] { (byte) 0x01, (byte) 0x80 }, -2047.9f);

        check(type, new byte[] { (byte) 0xFF, (byte) 0x7F }, 2047.9f);
    }

    @Test
    public void test_DATA2C_Reverse() throws EBusTypeException {

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(IEBusType.REVERSED_BYTE_ORDER, Boolean.TRUE);
        IEBusType<BigDecimal> type = types.getType(EBusTypeData2c.DATA2C, properties);

        check(type, new byte[] { 0x00, 0x00 }, 0f);

        check(type, new byte[] { 0x00, 0x01 }, 0.0625f);

        check(type, new byte[] { (byte) 0xFF, (byte) 0xFF }, -0.0625f);

        check(type, new byte[] { (byte) 0xFF, (byte) 0xF0 }, -1f);

        checkReplaceValue(type, new byte[] { (byte) 0x80, (byte) 0x00 });

        check(type, new byte[] { (byte) 0x80, (byte) 0x01 }, -2047.9f);

        check(type, new byte[] { (byte) 0x7F, (byte) 0xFF }, 2047.9f);
    }
}