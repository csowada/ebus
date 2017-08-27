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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.csdev.ebus.utils.NumberUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusDeviceTable {

    private static final Logger logger = LoggerFactory.getLogger(EBusDeviceTable.class);

    private Map<Byte, EBusDevice> deviceTable;

    /** the list for listeners */
    private final List<EBusDeviceTableListener> listeners = new ArrayList<EBusDeviceTableListener>();

    private Map<Integer, String> vendors;

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

        return vendors.get(vendorCode & 0xFF);
    }

    public void updateDevice(byte address, Map<String, Object> data) {

        // EBusDevice device = deviceTable.get(address);
        // boolean isNewDevice = false;
        // boolean isUpdate = false;
        //
        // if (device == null) {
        // logger.info("New eBus device found. [{}]", device);
        // device = new EBusDevice(address, this);
        // deviceTable.put(device.getMasterAddress(), device);
        // isNewDevice = true;
        // } else {
        // logger.info("Update eBus device. [{}]", device);
        //
        // }

        // if (data == null || data.isEmpty()) {
        //
        // EBusDevice device = deviceTable.get(address);
        // device.setLastActivity(System.currentTimeMillis());
        //
        // fireOnTelegramResolved(EBusDeviceTableListener.TYPE.UPDATE_ACTIVITY, device);
        // return;
        // }

        EBusDevice oldDevice = deviceTable.get(address);
        // EBusDevice newDevice = null;

        EBusDevice newDevice = new EBusDevice(address, this);
        newDevice.setLastActivity(System.currentTimeMillis());

        if (data != null && !data.isEmpty()) {

            Object obj = data.get("common.identification.device_id");
            if (obj != null) {
                newDevice.setDeviceId((byte[]) obj);
            }

            BigDecimal obj2 = NumberUtils.toBigDecimal(data.get("common.identification.hardware_version"));
            if (obj2 != null) {
                newDevice.setHardwareVersion(obj2);
            }

            obj2 = NumberUtils.toBigDecimal(data.get("common.identification.software_version"));
            if (obj2 != null) {
                newDevice.setSoftwareVersion(obj2);
            }

            obj2 = NumberUtils.toBigDecimal(data.get("common.identification.vendor"));
            if (obj2 != null) {
                int intValue = obj2.intValue();
                newDevice.setManufacturer((byte) intValue);
            }
        }

        deviceTable.put(address, newDevice);

        if (oldDevice == null) {
            fireOnTelegramResolved(EBusDeviceTableListener.TYPE.NEW, newDevice);
        } else if (oldDevice.equals(newDevice)) {
            fireOnTelegramResolved(EBusDeviceTableListener.TYPE.UPDATE_ACTIVITY, newDevice);
        } else {
            fireOnTelegramResolved(EBusDeviceTableListener.TYPE.UPDATE, newDevice);
        }

        // device.setLastActivity(System.currentTimeMillis());
        // deviceTable.put(address, device);
        //
        // if (oldDevice != null) {
        // if (device.equals(oldDevice)) {
        // if (oldDevice.getLastActivity() != device.getLastActivity()) {
        // fireOnTelegramResolved(EBusDeviceTableListener.TYPE.UPDATE_ACTIVITY, device);
        // }
        // } else {
        // fireOnTelegramResolved(EBusDeviceTableListener.TYPE.UPDATE, device);
        // }
        // } else {
        // fireOnTelegramResolved(EBusDeviceTableListener.TYPE.NEW, device);
        // }
        //
        // // update activity
        // device.setLastActivity(System.currentTimeMillis());

        // if (isNewDevice) {
        // fireOnTelegramResolved(EBusDeviceTableListener.TYPE.NEW, device);
        // } else if (isUpdate) {
        // fireOnTelegramResolved(EBusDeviceTableListener.TYPE.UPDATE, device);
        // }

    }

    public Collection<EBusDevice> getDeviceTable() {
        return Collections.unmodifiableCollection(deviceTable.values());
    }

    private void fireOnTelegramResolved(EBusDeviceTableListener.TYPE type, EBusDevice device) {
        for (EBusDeviceTableListener listener : listeners) {
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
    public void addEBusDeviceTableListener(EBusDeviceTableListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a listener
     *
     * @param listener
     * @return
     */
    public boolean removeEBusDeviceTableListener(EBusDeviceTableListener listener) {
        return listeners.remove(listener);
    }

}
