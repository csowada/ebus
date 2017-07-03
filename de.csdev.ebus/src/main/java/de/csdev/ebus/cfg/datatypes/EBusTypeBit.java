package de.csdev.ebus.cfg.datatypes;

import java.util.Map;

public class EBusTypeBit extends EBusTypeGeneric {

    public static String BIT = "bit";

    private static String[] supportedTypes = new String[] { BIT };

    private Integer bit = null;

    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @SuppressWarnings("unchecked")
    public <T> T decode(byte[] data) {
        Boolean isSet = (data[0] >> bit & 0x1) == 1;
        return (T) isSet;
    }

    public byte[] encode(Object data) {

        throw new RuntimeException("Not implemented yet!");
        // BigDecimal.valueOf(0).
        //
        // BigDecimal b = (BigDecimal)data;
        // return new byte[] { (byte) b.intValue() };
    }

    @Override
    public IEBusType getInstance(Map<String, Object> properties) {

        if (properties.containsKey("pos")) {
            EBusTypeBit x = new EBusTypeBit();
            x.types = types;
            x.bit = (Integer) properties.get("pos");
            return x;
        }

        return this;
    }
}
