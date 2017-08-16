package de.csdev.ebus.cfg.datatypes;

import java.math.BigDecimal;

import de.csdev.ebus.utils.NumberUtils;

public class EBusTypeData2c extends EBusTypeGenericReplaceValue {

    public static String DATA2C = "data2c";

    private static String[] supportedTypes = new String[] { DATA2C };

    public EBusTypeData2c() {
        replaceValue = new byte[] { (byte) 0x00, (byte) 0x80 };
    }

    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLenght() {
        return 2;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T decodeInt(byte[] data) throws EBusTypeException {
        BigDecimal intValue = types.decode(EBusTypeInteger.INTEGER, data);
        if (intValue == null) {
            return null;
        }
        return (T) intValue.divide(BigDecimal.valueOf(16));
    }

    @Override
    public byte[] encodeInt(Object data) throws EBusTypeException {
        BigDecimal b = NumberUtils.toBigDecimal(data);
        b = b.multiply(BigDecimal.valueOf(16));
        return types.encode(EBusTypeInteger.INTEGER, b);
    }

}
