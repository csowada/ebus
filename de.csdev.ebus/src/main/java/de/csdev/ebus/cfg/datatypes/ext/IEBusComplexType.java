package de.csdev.ebus.cfg.datatypes.ext;

import de.csdev.ebus.cfg.datatypes.EBusTypeException;

public interface IEBusComplexType {
	
    public <T> T decodeComplex(byte[] rawData, int pos) throws EBusTypeException;

    public byte[] encodeComplex(Object data) throws EBusTypeException;
}
