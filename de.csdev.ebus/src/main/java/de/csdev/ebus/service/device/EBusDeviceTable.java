/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.service.device;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.csdev.ebus.utils.EBusUtils;
import de.csdev.ebus.utils.NumberUtils;

/**
 * @author Christian Sowada
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

    public EBusDeviceTable(byte ownAddress) {
        this.ownAddress = ownAddress;

        deviceTable = new HashMap<Byte, EBusDevice>();

        EBusDevice d = new EBusDevice(ownAddress, this);
        deviceTable.put(d.getMasterAddress(), d);
    }

    public String getManufacturerName(byte vendorCode) {
        if (vendors == null) {

            try {
                final ObjectMapper mapper = new ObjectMapper();
                final InputStream inputStream = this.getClass().getResourceAsStream("/manufactures.json");

                vendors = mapper.readValue(inputStream, new TypeReference<Map<Integer, String>>() {
                });

                inputStream.close();

            } catch (JsonParseException e) {
                logger.error("error!", e);
            } catch (JsonMappingException e) {
                logger.error("error!", e);
            } catch (MalformedURLException e) {
                logger.error("error!", e);
            } catch (IOException e) {
                logger.error("error!", e);
            }
        }

        if (vendors == null) {
            logger.warn("Ups");
            return null;
        }

        return vendors.get(vendorCode & 0xFF);
    }

    public void updateDevice(byte address, Map<String, Object> data) {

        EBusDevice device = deviceTable.get(address);
        boolean isNewDevice = false;
        boolean isUpdate = false;

        if (device == null) {
            device = new EBusDevice(address, this);
            deviceTable.put(device.getMasterAddress(), device);
            logger.info("New eBus device with master address 0x{} found ...",
                    EBusUtils.toHexDumpString(device.getMasterAddress()));
            isNewDevice = true;
        }

        if (data != null && !data.isEmpty()) {

            Object obj = data.get("common.identification.device");
            if (obj != null) {
                device.setDeviceId((String) obj);
            }

            BigDecimal obj2 = NumberUtils.toBigDecimal(data.get("common.identification.hardware_version"));
            if (obj2 != null) {
                device.setHardwareVersion(obj2);
            }

            obj2 = NumberUtils.toBigDecimal(data.get("common.identification.software_version"));
            if (obj2 != null) {
                device.setSoftwareVersion(obj2);
            }

            obj2 = NumberUtils.toBigDecimal(data.get("common.identification.vendor"));
            if (obj2 != null) {
                int intValue = obj2.intValue();
                device.setManufacturer((byte) intValue);
            }

            isUpdate = true;
        }

        // update activity
        device.setLastActivity(System.currentTimeMillis());

        if (isNewDevice) {
            fireOnTelegramResolved(EBusDeviceTableListener.TYPE.NEW, device);
        } else if (isUpdate) {
            fireOnTelegramResolved(EBusDeviceTableListener.TYPE.UPDATE, device);
        }

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
