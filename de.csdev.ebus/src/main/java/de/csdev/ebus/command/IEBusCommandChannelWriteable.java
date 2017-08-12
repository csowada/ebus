package de.csdev.ebus.command;

public interface IEBusCommandChannelWriteable extends IEBusCommandChannel {

    public IEBusCommandChannelWriteable setCommand(byte[] command);

    public IEBusCommandChannelWriteable addSlaveValue(IEBusValue value);

    public IEBusCommandChannelWriteable addMasterValue(IEBusValue value);

}
