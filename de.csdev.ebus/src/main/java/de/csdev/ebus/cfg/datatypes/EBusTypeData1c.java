package de.csdev.ebus.cfg.datatypes;

import java.math.BigDecimal;

import de.csdev.ebus.utils.NumberUtils;

public class EBusTypeData1c extends EBusTypeGenericReplaceValue {

    public static String DATA1C = "data1c";

    private static String[] supportedTypes = new String[] { DATA1C };

    public EBusTypeData1c() {
    	replaceValue = new byte[] {(byte)0xFF};
    }
    
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T decodeInt(byte[] data) throws EBusTypeException {
        BigDecimal x = types.getType(EBusTypeByte.BYTE).decode(data);
        return (T) x.divide(BigDecimal.valueOf(2));
    }

    @Override
    public byte[] encodeInt(Object data) {
        BigDecimal b = NumberUtils.toBigDecimal(data);
        b = b.multiply(BigDecimal.valueOf(2));
        return new byte[] { (byte) b.intValue() };
    }

    @Override
    public void setTypesParent(EBusTypes types) {
        this.types = types;
    }

}
