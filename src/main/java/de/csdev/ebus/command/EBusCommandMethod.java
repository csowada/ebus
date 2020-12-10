/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
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
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCommandMethod implements IEBusCommandMethod {

    private byte[] command;

    private Byte destinationAddress;

    private List<IEBusValue> masterTypes;

    private IEBusCommandMethod.@NonNull Method method;

    private @NonNull IEBusCommand parent;

    private List<IEBusValue> slaveTypes;

    private Byte sourceAddress;

    private ByteBuffer telegramMask;

    private Type type;

    public EBusCommandMethod(EBusCommand parent, IEBusCommandMethod.Method method) {

        Objects.requireNonNull(parent);
        Objects.requireNonNull(method);

        this.parent = parent;
        this.method = method;

        parent.addCommandChannel(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommandWritable#addMasterValue(de.csdev.ebus.command.IEBusValue)
     */
    public EBusCommandMethod addMasterValue(IEBusValue value) {
        if (masterTypes == null) {
            masterTypes = new ArrayList<IEBusValue>();
        }

        if (value != null) {
            masterTypes.add(value);
        }

        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommandWritable#addSlaveValue(de.csdev.ebus.command.IEBusValue)
     */
    public EBusCommandMethod addSlaveValue(IEBusValue value) {
        if (slaveTypes == null) {
            slaveTypes = new ArrayList<IEBusValue>();
        }

        if (value != null) {
            slaveTypes.add(value);
        }

        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getCommand()
     */
    @Override
    public byte @NonNull [] getCommand() {
        return Objects.requireNonNull(command);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getDestinationAddress()
     */
    @Override
    public Byte getDestinationAddress() {
        return destinationAddress;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getMasterTelegramMask()
     */
    @Override
    public @NonNull ByteBuffer getMasterTelegramMask() {

        if (telegramMask == null) {
            telegramMask = EBusCommandUtils.getMasterTelegramMask(this);
        }

        return Objects.requireNonNull(telegramMask);

    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getMasterTypes()
     */
    @Override
    public @Nullable List<IEBusValue> getMasterTypes() {
        return masterTypes;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getType()
     */
    @Override
    public IEBusCommandMethod.@NonNull Method getMethod() {
        return method;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommandChannel#getParent()
     */
    @Override
    public @NonNull IEBusCommand getParent() {
        return parent;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getSlaveTypes()
     */
    @Override
    public @Nullable List<IEBusValue> getSlaveTypes() {
        return slaveTypes;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getSourceAddress()
     */
    @Override
    public Byte getSourceAddress() {
        return sourceAddress;
    }

    @Override
    public @NonNull Type getType() {

        if (type != null) {
            return type;
        }

        if (method.equals(Method.BROADCAST)) {
            return Type.BROADCAST;
        }

        return Type.MASTER_SLAVE;
    }

    public EBusCommandMethod setCommand(byte[] command) {
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

    public void setType(Type type) {
        this.type = type;
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
        sb.append("type=" + type);
        sb.append("]");

        return sb.toString();
    }

}
