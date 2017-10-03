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
import java.util.Arrays;
import java.util.List;

import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCommandMethod implements IEBusCommandMethod {

    private byte[] command;

    private Byte destinationAddress;

    private List<IEBusValue> masterTypes;

    private IEBusCommandMethod.Method method;

    private IEBusCommand parent;

    private List<IEBusValue> slaveTypes;

    private Byte sourceAddress;

    private ByteBuffer telegramMask;

    public EBusCommandMethod(EBusCommand parent, IEBusCommandMethod.Method method) {
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

        masterTypes.add(value);

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

        slaveTypes.add(value);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        EBusCommandMethod other = (EBusCommandMethod) obj;
        if (!Arrays.equals(command, other.command)) {
            return false;
        }
        if (destinationAddress == null) {
            if (other.destinationAddress != null) {
                return false;
            }
        } else if (!destinationAddress.equals(other.destinationAddress)) {
            return false;
        }
        if (masterTypes == null) {
            if (other.masterTypes != null) {
                return false;
            }
        } else if (!masterTypes.equals(other.masterTypes)) {
            return false;
        }
        if (method != other.method) {
            return false;
        }
        if (slaveTypes == null) {
            if (other.slaveTypes != null) {
                return false;
            }
        } else if (!slaveTypes.equals(other.slaveTypes)) {
            return false;
        }
        if (sourceAddress == null) {
            if (other.sourceAddress != null) {
                return false;
            }
        } else if (!sourceAddress.equals(other.sourceAddress)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getCommand()
     */
    @Override
    public byte[] getCommand() {
        return command;
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
    @Override
    public List<IEBusValue> getMasterTypes() {
        return masterTypes;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getType()
     */
    @Override
    public IEBusCommandMethod.Method getMethod() {
        return method;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommandChannel#getParent()
     */
    @Override
    public IEBusCommand getParent() {
        return parent;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getSlaveTypes()
     */
    @Override
    public List<IEBusValue> getSlaveTypes() {
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(command);
        result = prime * result + ((destinationAddress == null) ? 0 : destinationAddress.hashCode());
        result = prime * result + ((masterTypes == null) ? 0 : masterTypes.hashCode());
        result = prime * result + ((method == null) ? 0 : method.hashCode());
        result = prime * result + ((slaveTypes == null) ? 0 : slaveTypes.hashCode());
        result = prime * result + ((sourceAddress == null) ? 0 : sourceAddress.hashCode());
        return result;
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
