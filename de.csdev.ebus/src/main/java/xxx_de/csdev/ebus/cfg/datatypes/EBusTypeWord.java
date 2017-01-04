package xxx_de.csdev.ebus.cfg.datatypes;

import java.math.BigDecimal;

public class EBusTypeWord extends EBusTypeGeneric {

	public static String WORD = "word";
	public static String UINT = "uint";
	
	private static String[] supportedTypes = new String[] {WORD, UINT};

	public String[] getSupportedTypes() {
		return supportedTypes;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T decode(byte[] data, Object... args) {
		BigDecimal value = types.decode(EBusTypeInteger.INTGER, data);
		return (T) BigDecimal.valueOf((short) (value.intValue() & 0xffff));
	}
	
	public byte[] encode(Object data, Object... args) {
		BigDecimal b = (BigDecimal)data;
		return types.encode(EBusTypeInteger.INTGER, b.intValue() & 0xFFFF);
	}

}
