package de.csdev.ebus.cfg.datatypes;

import java.math.BigDecimal;

import de.csdev.ebus.utils.NumberUtils;

public class EBusTypeChar extends EBusTypeGeneric {

    public static String CHAR = "char";

    private static String[] supportedTypes = new String[] { CHAR };

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T decode(byte[] data, Object... args) {
        return (T) BigDecimal.valueOf(data[0]);
    }

    @Override
    public byte[] encode(Object data, Object... args) {
        BigDecimal b = NumberUtils.toBigDecimal(data);
        return new byte[] { (byte) ((byte) b.intValue() & 0xFF) };
    }

}
