package de.csdev.ebus.command.datatypes.std;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.lang.ArrayUtils;

import de.csdev.ebus.command.datatypes.EBusAbstractType;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.utils.NumberUtils;

public abstract class AbstractEBusTypeNumber extends EBusAbstractType<BigDecimal> {

    @Override
    public byte[] getReplaceValue() {
        int length = getTypeLength();
        if (replaceValue == null || replaceValue.length == 0) {
            replaceValue = new byte[length];
            replaceValue[length - 1] = (byte) 0x80;
        }

        return replaceValue;
    }

    @Override
    public BigDecimal decodeInt(byte[] data) throws EBusTypeException {
        byte[] clone = ArrayUtils.clone(data);
        ArrayUtils.reverse(clone);
        return new BigDecimal(new BigInteger(clone));
    }

    @Override
    public byte[] encodeInt(Object data) throws EBusTypeException {
        BigDecimal b = NumberUtils.toBigDecimal(data == null ? 0 : data);
        long l = b.longValue();
        int length = getTypeLength();
        byte[] result = new byte[length];
        for (int i = 0; i <= length - 1; i++) {
            result[i] = (byte) (l & 0xFF);
            l >>= 8;
        }

        return result;
    }

}
