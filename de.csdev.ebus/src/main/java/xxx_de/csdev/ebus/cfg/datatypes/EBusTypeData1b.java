package xxx_de.csdev.ebus.cfg.datatypes;

import java.math.BigDecimal;

public class EBusTypeData1b extends EBusTypeGeneric {

	public static String DATA1B = "data1b";
	
	private static String[] supportedTypes = new String[] {DATA1B};

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

	@Override
	public void setTypesParent(EBusTypes types) {
		this.types = types;
	}

}
