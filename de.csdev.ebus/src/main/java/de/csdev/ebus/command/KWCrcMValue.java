package de.csdev.ebus.command;

import java.math.BigDecimal;

import de.csdev.ebus.cfg.datatypes.IEBusType;

public class KWCrcMValue implements IEBusValue {

    private IEBusType type;

    public KWCrcMValue(IEBusType type) {
        this.type = type;
    }

    @Override
    public IEBusType getType() {
        return type;
    }

    @Override
    public BigDecimal getDefaultValue() {
        return BigDecimal.valueOf(0);
    }

    @Override
    public void setType(IEBusType type) {
        this.type = type;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

}
