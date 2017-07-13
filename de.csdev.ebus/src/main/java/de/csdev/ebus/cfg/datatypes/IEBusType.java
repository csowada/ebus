package de.csdev.ebus.cfg.datatypes;

import java.util.Map;

public interface IEBusType {

    public <T> T decode(byte[] data) throws EBusTypeException;

    public byte[] encode(Object data) throws EBusTypeException;

    public String[] getSupportedTypes();

    public void setTypesParent(EBusTypes types);

    public int getTypeLenght();

    public IEBusType getInstance(Map<String, Object> properties);

}
