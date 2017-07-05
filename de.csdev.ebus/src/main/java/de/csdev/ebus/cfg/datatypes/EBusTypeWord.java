package de.csdev.ebus.cfg.datatypes;

import java.math.BigDecimal;

import de.csdev.ebus.utils.NumberUtils;

public class EBusTypeWord extends EBusTypeGeneric {

    public static String WORD = "word";
    public static String UINT = "uint";

    private static String[] supportedTypes = new String[] { WORD, UINT };

    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLenght() {
        return 2;
    }

    @SuppressWarnings("unchecked")
    public <T> T decode(byte[] data) {
        BigDecimal value = types.decode(EBusTypeInteger.INTGER, data);
        return (T) BigDecimal.valueOf((short) (value.intValue() & 0xffff));
    }

    public byte[] encode(Object data) {
        BigDecimal b = NumberUtils.toBigDecimal(data == null ? 0 : data);
        return types.encode(EBusTypeInteger.INTGER, b.intValue() & 0xFFFF);
    }

}
