package de.csdev.ebus.cfg.datatypes;

import org.apache.commons.lang.ArrayUtils;

public abstract class EBusTypeGenericReplaceValue extends EBusTypeGeneric {

	protected byte[] replaceValue = null;
	
	protected boolean equalsReplaceValue(byte[] data) {
		return ArrayUtils.isEquals(data, this.replaceValue);
	}

	public <T> T decode(byte[] data) throws EBusTypeException {
		
		if(equalsReplaceValue(data))
			return null;

		return decodeInt(data);
	}

	public byte[] encode(Object data) throws EBusTypeException {
		
		if(data == null)
			return replaceValue;

		return encodeInt(data);
	}

	public abstract <T> T decodeInt(byte[] data) throws EBusTypeException;
	public abstract byte[] encodeInt(Object data) throws EBusTypeException;
}
