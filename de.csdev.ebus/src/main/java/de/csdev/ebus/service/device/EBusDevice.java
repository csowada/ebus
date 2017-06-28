/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.service.device;

import java.math.BigDecimal;

import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada
 *
 */
public class EBusDevice implements IEBusDevice {

    byte masterAddress;
    
    byte slaveAddress;
    
    public long lastActivity;
    
    private byte manufacturer;
    
    private String deviceId;
    
    private BigDecimal softwareVersion;
    
    private BigDecimal hardwareVersion;

    private EBusDeviceTable deviceTable;

    public void setManufacturer(byte manufactur) {
        this.manufacturer = manufactur;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setSoftwareVersion(BigDecimal softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public void setHardwareVersion(BigDecimal hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public EBusDevice(byte masterAddress, EBusDeviceTable deviceTable) {
        this.masterAddress = masterAddress;
        this.deviceTable = deviceTable;
        this.slaveAddress = EBusUtils.getSlaveAddress(masterAddress);
    }

    public long getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(long lastActivity) {
        this.lastActivity = lastActivity;
    }

    public byte getMasterAddress() {
        return masterAddress;
    }

    public byte getSlaveAddress() {
        return slaveAddress;
    }

    public byte getManufacturer() {
        return manufacturer;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public BigDecimal getSoftwareVersion() {
        return softwareVersion;
    }

    public BigDecimal getHardwareVersion() {
        return hardwareVersion;
    }

    public String getManufacturerName() {
        return deviceTable.getManufacturerName(manufacturer);
    }
    
    @Override
    public String toString() {
        return "EBusDevice [masterAddress=" + masterAddress + ", slaveAddress=" + slaveAddress + ", lastActivity="
                + lastActivity + ", manufacturer=" + manufacturer + "("+getManufacturerName()+"), deviceId=" + deviceId + ", softwareVersion="
                + softwareVersion + ", hardwareVersion=" + hardwareVersion + "]";
    }
}