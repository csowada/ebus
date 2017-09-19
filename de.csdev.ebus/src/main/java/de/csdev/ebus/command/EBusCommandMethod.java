/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCommandMethod implements IEBusCommandMethodWriteable {

    private byte[] command;

    private Byte destinationAddress;

    private List<IEBusValue> masterTypes;

    private List<IEBusValue> slaveTypes;

    private Byte sourceAddress;

    private ByteBuffer telegramMask;

    private IEBusCommand parent;

    private IEBusCommandMethod.Method method;

    public EBusCommandMethod(EBusCommand parent, IEBusCommandMethod.Method method) {
        this.parent = parent;
        this.method = method;

        parent.addCommandChannel(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getType()
     */
    public IEBusCommandMethod.Method getMethod() {
        return method;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommandWritable#addMasterValue(de.csdev.ebus.command.IEBusValue)
     */
    public IEBusCommandMethodWriteable addMasterValue(IEBusValue value) {
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
    public IEBusCommandMethodWriteable addSlaveValue(IEBusValue value) {
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

    public IEBusCommandMethodWriteable setCommand(byte[] command) {
        this.command = command;
        return this;
    }

    public void setDestinationAddress(Byte destinationAddress) {
        this.destinationAddress = destinationAddress;
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

    public Type getType() {

        if (method.equals(Method.BROADCAST)) {
            return Type.BROADCAST;
        }

        if (slaveTypes != null && !slaveTypes.isEmpty()) {
            return Type.MASTER_SLAVE;
        }

        return Type.MASTER_MASTER;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("EBusCommandMethod [");

        sb.append("command=" + EBusUtils.toHexDumpString(command).toString() + ", ");

        if (destinationAddress != null) {
            sb.append("destinationAddress=" + EBusUtils.toPrintHexDumpString(destinationAddress) + ", ");
        }

        if (sourceAddress != null) {
            sb.append("sourceAddress=" + EBusUtils.toPrintHexDumpString(sourceAddress) + ", ");
        }

        sb.append("masterTypes=" + masterTypes + ", ");
        sb.append("slaveTypes=" + slaveTypes + ", ");

        sb.append("method=" + method);
        sb.append("]");

        return sb.toString();
    }

}
