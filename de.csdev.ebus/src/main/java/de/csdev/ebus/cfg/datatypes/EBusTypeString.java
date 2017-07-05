package de.csdev.ebus.cfg.datatypes;

import java.util.Map;

public class EBusTypeString extends EBusTypeGeneric {

    public static String STRING = "string";

    private static String[] supportedTypes = new String[] { STRING };

    private Integer length = 1;

    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @SuppressWarnings("unchecked")
    public <T> T decode(byte[] data) {
        return (T) new String(data);
    }

    public byte[] encode(Object data) {

    	// return a empty string with defined len
    	if(data == null) {
    		StringBuilder sb = new StringBuilder();
    		for (int i = 0; i < length; i++) {
				sb.append(' ');
			}
    		return sb.toString().getBytes();
    	}
    	
        byte[] b = new byte[length];
        System.arraycopy(data.toString().getBytes(), 0, b, 0, b.length);

        return b;
    }

    @Override
    public int getTypeLenght() {
        return length;
    }

    @Override
    public IEBusType getInstance(Map<String, Object> properties) {

        if (properties.containsKey("length")) {
            EBusTypeString type = new EBusTypeString();
            type.types = this.types;
            type.length = (Integer) properties.get("length");
            return type;
        }

        return this;
    }
}
