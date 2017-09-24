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
import java.util.Arrays;

import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusDevice implements IEBusDevice {

    private byte[] deviceId;

    private EBusDeviceTable deviceTable;

    private BigDecimal hardwareVersion;

    public long lastActivity;

    private byte manufacturer;

    byte masterAddress;

    byte slaveAddress;

    private BigDecimal softwareVersion;

    public EBusDevice(byte masterAddress, EBusDeviceTable deviceTable) {
        this.masterAddress = masterAddress;
        this.deviceTable = deviceTable;
        this.slaveAddress = EBusUtils.getSlaveAddress(masterAddress);
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

    public byte[] getDeviceId() {
        return deviceId;
    }

    public BigDecimal getHardwareVersion() {
        return hardwareVersion;
    }

    public long getLastActivity() {
        return lastActivity;
    }

    public Byte getManufacturer() {
        return manufacturer;
    }

    public String getManufacturerName() {
        return deviceTable.getManufacturerName(manufacturer);
    }

    public Byte getMasterAddress() {
        return masterAddress;
    }

    public Byte getSlaveAddress() {
        return slaveAddress;
    }

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
        this.deviceId = deviceId;
    }

    public void setHardwareVersion(BigDecimal hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public void setLastActivity(long lastActivity) {
        this.lastActivity = lastActivity;
    }

    public void setManufacturer(byte manufactur) {
        this.manufacturer = manufactur;
    }

    public void setSoftwareVersion(BigDecimal softwareVersion) {
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