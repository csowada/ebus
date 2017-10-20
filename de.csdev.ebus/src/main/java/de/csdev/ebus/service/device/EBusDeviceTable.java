/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.service.device;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.csdev.ebus.command.IEBusCommandCollection;
import de.csdev.ebus.core.EBusConsts;
import de.csdev.ebus.utils.EBusUtils;
import de.csdev.ebus.utils.NumberUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusDeviceTable {

    private static final Logger logger = LoggerFactory.getLogger(EBusDeviceTable.class);

    private Map<Byte, EBusDevice> deviceTable;

    /** the list for listeners */
    private final List<IEBusDeviceTableListener> listeners = new CopyOnWriteArrayList<IEBusDeviceTableListener>();

    private Map<String, String> vendors;

    /** the address of this library */
    private byte ownAddress;

    public EBusDeviceTable() {
        deviceTable = new HashMap<Byte, EBusDevice>();
    }

    public void dispose() {
        if (listeners != null) {
            listeners.clear();
        }

        if (deviceTable != null) {
            deviceTable.clear();
        }

        if (vendors != null) {
            vendors.clear();
        }
    }

    public void setOwnAddress(byte ownAddress) {
        this.ownAddress = ownAddress;
        EBusDevice d = new EBusDevice(ownAddress, this);
        deviceTable.put(d.getMasterAddress(), d);
    }

    public String getManufacturerName(byte vendorCode) {

        // vendor list not loaded?
        if (vendors == null) {

            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();

            final InputStream inputStream = getClass().getResourceAsStream("/manufactures.json");

            vendors = gson.fromJson(new InputStreamReader(inputStream), type);
        }

        if (vendors == null) {
            logger.warn("Ups");
            return null;
        }

        return vendors.get(EBusUtils.toHexDumpString(vendorCode));
    }

    public void updateDevice(byte address, Map<String, Object> data) {

        boolean newDevice = false;
        boolean updatedDevice = false;

        if (address == EBusConsts.BROADCAST_ADDRESS) {
            return;
        } else if (EBusUtils.isMasterAddress(address)) {
            address = EBusUtils.getSlaveAddress(address);
        }

        EBusDevice device = deviceTable.get(address);

        if (device == null) {
            device = new EBusDevice(address, this);
            device.setLastActivity(System.currentTimeMillis());
            deviceTable.put(address, device);
            newDevice = true;
        }

        device.setLastActivity(System.currentTimeMillis());

        if (data != null && !data.isEmpty()) {

            Object obj = data.get("device_id");
            if (obj != null && !obj.equals(device.getDeviceId())) {
                device.setDeviceId((byte[]) obj);
                updatedDevice = true;
            }

            BigDecimal obj2 = NumberUtils.toBigDecimal(data.get("hardware_version"));
            if (obj2 != null && !ObjectUtils.equals(obj2, device.getHardwareVersion())) {
                device.setHardwareVersion(obj2);
                updatedDevice = true;
            }

            obj2 = NumberUtils.toBigDecimal(data.get("software_version"));
            if (obj2 != null && !ObjectUtils.equals(obj2, device.getSoftwareVersion())) {
                device.setSoftwareVersion(obj2);
                updatedDevice = true;
            }

            obj2 = NumberUtils.toBigDecimal(data.get("vendor"));
            if (obj2 != null && !ObjectUtils.equals(obj2.byteValue(), device.getManufacturer())) {
                int intValue = obj2.intValue();
                device.setManufacturer((byte) intValue);
                updatedDevice = true;
            }
        }

        if (newDevice) {
            fireOnDeviceUpdate(IEBusDeviceTableListener.TYPE.NEW, device);
        } else if (updatedDevice) {
            fireOnDeviceUpdate(IEBusDeviceTableListener.TYPE.UPDATE, device);
        } else {
            fireOnDeviceUpdate(IEBusDeviceTableListener.TYPE.UPDATE_ACTIVITY, device);
        }

        // if (data == null) {
        // if (device == null) {
        // device = new EBusDevice(address, this);
        // device.setLastActivity(System.currentTimeMillis());
        //
        // deviceTable.put(address, device);
        //
        // fireOnTelegramResolved(IEBusDeviceTableListener.TYPE.NEW, device);
        // return;
        // } else {
        // device.setLastActivity(System.currentTimeMillis());
        //
        // fireOnTelegramResolved(IEBusDeviceTableListener.TYPE.UPDATE_ACTIVITY, device);
        // return;
        // }
        // }
        //
        // if (!data.isEmpty()) {
        //
        // Object obj = data.get("device_id");
        // if (obj != null) {
        // device.setDeviceId((byte[]) obj);
        // }
        //
        // BigDecimal obj2 = NumberUtils.toBigDecimal(data.get("hardware_version"));
        // if (obj2 != null) {
        // device.setHardwareVersion(obj2);
        // }
        //
        // obj2 = NumberUtils.toBigDecimal(data.get("software_version"));
        // if (obj2 != null) {
        // device.setSoftwareVersion(obj2);
        // }
        //
        // obj2 = NumberUtils.toBigDecimal(data.get("vendor"));
        // if (obj2 != null) {
        // int intValue = obj2.intValue();
        // device.setManufacturer((byte) intValue);
        // }
        // }
        //
        // fireOnTelegramResolved(IEBusDeviceTableListener.TYPE.UPDATE, device);
    }

    public Collection<EBusDevice> getDeviceTable() {
        return Collections.unmodifiableCollection(deviceTable.values());
    }

    private void fireOnDeviceUpdate(IEBusDeviceTableListener.TYPE type, EBusDevice device) {
        for (IEBusDeviceTableListener listener : listeners) {
            listener.onEBusDeviceUpdate(type, device);
        }
    }

    public EBusDevice getOwnDevice() {
        return deviceTable.get(ownAddress);
    }

    /**
     * Add a listener
     *
     * @param listener
     */
    public void addEBusDeviceTableListener(IEBusDeviceTableListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a listener
     *
     * @param listener
     * @return
     */
    public boolean removeEBusDeviceTableListener(IEBusDeviceTableListener listener) {
        return listeners.remove(listener);
    }

    /**
     * @return
     */
    public String getDeviceTableInformation(Collection<IEBusCommandCollection> collections) {

        StringBuilder sb = new StringBuilder();

        Map<String, String> mapping = new HashMap<String, String>();

        for (IEBusCommandCollection collection : collections) {
            for (String identification : collection.getIdentification()) {
                mapping.put(identification, collection.getId());
            }
        }

        EBusDevice ownDevice = getOwnDevice();

        sb.append(String.format("%-2s | %-2s | %-14s | %-14s | %-20s | %-2s | %-10s | %-10s | %-20s\n", "MA", "SA",
                "Identifier", "Device", "Manufacture", "ID", "Firmware", "Hardware", "Last Activity"));

        sb.append(String.format("%-2s-+-%-2s-+-%-14s-+-%-14s-+-%-20s-+-%-2s-+-%-10s-+-%-10s-+-%-20s\n",
                StringUtils.repeat("-", 2), StringUtils.repeat("-", 2), StringUtils.repeat("-", 14),
                StringUtils.repeat("-", 14), StringUtils.repeat("-", 20), StringUtils.repeat("-", 2),
                StringUtils.repeat("-", 10), StringUtils.repeat("-", 10), StringUtils.repeat("-", 20)));

        for (EBusDevice device : deviceTable.values()) {

            boolean isBridge = device.equals(ownDevice);
            String masterAddress = EBusUtils.toHexDumpString(device.getMasterAddress());
            String slaveAddress = EBusUtils.toHexDumpString(device.getSlaveAddress());

            String activity = device.getLastActivity() == 0 ? "---" : new Date(device.getLastActivity()).toString();
            String id = EBusUtils.toHexDumpString(device.getDeviceId()).toString();
            String deviceName = isBridge ? "<interface>" : mapping.getOrDefault(id, "---");
            String manufacture = isBridge ? "eBUS Library" : device.getManufacturerName();

            sb.append(String.format("%-2s | %-2s | %-14s | %-14s | %-20s | %-2s | %-10s | %-10s | %-20s\n",
                    masterAddress, slaveAddress, id, deviceName, manufacture,
                    EBusUtils.toHexDumpString(device.getManufacturer()), device.getSoftwareVersion(),
                    device.getHardwareVersion(), activity));

        }

        sb.append(StringUtils.repeat("-", 118) + "\n");
        sb.append("MA = Master Address / SA = Slave Address / ID = Manufacture ID\n");

        return sb.toString();
    }

}
