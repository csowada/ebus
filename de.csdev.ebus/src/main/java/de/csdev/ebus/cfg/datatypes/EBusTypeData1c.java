package de.csdev.ebus.cfg.datatypes;

import java.math.BigDecimal;

import de.csdev.ebus.utils.NumberUtils;

public class EBusTypeData1c extends EBusTypeByte {

    public static String DATA1C = "data1c";

    private static String[] supportedTypes = new String[] { DATA1C };

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T decode(byte[] data, Object... args) {
        BigDecimal x = super.decode(data, args);
        return (T) x.divide(BigDecimal.valueOf(2));
    }

    @Override
    public byte[] encode(Object data, Object... args) {
        BigDecimal b = NumberUtils.toBigDecimal(data);
        b = b.multiply(BigDecimal.valueOf(2));
        return new byte[] { (byte) b.intValue() };
    }

    @Override
    public void setTypesParent(EBusTypes types) {
        this.types = types;
    }

}
