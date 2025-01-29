/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.service.device;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusDevice implements IEBusDevice {

    private byte[] deviceId;

    private EBusDeviceTable deviceTable;

    private BigDecimal hardwareVersion;

    private long lastActivity;

    private Byte manufacturer;

    private Byte masterAddress;

    private Byte slaveAddress;

    private BigDecimal softwareVersion;

    public EBusDevice(Byte address, EBusDeviceTable deviceTable) {

        this.deviceTable = deviceTable;

        if (EBusUtils.isMasterAddress(address)) {
            this.masterAddress = address;
            this.slaveAddress = EBusUtils.getSlaveAddress(address);
        } else {
            this.slaveAddress = address;
            this.masterAddress = EBusUtils.getMasterAddress(slaveAddress);
        }
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
        EBusDevice other = (EBusDevice) obj;
        if (!Arrays.equals(deviceId, other.deviceId)) {
            return false;
        }
        if (hardwareVersion == null) {
            if (other.hardwareVersion != null) {
                return false;
            }
        } else if (!hardwareVersion.equals(other.hardwareVersion)) {
            return false;
        }
        if (manufacturer != other.manufacturer) {
            return false;
        }
        if (masterAddress != other.masterAddress) {
            return false;
        }
        if (slaveAddress != other.slaveAddress) {
            return false;
        }
        if (softwareVersion == null) {
            if (other.softwareVersion != null) {
                return false;
            }
        } else if (!softwareVersion.equals(other.softwareVersion)) {
            return false;
        }
        return true;
    }

    @Override
    public byte[] getDeviceId() {
        return deviceId;
    }

    @Override
    public BigDecimal getHardwareVersion() {
        return hardwareVersion;
    }

    @Override
    public long getLastActivity() {
        return lastActivity;
    }

    @Override
    public Byte getManufacturer() {
        return manufacturer;
    }

    @Override
    public String getManufacturerName() {
        if (manufacturer != null) {
            return deviceTable.getManufacturerName(manufacturer);
        }

        return null;
    }

    @Override
    public Byte getMasterAddress() {
        return masterAddress;
    }

    @Override
    public Byte getSlaveAddress() {
        return slaveAddress;
    }

    @Override
    public BigDecimal getSoftwareVersion() {
        return softwareVersion;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(deviceId);
        result = prime * result + ((hardwareVersion == null) ? 0 : hardwareVersion.hashCode());
        result = prime * result + manufacturer;
        result = prime * result + masterAddress;
        result = prime * result + slaveAddress;
        result = prime * result + ((softwareVersion == null) ? 0 : softwareVersion.hashCode());
        return result;
    }

    public void setDeviceId(byte[] deviceId) {

        Objects.requireNonNull(deviceId, "deviceId");

        if(deviceId.length != 5) {
            throw new IllegalArgumentException("Argument 'deviceId' must be an array of 5 bytes!");
        }

        this.deviceId = deviceId;
    }

    public void setHardwareVersion(BigDecimal hardwareVersion) {
        Objects.requireNonNull(hardwareVersion, "hardwareVersion");
        this.hardwareVersion = hardwareVersion;
    }

    public void setLastActivity(long lastActivity) {
        this.lastActivity = lastActivity;
    }

    public void setManufacturer(Byte manufactur) {
        Objects.requireNonNull(manufactur, "manufactur");
        this.manufacturer = manufactur;
    }

    public void setSoftwareVersion(BigDecimal softwareVersion) {
        Objects.requireNonNull(softwareVersion, "softwareVersion");
        this.softwareVersion = softwareVersion;
    }

    @Override
    public String toString() {
        return "EBusDevice [masterAddress=0x" + EBusUtils.toHexDumpString(masterAddress) + ", slaveAddress=0x"
                + EBusUtils.toHexDumpString(slaveAddress) + ", lastActivity=" + lastActivity + ", manufacturer="
                + manufacturer + "(" + getManufacturerName() + "), deviceId="
                + EBusUtils.toHexDumpString(deviceId).toString() + ", softwareVersion=" + softwareVersion
                + ", hardwareVersion=" + hardwareVersion + "]";
    }

}