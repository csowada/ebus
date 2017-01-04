package de.csdev.ebus.meta;

public interface EBusDeviceTableListener {

    public enum TYPE {
        NEW,
        UPDATE,
        REMOVED
    }

    public void onEBusDeviceUpdate(TYPE type, IEBusDevice device);

}
