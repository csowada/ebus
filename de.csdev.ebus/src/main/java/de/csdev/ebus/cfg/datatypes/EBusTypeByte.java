package de.csdev.ebus.cfg.datatypes;

import java.math.BigDecimal;

import de.csdev.ebus.utils.NumberUtils;

public class EBusTypeByte extends EBusTypeGenericReplaceValue {

    public static String UCHAR = "uchar";
    public static String BYTE = "byte";

    private static String[] supportedTypes = new String[] { BYTE, UCHAR };

    public EBusTypeByte() {
    	replaceValue = new byte[] {(byte)0xFF};
    }
    
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @SuppressWarnings("unchecked")
    public <T> T decodeInt(byte[] data) {
        return (T) BigDecimal.valueOf(data[0] & 0xFF);
    }

    public byte[] encodeInt(Object data) {

    	if (data instanceof byte[]) {
            return (byte[]) data;
        }

        BigDecimal b = NumberUtils.toBigDecimal(data);

        if (b == null) {
            return new byte[] { 0x00 };
        }

        return new byte[] { (byte) b.intValue() };
    }

}
