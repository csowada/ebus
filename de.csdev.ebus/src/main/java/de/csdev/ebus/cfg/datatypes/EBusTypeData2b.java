package de.csdev.ebus.cfg.datatypes;

import java.math.BigDecimal;

import de.csdev.ebus.utils.NumberUtils;

public class EBusTypeData2b extends EBusTypeGenericReplaceValue {

    public static String DATA2B = "data2b";

    private static String[] supportedTypes = new String[] { DATA2B };

    public EBusTypeData2b() {
    	replaceValue = new byte[] {(byte)0x80, (byte)0x00};
    }
    
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLenght() {
        return 2;
    }

    @SuppressWarnings("unchecked")
    public <T> T decodeInt(byte[] data) {
        BigDecimal intValue = types.decode(EBusTypeInteger.INTGER, data);
        return (T) intValue.divide(BigDecimal.valueOf(256));
    }

    public byte[] encodeInt(Object data) {
        BigDecimal b = NumberUtils.toBigDecimal(data);
        b = b.multiply(BigDecimal.valueOf(256));
        return types.encode(EBusTypeInteger.INTGER, b);
    }

}
