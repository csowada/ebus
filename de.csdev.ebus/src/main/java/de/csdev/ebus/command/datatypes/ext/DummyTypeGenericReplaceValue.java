package de.csdev.ebus.command.datatypes.ext;

import org.apache.commons.lang.ArrayUtils;

import de.csdev.ebus.command.datatypes.EBusTypeException;

public abstract class DummyTypeGenericReplaceValue<T> extends DummyTypeGeneric<T> {

    protected byte[] replaceValue = null;

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
}
