package de.csdev.ebus.cfg.datatypes;

import java.math.BigDecimal;

import de.csdev.ebus.utils.NumberUtils;

public class EBusTypeChar extends EBusTypeGenericReplaceValue {

    public static String CHAR = "char";

    private static String[] supportedTypes = new String[] { CHAR };

    public EBusTypeChar() {
    	replaceValue = new byte[] {(byte)0xFF};
    }
    
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @SuppressWarnings("unchecked")
    public <T> T decodeInt(byte[] data) {
        return (T) BigDecimal.valueOf(data[0]);
    }

    public byte[] encodeInt(Object data) {
        BigDecimal b = NumberUtils.toBigDecimal(data == null ? 0 : data);
        return new byte[] { (byte) ((byte) b.intValue() & 0xFF) };
    }

}
