package de.csdev.ebus.cfg.datatypes;

public class EBusTypeData1b extends EBusTypeGenericReplaceValue {

    public static String DATA1B = "data1b";

    private static String[] supportedTypes = new String[] { DATA1B };

    public EBusTypeData1b() {
    	replaceValue = new byte[] {(byte)0x80};
    }
    
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    public <T> T decodeInt(byte[] data) throws EBusTypeException {
        return types.decode(EBusTypeChar.CHAR, data);
    }

    public byte[] encodeInt(Object data) throws EBusTypeException {
        return types.encode(EBusTypeChar.CHAR, data);
    }

    @Override
    public void setTypesParent(EBusTypes types) {
        this.types = types;
    }

}
