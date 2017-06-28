package de.csdev.ebus.command;

import java.math.BigDecimal;

import de.csdev.ebus.cfg.datatypes.IEBusType;

public class KWCrcMValue implements IEBusValue {

    private IEBusType type;

    public KWCrcMValue(IEBusType type) {
        this.type = type;
    }

    public IEBusType getType() {
        return type;
    }

    public BigDecimal getDefaultValue() {
        return BigDecimal.valueOf(0);
    }

    public void setType(IEBusType type) {
        this.type = type;
    }

    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

}
