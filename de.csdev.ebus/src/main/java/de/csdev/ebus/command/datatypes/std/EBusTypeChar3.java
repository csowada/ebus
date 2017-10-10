package de.csdev.ebus.command.datatypes.std;

import java.math.BigDecimal;

import de.csdev.ebus.command.datatypes.EBusAbtstractReplaceValueType;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.utils.NumberUtils;

public class EBusTypeChar3 extends EBusAbtstractReplaceValueType<BigDecimal> {

    public static String CHAR = "char3";

    private static String[] supportedTypes = new String[] { CHAR };

    protected int variant = 1;

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public byte[] getReplaceValue() {

        if (replaceValue == null || replaceValue.length == 0) {
            replaceValue = new byte[length];
            for (int i = 0; i < replaceValue.length; i++) {
                replaceValue[i] = (byte) 0xFF;
            }
        }

        return replaceValue;
    }

    @Override
    public BigDecimal decodeInt(byte[] data) throws EBusTypeException {
        long result = 0;
        for (int i = length; i > 0; i--) {
            result <<= 8;
            result |= (data[i - 1] & 0xFF);
        }

        return BigDecimal.valueOf(result);
    }

    @Override
    public byte[] encodeInt(Object data) throws EBusTypeException {
        BigDecimal b = NumberUtils.toBigDecimal(data == null ? 0 : data);
        long l = b.longValue();

        byte[] result = new byte[length];
        for (int i = 0; i <= length - 1; i++) {
            result[i] = (byte) (l & 0xFF);
            l >>= 8;
        }

        return result;
    }

}
