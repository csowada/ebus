package de.csdev.ebus.meta;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.aaa.EBusTelegramComposer;
import de.csdev.ebus.utils.EBusUtils;
import de.csdev.ebus.utils.NumberUtils;

public class EBusDeviceTable {

	private static final Logger logger = LoggerFactory.getLogger(EBusTelegramComposer.class);
	
	private Map<Byte, EBusDevice> deviceTable;

	/** the list for listeners */
	private final List<EBusDeviceTableListener> listeners = new ArrayList<EBusDeviceTableListener>();

	/** the address of this library */
	private byte ownAddress;
	
	public EBusDeviceTable(byte ownAddress) {
		this.ownAddress = ownAddress;
		
		deviceTable = new HashMap<Byte, EBusDevice>();
		
		EBusDevice d = new EBusDevice(ownAddress);
		deviceTable.put(d.getMasterAddress(), d);
	}
	
	public void updateDevice(byte address, Map<String, Object> data) {
		
		EBusDevice device = deviceTable.get(address);

		if(device == null) {
			device = new EBusDevice(address);
			deviceTable.put(device.getMasterAddress(), device);
			logger.info("New eBus device with master address 0x{} found ...", EBusUtils.toHexDumpString(device.getMasterAddress()));
		}

		if(data != null && !data.isEmpty()) {

			Object obj = data.get("common.identification.device");
			if(obj != null) device.setDeviceId((String)obj);

			BigDecimal obj2 =  NumberUtils.toBigDecimal(data.get("common.identification.hardware_version"));
			if(obj != null) device.setHardwareVersion(obj2);

			obj = NumberUtils.toBigDecimal(data.get("common.identification.software_version"));
			if(obj != null) device.setSoftwareVersion(obj2);

			obj = NumberUtils.toBigDecimal(data.get("common.identification.vendor"));
			if(obj != null) device.setVendor(obj2.byteValue());
		}

		// update activity
		device.setLastActivity(System.currentTimeMillis());
		fireOnTelegramResolved();
	}
	
	public Collection<EBusDevice> getDeviceTable() {
		return Collections.unmodifiableCollection(deviceTable.values());
	}
	
	private void fireOnTelegramResolved() {
		for (EBusDeviceTableListener listener : listeners) {
			//listener.onTelegramResolved();
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
