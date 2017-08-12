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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.csdev.ebus.utils.CollectionUtils;

/**
 * @author Christian Sowada
 *
 */
public class EBusCommand implements IEBusCommandWritable {

    private byte[] command;

    private String configurationSource;

    private String description;

    private Byte destinationAddress;

    private String device;

    private List<IEBusValue> extendCommandValue;

    private Map<String, Object> properties;

    private String id;

    private List<IEBusValue> masterTypes;

    private List<IEBusValue> slaveTypes;

    private Byte sourceAddress;

    private ByteBuffer telegramMask;

    private Type type;

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommandWritable#addMasterValue(de.csdev.ebus.command.IEBusValue)
     */
    public IEBusCommandWritable addMasterValue(IEBusValue value) {
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
    public IEBusCommandWritable addSlaveValue(IEBusValue value) {
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

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getConfigurationSource()
     */
    public String getConfigurationSource() {
        return configurationSource;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getDestinationAddress()
     */
    public Byte getDestinationAddress() {
        return destinationAddress;
    }

    // public IEBusCommandWritable addExtendedCommand(IEBusValue value) {
    // if (extendCommandValue == null) {
    // extendCommandValue = new ArrayList<IEBusValue>();
    // }
    //
    // extendCommandValue.add(value);
    //
    // return this;
    // }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getDevice()
     */
    public String getDevice() {
        return device;
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
     * @see de.csdev.ebus.command.IEBusCommand#getId()
     */
    public String getId() {
        return id;
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

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getType()
     */
    public Type getType() {
        return type;
    }

    public EBusCommand setCommand(byte[] command) {
        this.command = command;
        return this;
    }

    public void setConfigurationSource(String configurationSource) {
        this.configurationSource = configurationSource;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDestinationAddress(Byte destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public void setExtendCommandValue(List<IEBusValue> extendCommandValue) {
        this.extendCommandValue = extendCommandValue;
    }

    public EBusCommand setId(String id) {
        this.id = id;
        return this;
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

    public IEBusCommandWritable setType(Type type) {
        this.type = type;
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "EBusCommand [description=" + description + ", command=" + Arrays.toString(command)
                + ", configurationSource=" + configurationSource + ", extendCommandValue=" + extendCommandValue
                + ", id=" + id + ", masterTypes=" + masterTypes + ", slaveTypes=" + slaveTypes + ", type=" + type
                + ", telegramMask=" + telegramMask + ", device=" + device + ", destinationAddress=" + destinationAddress
                + ", sourceAddress=" + sourceAddress + "]";
    }

    public Map<String, Object> getProperties() {
        return CollectionUtils.unmodifiableNotNullMap(properties);
    }

    public void setProperty(String key, String value) {
        properties = CollectionUtils.newMapIfNull(properties);
        properties.put(key, value);
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = new HashMap<String, Object>();
        this.properties.putAll(properties);
    }

}
