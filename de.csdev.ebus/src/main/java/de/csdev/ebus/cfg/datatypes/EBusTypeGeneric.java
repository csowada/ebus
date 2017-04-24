package de.csdev.ebus.cfg.datatypes;

import java.util.Map;

public abstract class EBusTypeGeneric implements IEBusType {

    @Override
    public int getTypeLenght() {
        return 1;
    }

    protected EBusTypes types;

    @Override
    public void setTypesParent(EBusTypes types) {
        this.types = types;
    }

    @Override
    public IEBusType getInstance(Map<String, Object> properties) {
        return this;
    }
}
