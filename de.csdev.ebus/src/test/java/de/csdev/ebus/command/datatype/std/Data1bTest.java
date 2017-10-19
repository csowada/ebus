package de.csdev.ebus.command.datatype.std;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.std.EBusTypeData1b;

public class Data1bTest {

    EBusTypeRegistry types;

    @Before
    public void before() {
        types = new EBusTypeRegistry();
    }

    private void check(IEBusType<?> type, byte[] bs, int result) throws EBusTypeException {
        BigDecimal value = (BigDecimal) type.decode(bs);
        assertEquals(result, value.intValue());

        byte[] encode = type.encode(value);
        assertArrayEquals(bs, encode);
    }

    private void checkReplaceValue(IEBusType<?> type, byte[] bs) throws EBusTypeException {
        BigDecimal value = (BigDecimal) type.decode(bs);
        assertNull(value);

        byte[] encode = type.encode(value);
        assertArrayEquals(bs, encode);
    }

    @Test
    public void test_Data1b() throws EBusTypeException {

        IEBusType<BigDecimal> type = types.getType(EBusTypeData1b.TYPE_DATA1B);

        check(type, new byte[] { (byte) 0x00 }, 0);

        check(type, new byte[] { (byte) 0x01 }, 1);

        check(type, new byte[] { (byte) 0x7F }, 127);

        checkReplaceValue(type, new byte[] { (byte) 0x80 });

        check(type, new byte[] { (byte) 0x81 }, -127);
    }

    @Test
    public void test_Data1b_Reverse() throws EBusTypeException {

        IEBusType<BigDecimal> type = types.getType(EBusTypeData1b.TYPE_DATA1B, IEBusType.REVERSED_BYTE_ORDER,
                Boolean.TRUE);

        check(type, new byte[] { (byte) 0x00 }, 0);

        check(type, new byte[] { (byte) 0x01 }, 1);

        check(type, new byte[] { (byte) 0x7F }, 127);

        checkReplaceValue(type, new byte[] { (byte) 0x80 });

        check(type, new byte[] { (byte) 0x81 }, -127);
    }
}
