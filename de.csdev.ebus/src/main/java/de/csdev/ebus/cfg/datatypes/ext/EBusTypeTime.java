package de.csdev.ebus.cfg.datatypes.ext;

import java.math.BigDecimal;

import de.csdev.ebus.cfg.datatypes.EBusTypeBCD;
import de.csdev.ebus.cfg.datatypes.EBusTypeGeneric;
import de.csdev.ebus.utils.NumberUtils;

public class EBusTypeTime extends EBusTypeGeneric {

    public static String TIME = "time";

    private static String[] supportedTypes = new String[] { TIME };

    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
	public int getTypeLenght() {
		return 3;
	}

	@SuppressWarnings("unchecked")
    public <T> T decode(byte[] data) {
    	
    	BigDecimal sec = NumberUtils.toBigDecimal(types.decode(EBusTypeBCD.BCD, new byte[]{data[2]}));
    	BigDecimal min = NumberUtils.toBigDecimal(types.decode(EBusTypeBCD.BCD, new byte[]{data[1]}));
    	BigDecimal hr = NumberUtils.toBigDecimal(types.decode(EBusTypeBCD.BCD, new byte[]{data[0]}));
    	
    	return (T) (hr.toString() + ":" + min.toString() + ":" + sec.toString());
    }

    public byte[] encode(Object data) {
//        BigDecimal b = NumberUtils.toBigDecimal(data);
//
//        if (b == null) {
//            return new byte[] { 0x00 };
//        }

        return new byte[this.getTypeLenght()];
    }

}
