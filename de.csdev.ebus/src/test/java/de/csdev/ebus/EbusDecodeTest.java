package de.csdev.ebus;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.csdev.ebus.utils.EBusCodecUtils;

public class EbusDecodeTest {

	@Test
	public void test_PRIMARY() {

		boolean decodeBit = EBusCodecUtils.decodeBit((byte)0x01, 1);
		assertEquals("Decode BIT failed!", decodeBit, false);
		
		byte decodeBCD = EBusCodecUtils.decodeBCD((byte) 0x50);
		assertEquals("Decode BCD failed!", decodeBCD, 50);
		
		short decodeChar = EBusCodecUtils.decodeChar((byte) 0xFA);
		assertEquals("Decode CHAR failed!", decodeChar, (byte) 0xFA);
	}
	
	@Test
	public void test_DATA1B() {
		short decodeDATA1b = EBusCodecUtils.decodeDATA1b((byte)0x00);
		assertEquals("Decode DATA1B failed!", decodeDATA1b, 0);
		
		decodeDATA1b = EBusCodecUtils.decodeDATA1b((byte)0x01);
		assertEquals("Decode DATA1B failed!", decodeDATA1b, 1);
		
		decodeDATA1b = EBusCodecUtils.decodeDATA1b((byte)0x7F);
		assertEquals("Decode DATA1B failed!", decodeDATA1b, 127);
		
		decodeDATA1b = EBusCodecUtils.decodeDATA1b((byte)0x80);
		assertEquals("Decode DATA1B failed!", decodeDATA1b, -128);
		
		decodeDATA1b = EBusCodecUtils.decodeDATA1b((byte)0x81);
		assertEquals("Decode DATA1B failed!", decodeDATA1b, -127);
	}
	
	@Test
	public void test_DATA1C() {
		float decodeDATA1c = EBusCodecUtils.decodeDATA1c((byte)0x00);
		assertEquals("Decode DATA1B failed!", decodeDATA1c, 0f, 0.1f);
		
		decodeDATA1c = EBusCodecUtils.decodeDATA1c((byte)0x64);
		assertEquals("Decode DATA1B failed!", decodeDATA1c, 50f, 0.1f);
		
		decodeDATA1c = EBusCodecUtils.decodeDATA1c((byte)0xC8);
		assertEquals("Decode DATA1B failed!", decodeDATA1c, 100f, 0.1f);
	}
	
	@Test
	public void test_DATA2B() {
		float value = EBusCodecUtils.decodeDATA2b(new byte[]{0x00, 0x00});
		assertEquals("Decode DATA1B failed!", value, 0f, 0.1f);
		
		value = EBusCodecUtils.decodeDATA2b(new byte[]{0x00, 0x01});
		assertEquals("Decode DATA1B failed!", value, 0.00390625f, 0.1f);
		
		value = EBusCodecUtils.decodeDATA2b(new byte[]{(byte) 0xFF, (byte) 0xFF});
		assertEquals("Decode DATA1B failed!", value, -0.00390625f, 0.1f);
		
		value = EBusCodecUtils.decodeDATA2b(new byte[]{(byte) 0xFF, (byte) 0x00});
		assertEquals("Decode DATA1B failed!", value, -1f, 0.1f);
		
		value = EBusCodecUtils.decodeDATA2b(new byte[]{(byte) 0x80, (byte) 0x00});
		assertEquals("Decode DATA1B failed!", value, -128f, 0.1f);
		
		value = EBusCodecUtils.decodeDATA2b(new byte[]{(byte) 0x80, (byte) 0x01});
		assertEquals("Decode DATA1B failed!", value, -127.99f, 0.1f);
		
		value = EBusCodecUtils.decodeDATA2b(new byte[]{(byte) 0x7F, (byte) 0xFF});
		assertEquals("Decode DATA1B failed!", value, 127.99f, 0.1f);
	}
	
	@Test
	public void test_DATA2C() {
		float value = EBusCodecUtils.decodeDATA2c(new byte[]{0x00, 0x00});
		assertEquals("Decode DATA1B failed!", value, 0f, 0.1f);
		
		value = EBusCodecUtils.decodeDATA2c(new byte[]{0x00, 0x01});
		assertEquals("Decode DATA1B failed!", value, 0.0625f, 0.1f);
		
		value = EBusCodecUtils.decodeDATA2c(new byte[]{(byte) 0xFF, (byte) 0xFF});
		assertEquals("Decode DATA1B failed!", value, -0.0625f, 0.1f);
		
		value = EBusCodecUtils.decodeDATA2c(new byte[]{(byte) 0xFF, (byte) 0xF0});
		assertEquals("Decode DATA1B failed!", value, -1f, 0.1f);
		
		value = EBusCodecUtils.decodeDATA2c(new byte[]{(byte) 0x80, (byte) 0x00});
		assertEquals("Decode DATA1B failed!", value, -2048f, 0.1f);
		
		value = EBusCodecUtils.decodeDATA2c(new byte[]{(byte) 0x80, (byte) 0x01});
		assertEquals("Decode DATA1B failed!", value, -2047.9f, 0.1f);
		
		value = EBusCodecUtils.decodeDATA2c(new byte[]{(byte) 0x7F, (byte) 0xFF});
		assertEquals("Decode DATA1B failed!", value, 2047.9f, 0.1f);
	}

}
