/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final List<IEBusDeviceTableListener> listeners = new CopyOnWriteArrayList<>();

    private Map<String, String> vendors;

    /** the address of this library */
    private byte ownAddress;

    public EBusDeviceTable() {
        deviceTable = new HashMap<>();
    }

    /**
     * 
     */
    public void dispose() {
        listeners.clear();

        if (deviceTable != null) {
            deviceTable.clear();
        }

        if (vendors != null) {
            vendors.clear();
        }
    }

    /**
     * 
     * @param ownAddress
     */
    public void setOwnAddress(final byte ownAddress) {
        this.ownAddress = ownAddress;
        EBusDevice d = new EBusDevice(ownAddress, this);
        deviceTable.put(d.getMasterAddress(), d);
    }

    /**
     * 
     * @param vendorCode
     * @return
     */
    public String getManufacturerName(final byte vendorCode) {

        // vendor list not loaded?
        if (vendors == null) {

            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();

            final InputStream inputStream = getClass().getResourceAsStream("/manufactures.json");

            vendors = gson.fromJson(new InputStreamReader(inputStream), type);
        }

        if (vendors == null) {
            logger.warn("Unable to load manufactures.json!");
            return null;
        }

        return vendors.get(EBusUtils.toHexDumpString(vendorCode));
    }

    /**
     * 
     * @param address
     * @param data
     */
    @SuppressWarnings("java:S3776")
    public void updateDevice(final byte address, final Map<@NonNull String, @Nullable Object> data) {

        boolean newDevice = false;
        boolean updatedDevice = false;
        byte addr = address;

        if (addr == EBusConsts.BROADCAST_ADDRESS) {
            return;
        } else if (EBusUtils.isMasterAddress(addr)) {
            Byte result = EBusUtils.getSlaveAddress(addr);

            if (result == null) {
                throw new IllegalArgumentException(
                        String.format("Given slave address %s is invalid!", EBusUtils.toHexDumpString(addr)));
            }

            addr = result;
        }

        if (addr == ownAddress) {
            // ignore own address
            return;
        }

        EBusDevice device = deviceTable.get(addr);

        if (device == null) {
            device = new EBusDevice(addr, this);
            device.setLastActivity(System.currentTimeMillis());
            deviceTable.put(addr, device);
            newDevice = true;
        }

        device.setLastActivity(System.currentTimeMillis());

        if (data != null && !data.isEmpty()) {

            if (data.containsKey("device_id")) {
                @Nullable
                Object obj = data.get("device_id");
                if (obj != null && !obj.equals(device.getDeviceId())) {
                    if (obj instanceof byte[]) {
                        device.setDeviceId((byte[]) obj);
                        updatedDevice = true;
                    }
                }
            }

            @Nullable
            Object value = data.get("hardware_version");
            if (value != null) {
                BigDecimal obj2 = NumberUtils.toBigDecimal(value);
                if (obj2 != null && !Objects.equals(obj2, device.getHardwareVersion())) {
                    device.setHardwareVersion(obj2);
                    updatedDevice = true;
                }
            }

            value = data.get("software_version");
            if (value != null) {
                BigDecimal obj2 = NumberUtils.toBigDecimal(value);
                if (obj2 != null && !Objects.equals(obj2, device.getSoftwareVersion())) {
                    device.setSoftwareVersion(obj2);
                    updatedDevice = true;
                }
            }

            value = data.get("vendor");
            if (value != null) {
                BigDecimal obj2 = NumberUtils.toBigDecimal(value);
                if (obj2 != null && !Objects.equals(obj2.byteValue(), device.getManufacturer())) {
                    int intValue = obj2.intValue();
                    device.setManufacturer((byte) intValue);
                    updatedDevice = true;
                }
            }
        }

        if (newDevice) {
            fireOnDeviceUpdate(IEBusDeviceTableListener.TYPE.NEW, device);
        } else if (updatedDevice) {
            fireOnDeviceUpdate(IEBusDeviceTableListener.TYPE.UPDATE, device);
        } else {
            fireOnDeviceUpdate(IEBusDeviceTableListener.TYPE.UPDATE_ACTIVITY, device);
        }
    }

    /**
     * 
     * @return
     */
    public Collection<EBusDevice> getDeviceTable() {
        return Collections.unmodifiableCollection(deviceTable.values());
    }

    /**
     * 
     * @param type
     * @param device
     */
    private void fireOnDeviceUpdate(final IEBusDeviceTableListener.@NonNull TYPE type, final @NonNull EBusDevice device) {
        for (IEBusDeviceTableListener listener : listeners) {
            try {
                listener.onEBusDeviceUpdate(type, device);
            } catch (Exception e) {
                logger.error("Error while firing onEBusDeviceUpdate events!", e);
            }
        }
    }

    /**
     * 
     * @return
     */
    public EBusDevice getOwnDevice() {
        return deviceTable.get(ownAddress);
    }

    /**
     * Add a listener
     *
     * @param listener
     */
    public void addEBusDeviceTableListener(final IEBusDeviceTableListener listener) {
        Objects.requireNonNull(listener);
        listeners.add(listener);
    }

    /**
     * Remove a listener
     *
     * @param listener
     * @return
     */
    public boolean removeEBusDeviceTableListener(final IEBusDeviceTableListener listener) {
        Objects.requireNonNull(listener);
        return listeners.remove(listener);
    }

}
