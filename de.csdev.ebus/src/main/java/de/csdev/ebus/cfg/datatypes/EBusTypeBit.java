package de.csdev.ebus.cfg.datatypes;

public class EBusTypeBit extends EBusTypeGeneric {

	public static String BIT = "bit";
	
	private static String[] supportedTypes = new String[] {BIT};

	public String[] getSupportedTypes() {
		return supportedTypes;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T decode(byte[] data, Object... args) {
		int bit = (Integer) args[0];
		
		Boolean isSet = ((Byte) data[0] >> bit & 0x1) == 1;
		return (T) isSet;
	}
	
	public byte[] encode(Object data, Object... args) {
		
		throw new RuntimeException("Not implemented yet!");
//		BigDecimal.valueOf(0).
//
//		BigDecimal b = (BigDecimal)data;
//		return new byte[] { (byte) b.intValue() };
	}

}
