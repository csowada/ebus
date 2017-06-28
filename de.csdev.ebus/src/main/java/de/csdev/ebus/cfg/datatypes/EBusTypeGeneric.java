package de.csdev.ebus.cfg.datatypes;

import java.util.Map;

public abstract class EBusTypeGeneric implements IEBusType {

    public int getTypeLenght() {
        return 1;
    }

    protected EBusTypes types;

    public void setTypesParent(EBusTypes types) {
        this.types = types;
    }

    public IEBusType getInstance(Map<String, Object> properties) {
        return this;
    }
}
