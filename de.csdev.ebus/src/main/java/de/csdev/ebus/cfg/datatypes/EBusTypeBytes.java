package de.csdev.ebus.cfg.datatypes;

import java.util.Map;

public class EBusTypeBytes extends EBusTypeGeneric {

    public static String BYTES = "bytes";

    private static String[] supportedTypes = new String[] { BYTES };

    private Integer length = 1;

    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @SuppressWarnings("unchecked")
    public <T> T decode(byte[] data) {
        return (T) data;
    }

    public byte[] encode(Object data) {

        byte[] b = new byte[length];
        System.arraycopy(data.toString().getBytes(), 0, b, 0, b.length);

        return (byte[]) data;
    }

    @Override
    public int getTypeLenght() {
        return length;
    }

    @Override
    public IEBusType getInstance(Map<String, Object> properties) {

        if (properties.containsKey("length")) {
            EBusTypeBytes type = new EBusTypeBytes();
            type.length = (Integer) properties.get("length");
            type.types = this.types;
            return type;
        }

        return this;
    }
}
