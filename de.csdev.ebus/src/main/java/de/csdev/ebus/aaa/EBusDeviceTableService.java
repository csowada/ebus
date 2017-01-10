/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.aaa;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.EBusConfigurationProvider;
import de.csdev.ebus.cfg.EBusConfigurationTelegram;
import de.csdev.ebus.core.EBusConnectorEventListener;
import de.csdev.ebus.core.EBusConsts;
import de.csdev.ebus.core.EBusController;
import de.csdev.ebus.core.EBusDataException;
import de.csdev.ebus.meta.EBusDeviceTable;
import de.csdev.ebus.meta.EBusDeviceTableListener;
import de.csdev.ebus.meta.IEBusDevice;

/**
 * @author Christian Sowada
 *
 */
public class EBusDeviceTableService implements EBusConnectorEventListener, EBusParserListener, EBusDeviceTableListener {

    private static final Logger logger = LoggerFactory.getLogger(EBusDeviceTableService.class);

    private EBusController controller;

    private Integer scanQueueId = -1;

    private EBusConfigurationProvider configurationProvider;

    private EBusDeviceTable deviceTable;

    public EBusDeviceTableService(EBusController controller, EBusConfigurationProvider configurationProvider,
            EBusDeviceTable deviceTable) {

        this.controller = controller;
        this.configurationProvider = configurationProvider;
        this.deviceTable = deviceTable;
        this.controller.addEBusEventListener(this);
    }

    public void startDeviceScan() {

        if (scanQueueId != -1) {
            logger.warn("Scan is still in progress ...");
            return;
        }

        logger.info("Start scan process ...");

        byte masterAddress = deviceTable.getOwnDevice().getMasterAddress();

        EBusConfigurationTelegram command = configurationProvider.getCommandById("common.inquiry_of_existence");
        byte[] buffer = EBusTelegramComposer.composeEBusTelegram(command, EBusConsts.BROADCAST_ADDRESS, masterAddress,
                null);

        scanQueueId = controller.addToSendQueue(buffer);
    }

    /**
     *
     */
    public void close() {
        this.controller.removeEBusEventListener(this);
        this.controller = null;
    }

    private void sendSignOfLife() {
        byte masterAddress = deviceTable.getOwnDevice().getMasterAddress();
        EBusConfigurationTelegram command = configurationProvider.getCommandById("common.sign_of_life");

        byte[] buffer = EBusTelegramComposer.composeEBusTelegram(command, EBusConsts.BROADCAST_ADDRESS, masterAddress,
                null);

        controller.addToSendQueue(buffer);
    }

    public void sendIdentificationRequest(byte slaveAddress) {
        byte masterAddress = deviceTable.getOwnDevice().getMasterAddress();
        EBusConfigurationTelegram command = configurationProvider.getCommandById("common.identification");

        byte[] buffer = EBusTelegramComposer.composeEBusTelegram(command, slaveAddress, masterAddress, null);

        controller.addToSendQueue(buffer);
    }

    @Override
    public void onTelegramReceived(byte[] receivedData, Integer sendQueueId) {

        deviceTable.updateDevice(receivedData[0], null);

        if (sendQueueId != null && sendQueueId.equals(scanQueueId)) {
            logger.warn("Scan broadcast has been send out!");
            scanQueueId = -1;
        }
    }

    @Override
    public void onTelegramException(EBusDataException exception, Integer sendQueueId) {
        if (sendQueueId != null && sendQueueId.equals(scanQueueId)) {
            logger.warn("Scan broadcast failed!");
            scanQueueId = -1;
        }
    }

    @Override
    public void onTelegramResolved(EBusConfigurationTelegram registryEntry, Map<String, Object> result,
            byte[] receivedData, Integer sendQueueId) {

        String id = registryEntry.getFullId();
        byte masterAddress = receivedData[0];

        if (id.equals("common.sign_of_life")) {
            deviceTable.updateDevice(masterAddress, null);

        } else if (id.equals("common.inquiry_of_existence")) {
            sendSignOfLife();

        } else if (id.equals("common.identification") || id.equals("common.identification_broadcast")) {
            deviceTable.updateDevice(masterAddress, result);
        }
    }

    @Override
    public void onEBusDeviceUpdate(TYPE type, IEBusDevice device) {

        logger.info("DATA TABLE UPDATE {}", device);

        // identify new devices
        if (type.equals(TYPE.NEW)) {
            sendIdentificationRequest(device.getSlaveAddress());
        }
    }
}
