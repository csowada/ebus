package de.csdev.ebus.cfg.datatypes;

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

}
