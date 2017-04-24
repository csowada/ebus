package de.csdev.ebus.cfg.datatypes;

import java.math.BigDecimal;

import de.csdev.ebus.utils.NumberUtils;

public class EBusTypeData2b extends EBusTypeGeneric {

    public static String DATA2B = "data2b";

    private static String[] supportedTypes = new String[] { DATA2B };

    @Override
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLenght() {
        return 2;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T decode(byte[] data) {
        BigDecimal intValue = types.decode(EBusTypeInteger.INTGER, data);
        return (T) intValue.divide(BigDecimal.valueOf(256));
    }

    @Override
    public byte[] encode(Object data) {
        BigDecimal b = NumberUtils.toBigDecimal(data);
        b = b.multiply(BigDecimal.valueOf(256));
        return types.encode(EBusTypeInteger.INTGER, b);
    }

}
