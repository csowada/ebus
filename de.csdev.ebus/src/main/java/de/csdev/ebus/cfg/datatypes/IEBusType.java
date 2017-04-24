package de.csdev.ebus.cfg.datatypes;

import java.util.Map;

public interface IEBusType {

    public <T> T decode(byte[] data);

    public byte[] encode(Object data);

    public String[] getSupportedTypes();

    public void setTypesParent(EBusTypes types);

    public int getTypeLenght();

    public IEBusType getInstance(Map<String, Object> properties);
}
