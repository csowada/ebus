package de.csdev.ebus.command;

import de.csdev.ebus.cfg.datatypes.IEBusType;

public interface IEBusValue {

    public IEBusType getType();

    public void setType(IEBusType type);

    public Object getDefaultValue();

    public String getName();

}
