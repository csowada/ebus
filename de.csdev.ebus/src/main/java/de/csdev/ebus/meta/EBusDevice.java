/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.meta;

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
    private byte vendor;
    private String deviceId;
    private BigDecimal softwareVersion;
    private BigDecimal hardwareVersion;

    public void setVendor(byte vendor) {
        this.vendor = vendor;
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

    public EBusDevice(byte masterAddress) {
        this.masterAddress = masterAddress;
        this.slaveAddress = EBusUtils.getSlaveAddress(masterAddress);
    }

    @Override
    public long getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(long lastActivity) {
        this.lastActivity = lastActivity;
    }

    @Override
    public byte getMasterAddress() {
        return masterAddress;
    }

    @Override
    public byte getSlaveAddress() {
        return slaveAddress;
    }

    @Override
    public byte getVendor() {
        return vendor;
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public BigDecimal getSoftwareVersion() {
        return softwareVersion;
    }

    @Override
    public BigDecimal getHardwareVersion() {
        return hardwareVersion;
    }

    @Override
    public String toString() {
        return "EBusDevice [masterAddress=" + masterAddress + ", slaveAddress=" + slaveAddress + ", lastActivity="
                + lastActivity + ", vendor=" + vendor + ", deviceId=" + deviceId + ", softwareVersion="
                + softwareVersion + ", hardwareVersion=" + hardwareVersion + "]";
    }
}