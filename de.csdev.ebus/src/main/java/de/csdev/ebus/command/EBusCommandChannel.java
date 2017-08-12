package de.csdev.ebus.command;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import de.csdev.ebus.command.IEBusCommand.Type;

public class EBusCommandChannel implements IEBusCommandChannelWriteable {

    private byte[] command;

    private Byte destinationAddress;

    private List<IEBusValue> extendCommandValue;

    private List<IEBusValue> masterTypes;

    private List<IEBusValue> slaveTypes;

    private Byte sourceAddress;

    private ByteBuffer telegramMask;

    private IEBusCommand parent;

    private Type type;

    public EBusCommandChannel(EBusCommand parent) {
        this.parent = parent;
        parent.addCommandChannel(this);
    }

    public IEBusCommandChannelWriteable setType(Type type) {
        this.type = type;
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getType()
     */
    public Type getType() {
        return type;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommandWritable#addMasterValue(de.csdev.ebus.command.IEBusValue)
     */
    public IEBusCommandChannelWriteable addMasterValue(IEBusValue value) {
        if (masterTypes == null) {
            masterTypes = new ArrayList<IEBusValue>();
        }

        masterTypes.add(value);

        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommandWritable#addSlaveValue(de.csdev.ebus.command.IEBusValue)
     */
    public IEBusCommandChannelWriteable addSlaveValue(IEBusValue value) {
        if (slaveTypes == null) {
            slaveTypes = new ArrayList<IEBusValue>();
        }

        slaveTypes.add(value);
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getCommand()
     */
    public byte[] getCommand() {
        return command;
    }

    public String getConfigurationSource() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getDestinationAddress()
     */
    public Byte getDestinationAddress() {
        return destinationAddress;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getExtendCommandValue()
     */
    public List<IEBusValue> getExtendCommandValue() {
        return extendCommandValue;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getMasterTelegramMask()
     */
    public ByteBuffer getMasterTelegramMask() {

        if (telegramMask == null) {
            telegramMask = EBusCommandUtils.getMasterTelegramMask(this);
        }

        return telegramMask;

    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getMasterTypes()
     */
    public List<IEBusValue> getMasterTypes() {
        return masterTypes;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommandChannel#getParent()
     */
    public IEBusCommand getParent() {
        return parent;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getSlaveTypes()
     */
    public List<IEBusValue> getSlaveTypes() {
        return slaveTypes;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getSourceAddress()
     */
    public Byte getSourceAddress() {
        return sourceAddress;
    }

    public IEBusCommandChannelWriteable setCommand(byte[] command) {
        this.command = command;
        return this;
    }

    public void setDestinationAddress(Byte destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public void setExtendCommandValue(List<IEBusValue> extendCommandValue) {
        this.extendCommandValue = extendCommandValue;
    }

    public void setMasterTypes(List<IEBusValue> masterTypes) {
        this.masterTypes = masterTypes;
    }

    public void setSlaveTypes(List<IEBusValue> slaveTypes) {
        this.slaveTypes = slaveTypes;
    }

    public void setSourceAddress(Byte sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

}
