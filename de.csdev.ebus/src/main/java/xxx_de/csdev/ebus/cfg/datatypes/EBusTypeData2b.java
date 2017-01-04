package xxx_de.csdev.ebus.cfg.datatypes;

import java.math.BigDecimal;

public class EBusTypeData2b extends EBusTypeGeneric {

	public static String DATA2B = "data2b";
	
	private static String[] supportedTypes = new String[] {DATA2B};

	public String[] getSupportedTypes() {
		return supportedTypes;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T decode(byte[] data, Object... args) {
		BigDecimal intValue = types.decode(EBusTypeInteger.INTGER, data);
		return (T) intValue.divide(BigDecimal.valueOf(256));
	}
	
	public byte[] encode(Object data, Object... args) {
		
		BigDecimal b = (BigDecimal)data;

		short m = (short) (b.floatValue() * 256);
		return  types.encode(EBusTypeInteger.INTGER, m);
		//return new byte[] { (byte) (m >> 8), (byte) m };
	}

}
