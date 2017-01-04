package xxx_de.csdev.ebus.cfg.datatypes;

import java.math.BigDecimal;

public class EBusTypeChar extends EBusTypeGeneric {

	public static String UCHAR = "uchar";
	
	private static String[] supportedTypes = new String[] {UCHAR};

	public String[] getSupportedTypes() {
		return supportedTypes;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T decode(byte[] data, Object... args) {
		return (T) BigDecimal.valueOf(data[0] & 0xFF);
	}
	
	public byte[] encode(Object data, Object... args) {
		BigDecimal b = (BigDecimal)data;
		return new byte[] { (byte) ((byte) b.intValue() & 0xFF) };
	}

}
