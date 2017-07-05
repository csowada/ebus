package de.csdev.ebus.cfg.datatypes;

import java.math.BigDecimal;

import de.csdev.ebus.utils.NumberUtils;

public class EBusTypeInteger extends EBusTypeGeneric {

    public static String INTGER = "int";

    private static String[] supportedTypes = new String[] { INTGER };

    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLenght() {
        return 2;
    }

    @SuppressWarnings("unchecked")
    public <T> T decode(byte[] data) {
        return (T) BigDecimal.valueOf((short) (data[1] << 8 | data[0] & 0xFF));
    }

    public byte[] encode(Object data) {
        BigDecimal b = NumberUtils.toBigDecimal(data == null ? 0 : data);
        return new byte[] { (byte) b.intValue(), (byte) (b.intValue() >> 8) };
    }

}
