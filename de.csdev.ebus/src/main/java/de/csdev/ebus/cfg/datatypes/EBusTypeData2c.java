package de.csdev.ebus.cfg.datatypes;

import java.math.BigDecimal;

import de.csdev.ebus.utils.NumberUtils;

public class EBusTypeData2c extends EBusTypeGeneric {

    public static String DATA2C = "data2c";

    private static String[] supportedTypes = new String[] { DATA2C };

    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLenght() {
        return 2;
    }

    @SuppressWarnings("unchecked")
    public <T> T decode(byte[] data) {
        short x = (short) (data[0] << 8 | data[1] & 0xFF);
        return (T) BigDecimal.valueOf(x).divide(BigDecimal.valueOf(16));
    }

    public byte[] encode(Object data) {

        BigDecimal b = NumberUtils.toBigDecimal(data == null ? 0 : data);
        b = b.multiply(BigDecimal.valueOf(16));
        short m = b.shortValue();
        return new byte[] { (byte) (m >> 8), (byte) m };
    }

}
