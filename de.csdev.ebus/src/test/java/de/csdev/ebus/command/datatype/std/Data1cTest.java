package de.csdev.ebus.command.datatype.std;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.std.EBusTypeData1c;

public class Data1cTest {

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
    public void test_Data1c() throws EBusTypeException {

        IEBusType<BigDecimal> type = types.getType(EBusTypeData1c.TYPE_DATA1C);

        check(type, new byte[] { (byte) 0x00 }, 0f);

        check(type, new byte[] { (byte) 0x64 }, 50f);

        check(type, new byte[] { (byte) 0xC8 }, 100f);

        checkReplaceValue(type, new byte[] { (byte) 0xFF });
    }

    @Test
    public void test_Data1c_Reverse() throws EBusTypeException {

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(IEBusType.REVERSED_BYTE_ORDER, Boolean.TRUE);
        IEBusType<BigDecimal> type = types.getType(EBusTypeData1c.TYPE_DATA1C, properties);

        check(type, new byte[] { (byte) 0x00 }, 0f);

        check(type, new byte[] { (byte) 0x64 }, 50f);

        check(type, new byte[] { (byte) 0xC8 }, 100f);

        checkReplaceValue(type, new byte[] { (byte) 0xFF });
    }
}
