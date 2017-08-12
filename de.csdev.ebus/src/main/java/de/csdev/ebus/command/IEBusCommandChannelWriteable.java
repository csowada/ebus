package de.csdev.ebus.command;

import de.csdev.ebus.command.IEBusCommand.Type;

public interface IEBusCommandChannelWriteable extends IEBusCommandChannel {

    public IEBusCommandChannelWriteable setCommand(byte[] command);

    public IEBusCommandChannelWriteable addSlaveValue(IEBusValue value);

    public IEBusCommandChannelWriteable addMasterValue(IEBusValue value);

    public IEBusCommandChannelWriteable setType(Type type);

}
