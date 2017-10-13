package de.csdev.ebus.command.datatype.std;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.command.datatypes.IEBusType;
import de.csdev.ebus.command.datatypes.std.EBusTypeBCD;

public class BCDTest {

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
        assertArrayEquals(new byte[] { (byte) 0xFF}, encode);
    }

    @Test
    public void test_BCD() throws EBusTypeException {

        IEBusType<BigDecimal> type = types.getType(EBusTypeBCD.BCD, null);

        check(type, new byte[] { (byte) 0x00 }, 0);

        check(type, new byte[] { (byte) 0x01 }, 1);

        check(type, new byte[] { (byte) 0x10 }, 10);
        
        check(type, new byte[] { (byte) 0x50 }, 50);

        check(type, new byte[] { (byte) 0x80 }, 80);
        
        check(type, new byte[] { (byte) 0x99 }, 99);
        
        checkReplaceValue(type, new byte[] { (byte) 0x3D });
        
        checkReplaceValue(type, new byte[] { (byte) 0xFF });
    }
}
