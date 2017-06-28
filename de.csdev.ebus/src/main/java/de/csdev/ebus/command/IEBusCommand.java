package de.csdev.ebus.command;

import java.nio.ByteBuffer;
import java.util.List;

public interface IEBusCommand {

    public enum Type {
        READ,
        WRITE
    }
	
	public ByteBuffer getMasterTelegramMask();
	
	public List<IEBusValue> getExtendCommandValue();
	
	public List<IEBusValue> getMasterTypes();
	
	public List<IEBusValue> getSlaveTypes();
	
	public byte[] getCommand();
	
	public String getConfigurationSource();
	
	public String getDescription();
	
	public String getId();
	
	public Type getType();
	
}
