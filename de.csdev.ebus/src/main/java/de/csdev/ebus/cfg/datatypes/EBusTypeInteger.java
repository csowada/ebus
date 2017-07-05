package de.csdev.ebus.cfg.datatypes;

import java.math.BigDecimal;

import de.csdev.ebus.utils.NumberUtils;

public class EBusTypeInteger extends EBusTypeGenericReplaceValue {

    public static String INTGER = "int";

    private static String[] supportedTypes = new String[] { INTGER };

    public EBusTypeInteger() {
    	replaceValue = new byte[] {(byte)0x80, (byte)0x00};
    }
    
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLenght() {
        return 2;
    }

    @SuppressWarnings("unchecked")
    public <T> T decodeInt(byte[] data) {
        return (T) BigDecimal.valueOf((short) (data[1] << 8 | data[0] & 0xFF));
    }

    public byte[] encodeInt(Object data) {
        BigDecimal b = NumberUtils.toBigDecimal(data);
        return new byte[] { (byte) b.intValue(), (byte) (b.intValue() >> 8) };
    }

}
