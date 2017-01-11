package de.csdev.ebus;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import de.csdev.ebus.cfg.datatypes.EBusTypeBCD;
import de.csdev.ebus.cfg.datatypes.EBusTypeBit;
import de.csdev.ebus.cfg.datatypes.EBusTypeChar;
import de.csdev.ebus.cfg.datatypes.EBusTypeData1b;
import de.csdev.ebus.cfg.datatypes.EBusTypeData1c;
import de.csdev.ebus.cfg.datatypes.EBusTypeData2b;
import de.csdev.ebus.cfg.datatypes.EBusTypeData2c;
import de.csdev.ebus.cfg.datatypes.EBusTypes;

public class EbusDecodeTest2 {

    EBusTypes types;

    @Before
    public void before() {
        types = new EBusTypes();
    }

    @Test
    public void test_PRIMARY() {

        Boolean decodeBit = types.decode(EBusTypeBit.BIT, new byte[] { (byte) 0x01 }, 1);
        assertEquals("Decode BIT failed!", false, decodeBit);

        BigDecimal decodeBCD = types.decode(EBusTypeBCD.BCD, new byte[] { (byte) 0x50 });
        assertEquals("Decode BCD failed!", BigDecimal.valueOf(50), decodeBCD);

        BigDecimal decodeChar = types.decode(EBusTypeChar.CHAR, new byte[] { (byte) 0xFA });
        assertEquals("Decode CHAR failed!", BigDecimal.valueOf((byte) 0xFA), decodeChar);
    }

    @Test
    public void test_PRIMARY_encode() {

        byte[] encode = types.encode(EBusTypeBCD.BCD, 50, (Object[]) null);
        assertArrayEquals("Encode BCD failed!", new byte[] { 0x50 }, encode);

    }

    @Test
    public void test_decode_DATA1B() {

        BigDecimal decodeDATA1b = types.decode(EBusTypeData1b.DATA1B, new byte[] { (byte) 0x00 });
        assertEquals("Decode DATA1B failed!", 0, decodeDATA1b.intValue());

        decodeDATA1b = types.decode(EBusTypeData1b.DATA1B, new byte[] { (byte) 0x01 });
        assertEquals("Decode DATA1B failed!", 1, decodeDATA1b.intValue());

        decodeDATA1b = types.decode(EBusTypeData1b.DATA1B, new byte[] { (byte) 0x7F });
        assertEquals("Decode DATA1B failed!", 127, decodeDATA1b.intValue());

        decodeDATA1b = types.decode(EBusTypeData1b.DATA1B, new byte[] { (byte) 0x80 });
        assertEquals("Decode DATA1B failed!", -128, decodeDATA1b.intValue());

        decodeDATA1b = types.decode(EBusTypeData1b.DATA1B, new byte[] { (byte) 0x81 });
        assertEquals("Decode DATA1B failed!", -127, decodeDATA1b.intValue());
    }

    @Test
    public void test__endcode_DATA1B() {

        byte[] encode = types.encode(EBusTypeData1b.DATA1B, 0, (Object[]) null);
        assertArrayEquals("Encode DATA1B failed!", new byte[] { 0x00 }, encode);

        encode = types.encode(EBusTypeData1b.DATA1B, 1, (Object[]) null);
        assertArrayEquals("Encode DATA1B failed!", new byte[] { 0x01 }, encode);

        encode = types.encode(EBusTypeData1b.DATA1B, 127, (Object[]) null);
        assertArrayEquals("Encode DATA1B failed!", new byte[] { 0x7F }, encode);

        encode = types.encode(EBusTypeData1b.DATA1B, -128, (Object[]) null);
        assertArrayEquals("Encode DATA1B failed!", new byte[] { (byte) 0x80 }, encode);

        encode = types.encode(EBusTypeData1b.DATA1B, -127, (Object[]) null);
        assertArrayEquals("Encode DATA1B failed!", new byte[] { (byte) 0x81 }, encode);
    }

    @Test
    public void test_decode_DATA1C() {
        BigDecimal decodeDATA1c = types.decode(EBusTypeData1c.DATA1C, new byte[] { (byte) 0x00 });
        assertEquals("Decode DATA1B failed!", 0f, decodeDATA1c.floatValue(), 0.1f);

        decodeDATA1c = types.decode(EBusTypeData1c.DATA1C, new byte[] { (byte) 0x64 });
        assertEquals("Decode DATA1B failed!", 50f, decodeDATA1c.floatValue(), 0.1f);

        decodeDATA1c = types.decode(EBusTypeData1c.DATA1C, new byte[] { (byte) 0xC8 });
        assertEquals("Decode DATA1B failed!", 100f, decodeDATA1c.floatValue(), 0.1f);
    }

    @Test
    public void test_encode_DATA1C() {

        byte[] encode = types.encode(EBusTypeData1c.DATA1C, 0f, (Object[]) null);
        assertArrayEquals("Encode DATA1C failed!", new byte[] { 0x00 }, encode);

        encode = types.encode(EBusTypeData1c.DATA1C, 50f, (Object[]) null);
        assertArrayEquals("Encode DATA1C failed!", new byte[] { 0x64 }, encode);

        encode = types.encode(EBusTypeData1c.DATA1C, 100f, (Object[]) null);
        assertArrayEquals("Encode DATA1C failed!", new byte[] { (byte) 0xC8 }, encode);
    }

