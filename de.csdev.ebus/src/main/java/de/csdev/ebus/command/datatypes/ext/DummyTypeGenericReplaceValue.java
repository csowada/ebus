package de.csdev.ebus.command.datatypes.ext;

import org.apache.commons.lang.ArrayUtils;

import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.utils.EBusUtils;

public abstract class DummyTypeGenericReplaceValue<T> extends DummyTypeGeneric<T> {

    protected byte[] replaceValue = null;

    public byte[] getReplaceValue() {
    	return replaceValue;
    }
    
    protected boolean equalsReplaceValue(byte[] data) {
        return ArrayUtils.isEquals(data, this.replaceValue);
    }

    @Override
    public T decode(byte[] data) throws EBusTypeException {

        applyByteOrder(data);

        if (equalsReplaceValue(data)) {
            return null;
        }

        return decodeInt(data);
    }

    @Override
    public byte[] encode(Object data) throws EBusTypeException {

        if (data == null) {
            return replaceValue;
        }

        byte[] result = encodeInt(data);
        applyByteOrder(result);

        return encodeInt(result);
    }
    
    @Override
    public String toString() {
        return "XXX [replaceValue=" + EBusUtils.toHexDumpString(replaceValue).toString() + "]";
    }
}
