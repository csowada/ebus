package xxx_de.csdev.ebus.cfg.datatypes;

import java.math.BigDecimal;

public class EBusTypeByte extends EBusTypeGeneric {

	public static String UCHAR = "uchar";
	public static String BYTE = "byte";
	
	private static String[] supportedTypes = new String[] {BYTE, UCHAR};

	public String[] getSupportedTypes() {
		return supportedTypes;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T decode(byte[] data, Object... args) {
		return (T) BigDecimal.valueOf(data[0]);
	}
	
	public byte[] encode(Object data, Object... args) {
		BigDecimal b = (BigDecimal)data;
		return new byte[] { (byte) b.intValue() };
	}

}
