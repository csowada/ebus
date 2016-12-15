package de.csdev.ebus.cfg;

public interface IEBusDevice {

    public byte getMasterAddress();

    public byte getSlaveAddress();

    public byte getVendor();

    public String getDeviceId();

    public String getSoftwareVersion();

    public String getHardwareVersion();

}
