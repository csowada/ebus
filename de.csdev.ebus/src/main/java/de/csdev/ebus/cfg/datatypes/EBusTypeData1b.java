package de.csdev.ebus.cfg.datatypes;

public class EBusTypeData1b extends EBusTypeGeneric {

    public static String DATA1B = "data1b";

    private static String[] supportedTypes = new String[] { DATA1B };

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public <T> T decode(byte[] data, Object... args) {
        return types.decode(EBusTypeChar.CHAR, data, args);
    }

    @Override
    public byte[] encode(Object data, Object... args) {
        return types.encode(EBusTypeChar.CHAR, data);
    }

    @Override
    public void setTypesParent(EBusTypes types) {
        this.types = types;
    }

}
