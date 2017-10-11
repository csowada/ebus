package de.csdev.ebus.command.datatypes;

import org.apache.commons.lang.ArrayUtils;

public abstract class EBusAbtstractReplaceValueType<T> extends EBusAbstractType<T> {

    protected byte[] replaceValue = null;

    /**
     * @return
     */
    public byte[] getReplaceValue() {
        return replaceValue;
    }

    /**
     * @param data
     * @return
     */
    protected boolean equalsReplaceValue(byte[] data) {
        return ArrayUtils.isEquals(data, getReplaceValue());
    }

    /**
     * @param replaceValue
     * @throws EBusTypeException
     */
    public void setReplaceValue(Object replaceValue) throws EBusTypeException {
        this.replaceValue = encodeInt(replaceValue);
    }

    /**
     * @param replaceValue
     * @throws EBusTypeException
     */
    public void setReplaceValue(byte[] replaceValue) throws EBusTypeException {
        this.replaceValue = applyByteOrder(replaceValue);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.datatypes.ext.DummyTypeGeneric#decode(byte[])
     */
    @Override
    public T decode(byte[] data) throws EBusTypeException {

        data = applyByteOrder(data);

        // return null in case of a replace value
        if (equalsReplaceValue(data)) {
            return null;
        }

        return decodeInt(data);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.datatypes.ext.DummyTypeGeneric#encode(java.lang.Object)
     */
    @Override
    public byte[] encode(Object data) throws EBusTypeException {

        // return the replacec value
        if (data == null) {
            return applyByteOrder(replaceValue);
        }

        byte[] result = encodeInt(data);
        result = applyByteOrder(result);

        return result;
    }
}
