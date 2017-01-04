package xxx_de.csdev.ebus.cfg.datatypes;

import java.math.BigDecimal;

public class EBusTypeInteger extends EBusTypeGeneric {

	public static String INTGER = "int";
	
	private static String[] supportedTypes = new String[] {INTGER};

	public String[] getSupportedTypes() {
		return supportedTypes;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T decode(byte[] data, Object... args) {
		return (T) BigDecimal.valueOf((short) (data[0] << 8 | data[1] & 0xFF));
	}
	
	public byte[] encode(Object data, Object... args) {
		BigDecimal b = (BigDecimal)data;
		return new byte[] { (byte) (b.intValue() >> 8), (byte) b.intValue() };
	}

}
