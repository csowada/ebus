package de.csdev.ebus.cfg.datatypes.ext;

import java.math.BigDecimal;
import java.util.Map;

import de.csdev.ebus.cfg.datatypes.EBusTypeGeneric;
import de.csdev.ebus.cfg.datatypes.EBusTypeWord;
import de.csdev.ebus.cfg.datatypes.IEBusType;
import de.csdev.ebus.utils.NumberUtils;

public class EBusTypeMultiWord extends EBusTypeGeneric {

    public static String MWORD = "mword";

    private static String[] supportedTypes = new String[] { MWORD };

    private int length = 2;
    private int pow = 1000;
    
    public String[] getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public int getTypeLenght() {
        return length * 2;
    }

    @SuppressWarnings("unchecked")
    public <T> T decode(byte[] data) {
    	
    	
    	byte[] dataNew = new byte[2];

    	int x = this.length - 1;
    	
    	BigDecimal valx = new BigDecimal(0);
    	
    	for (int i = 0; i <= x; i++) {
    		
        	System.arraycopy(data, i*2, dataNew, 0, dataNew.length);
        	BigDecimal value = types.decode(EBusTypeWord.WORD, dataNew);
        	
        	BigDecimal factor = new BigDecimal(this.pow).pow(i);
        	valx = valx.add(value.multiply(factor));
		}

    	System.out.println("EBusTypeDWord.decode()" + valx.toString());
        return (T) valx;
    }

    public byte[] encode(Object data) {
        BigDecimal b = NumberUtils.toBigDecimal(data);
        return new byte[] { (byte) b.intValue(), (byte) (b.intValue() >> 8), 0, 0 };
    }

    @Override
    public IEBusType getInstance(Map<String, Object> properties) {

        if (properties.containsKey("length")) {
        	EBusTypeMultiWord x = new EBusTypeMultiWord();
        	x.types = this.types;
        	
            x.length = (Integer) properties.get("length");
            
            if (properties.containsKey("pow")) {
            	x.pow = (Integer) properties.get("pow");
            }
            
            System.out.println("EBusTypeDWord.getInstance()");
            
            return x;
        }

        return this;
    }
    
}
