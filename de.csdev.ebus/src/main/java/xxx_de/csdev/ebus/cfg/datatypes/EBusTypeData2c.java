package xxx_de.csdev.ebus.cfg.datatypes;

import java.math.BigDecimal;

public class EBusTypeData2c extends EBusTypeGeneric {

	public static String DATA2C = "data2c";
	
	private static String[] supportedTypes = new String[] {DATA2C};

	public String[] getSupportedTypes() {
		return supportedTypes;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T decode(byte[] data, Object... args) {
		short x = (short) (data[0] << 8 | data[1] & 0xFF);
		return (T) BigDecimal.valueOf(x).divide(BigDecimal.valueOf(16));
	}
	
	public byte[] encode(Object data, Object... args) {
		
		BigDecimal b = (BigDecimal)data;

		short m = (short) (b.floatValue() * 16);
		return new byte[] { (byte) (m >> 8), (byte) m };
	}

}
