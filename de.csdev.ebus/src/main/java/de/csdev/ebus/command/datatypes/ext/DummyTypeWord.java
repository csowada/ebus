package de.csdev.ebus.command.datatypes.ext;

import java.math.BigDecimal;

import de.csdev.ebus.command.datatypes.EBusTypeException;

public class DummyTypeWord extends DummyTypeGenericReplaceValue<BigDecimal> {

    protected int variant = 1;

    @Override
    public String[] getSupportedTypes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BigDecimal decodeInt(byte[] data) throws EBusTypeException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte[] encodeInt(Object data) throws EBusTypeException {
        // TODO Auto-generated method stub
        return null;
    }

}
