package de.csdev.ebus.cfg.datatypes;

import java.math.BigDecimal;

import de.csdev.ebus.utils.NumberUtils;

public class EBusTypeWord extends EBusTypeGenericReplaceValue {

    public static String WORD = "word";
    public static String UINT = "uint";

    private static String[] supportedTypes = new String[] { WORD, UINT };

    public EBusTypeWord() {
        replaceValue = new byte[] { (byte) 0xFF, (byte) 0xFF };
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
        BigDecimal value = types.decode(EBusTypeInteger.INTEGER, data);
        if (value == null) {
            return null;
        }
        return (T) BigDecimal.valueOf((short) (value.intValue() & 0xffff));
    }

    @Override
    public byte[] encodeInt(Object data) throws EBusTypeException {
        BigDecimal b = NumberUtils.toBigDecimal(data);
        return types.encode(EBusTypeInteger.INTEGER, b.intValue() & 0xFFFF);
    }

}
