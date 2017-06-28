package de.csdev.ebus.command;

public interface IEBusCommandWritable extends IEBusCommand {

	public IEBusCommandWritable addSlaveValue(IEBusValue value);
	
	public IEBusCommandWritable addMasterValue(IEBusValue value);
	
	public IEBusCommandWritable addExtendedCommand(IEBusValue value);

	public IEBusCommandWritable setType(Type type);

	public IEBusCommandWritable setId(String id);

	public IEBusCommandWritable setCommand(byte[] command);

	public void setConfigurationSource(String configurationSource);

	public void setDescription(String description);
	
}
