package de.csdev.ebus.cfg.datatypes;

import java.math.BigDecimal;

import de.csdev.ebus.utils.NumberUtils;

public class EBusTypeChar extends EBusTypeGeneric {

    public static String CHAR = "char";

    private static String[] supportedTypes = new String[] { CHAR };

    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @SuppressWarnings("unchecked")
    public <T> T decode(byte[] data) {
        return (T) BigDecimal.valueOf(data[0]);
    }

    public byte[] encode(Object data) {
        BigDecimal b = NumberUtils.toBigDecimal(data);
        return new byte[] { (byte) ((byte) b.intValue() & 0xFF) };
    }

}
