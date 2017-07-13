package de.csdev.ebus.cfg.datatypes.ext;

import de.csdev.ebus.cfg.datatypes.EBusTypeException;
import de.csdev.ebus.cfg.datatypes.EBusTypeGeneric;

public class EBusTypeKWCrc extends EBusTypeGeneric implements IEBusComplexType {

    public static String KW_CRC = "kw-crc";

    private static String[] supportedTypes = new String[] { KW_CRC };

    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    public <T> T decode(byte[] data) {
    	throw new RuntimeException("Not implmented!");
    }

    public byte[] encode(Object data) {
        throw new RuntimeException("Not implmented!");
    }

	public <T> T decodeComplex(byte[] rawData, int pos) throws EBusTypeException {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] encodeComplex(Object data) throws EBusTypeException {
		// TODO Auto-generated method stub
		return new byte[] {0x11};
	}

}
