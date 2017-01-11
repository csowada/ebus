package de.csdev.ebus.cfg.datatypes;

public class EBusTypeString extends EBusTypeGeneric {

    public static String STRING = "string";

    private static String[] supportedTypes = new String[] { STRING };

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T decode(byte[] data, Object... args) {
        return (T) new String(data);
    }

    @Override
    public byte[] encode(Object data, Object... args) {

        byte[] b = new byte[0];

        if (args[0] instanceof Integer) {
            b = new byte[(Integer) args[0]];
            System.arraycopy(data.toString().getBytes(), 0, b, 0, b.length);
        }

        return b;
    }
}
