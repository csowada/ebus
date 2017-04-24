package de.csdev.ebus.cfg.datatypes;

import java.math.BigDecimal;

import de.csdev.ebus.utils.NumberUtils;

public class EBusTypeByte extends EBusTypeGeneric {

    public static String UCHAR = "uchar";
    public static String BYTE = "byte";

    private static String[] supportedTypes = new String[] { BYTE, UCHAR };

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T decode(byte[] data) {
        return (T) BigDecimal.valueOf(data[0] & 0xFF);
    }

    @Override
    public byte[] encode(Object data) {
        BigDecimal b = NumberUtils.toBigDecimal(data);

        if (b == null) {
            return new byte[] { 0x00 };
        }

        return new byte[] { (byte) b.intValue() };
    }

}
