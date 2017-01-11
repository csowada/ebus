package de.csdev.ebus.cfg.datatypes;

import java.math.BigDecimal;

import de.csdev.ebus.utils.NumberUtils;

public class EBusTypeInteger extends EBusTypeGeneric {

    public static String INTGER = "int";

    private static String[] supportedTypes = new String[] { INTGER };

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLenght() {
        return 2;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T decode(byte[] data, Object... args) {
        return (T) BigDecimal.valueOf((short) (data[0] << 8 | data[1] & 0xFF));
    }

    @Override
    public byte[] encode(Object data, Object... args) {
        BigDecimal b = NumberUtils.toBigDecimal(data);
        return new byte[] { (byte) (b.intValue() >> 8), (byte) b.intValue() };
    }

}