    @Test
    public void test_decode_DATA2B() {
        BigDecimal value = types.decode(EBusTypeData2b.DATA2B, new byte[] { 0x00, 0x00 });
        assertEquals("Decode DATA2B failed!", 0f, value.floatValue(), 0.1f);

        value = types.decode(EBusTypeData2b.DATA2B, new byte[] { 0x01, 0x00 });
        assertEquals("Decode DATA2B failed!", 0.00390625f, value.floatValue(), 0.1f);

        value = types.decode(EBusTypeData2b.DATA2B, new byte[] { (byte) 0xFF, (byte) 0xFF });
        assertEquals("Decode DATA2B failed!", -0.00390625f, value.floatValue(), 0.1f);

        value = types.decode(EBusTypeData2b.DATA2B, new byte[] { (byte) 0x00, (byte) 0xFF });
        assertEquals("Decode DATA2B failed!", -1f, value.floatValue(), 0.1f);

        value = types.decode(EBusTypeData2b.DATA2B, new byte[] { (byte) 0x00, (byte) 0x80 });
        assertEquals("Decode DATA2B failed!", -128f, value.floatValue(), 0.1f);

        value = types.decode(EBusTypeData2b.DATA2B, new byte[] { (byte) 0x01, (byte) 0x80 });
        assertEquals("Decode DATA2B failed!", -127.99f, value.floatValue(), 0.1f);

        value = types.decode(EBusTypeData2b.DATA2B, new byte[] { (byte) 0xFF, (byte) 0xF7F });
        assertEquals("Decode DATA2B failed!", 127.99f, value.floatValue(), 0.1f);
    }

    @Test
    public void test_encode_DATA2B() {
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
    public void test_decode_DATA2C() {
        BigDecimal value = types.decode(EBusTypeData2c.DATA2C, new byte[] { 0x00, 0x00 });
        assertEquals("Decode DATA2C failed!", 0f, value.floatValue(), 0.1f);

        value = types.decode(EBusTypeData2c.DATA2C, new byte[] { 0x00, 0x01 });
        assertEquals("Decode DATA2C failed!", 0.0625f, value.floatValue(), 0.1f);

        value = types.decode(EBusTypeData2c.DATA2C, new byte[] { (byte) 0xFF, (byte) 0xFF });
        assertEquals("Decode DATA2C failed!", -0.0625f, value.floatValue(), 0.1f);

        value = types.decode(EBusTypeData2c.DATA2C, new byte[] { (byte) 0xFF, (byte) 0xF0 });
        assertEquals("Decode DATA2C failed!", -1f, value.floatValue(), 0.1f);

        value = types.decode(EBusTypeData2c.DATA2C, new byte[] { (byte) 0x80, (byte) 0x00 });
        assertEquals("Decode DATA2C failed!", -2048f, value.floatValue(), 0.1f);

        value = types.decode(EBusTypeData2c.DATA2C, new byte[] { (byte) 0x80, (byte) 0x01 });
        assertEquals("Decode DATA2C failed!", -2047.9f, value.floatValue(), 0.1f);

        value = types.decode(EBusTypeData2c.DATA2C, new byte[] { (byte) 0x7F, (byte) 0xFF });
        assertEquals("Decode DATA2C failed!", 2047.9f, value.floatValue(), 0.1f);
    }

    @Test
    public void test_encode_DATA2C() {
        byte[] encode = types.encode(EBusTypeData2c.DATA2C, 0f, (Object[]) null);
        assertArrayEquals("Encode DATA2C failed!", new byte[] { 0x00, 0x00 }, encode);

        encode = types.encode(EBusTypeData2c.DATA2C, 0.0625f, (Object[]) null);
        assertArrayEquals("Encode DATA2C failed!", new byte[] { 0x00, 0x01 }, encode);

        encode = types.encode(EBusTypeData2c.DATA2C, -0.0625f, (Object[]) null);
        assertArrayEquals("Encode DATA2C failed!", new byte[] { (byte) 0xFF, (byte) 0xFF }, encode);

        encode = types.encode(EBusTypeData2c.DATA2C, -1f, (Object[]) null);
        assertArrayEquals("Encode DATA2C failed!", new byte[] { (byte) 0xFF, (byte) 0xF0 }, encode);

        encode = types.encode(EBusTypeData2c.DATA2C, -2048f, (Object[]) null);
        assertArrayEquals("Encode DATA2C failed!", new byte[] { (byte) 0x80, (byte) 0x00 }, encode);

        encode = types.encode(EBusTypeData2c.DATA2C, -2047.99f, (Object[]) null);
        assertArrayEquals("Encode DATA2C failed!", new byte[] { (byte) 0x80, (byte) 0x01 }, encode);

        encode = types.encode(EBusTypeData2c.DATA2C, 2047.99f, (Object[]) null);
        assertArrayEquals("Encode DATA2C failed!", new byte[] { (byte) 0x7F, (byte) 0xFF }, encode);
    }

}
