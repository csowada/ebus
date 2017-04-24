package de.csdev.ebus.cfg.datatypes;

import java.util.Map;

public class EBusTypeBit extends EBusTypeGeneric {

    public static String BIT = "bit";

    private static String[] supportedTypes = new String[] { BIT };

    private Integer bit = null;

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T decode(byte[] data) {
        // int bit = (Integer) args[0];

        Boolean isSet = (data[0] >> bit & 0x1) == 1;
        return (T) isSet;
    }

    @Override
    public byte[] encode(Object data) {

        throw new RuntimeException("Not implemented yet!");
        // BigDecimal.valueOf(0).
        //
        // BigDecimal b = (BigDecimal)data;
        // return new byte[] { (byte) b.intValue() };
    }

    @Override
    public IEBusType getInstance(Map<String, Object> properties) {

        if (properties.containsKey("bit")) {
            EBusTypeBit x = new EBusTypeBit();
            x.bit = (Integer) properties.get("bit");
            return x;
        }

        return this;
    }
}
