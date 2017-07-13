/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.service.device;

import java.nio.ByteBuffer;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.IEBusCommand;
import de.csdev.ebus.command.IEBusCommand.Type;
import de.csdev.ebus.core.EBusConnectorEventListener;
import de.csdev.ebus.core.EBusConsts;
import de.csdev.ebus.core.EBusController;
import de.csdev.ebus.core.EBusDataException;
import de.csdev.ebus.service.parser.EBusParserListener;

/**
 * @author Christian Sowada
 *
 */
public class EBusDeviceTableService implements EBusConnectorEventListener, EBusParserListener, EBusDeviceTableListener {

    private static final Logger logger = LoggerFactory.getLogger(EBusDeviceTableService.class);

    private EBusController controller;

    private Integer scanQueueId = -1;

    private EBusCommandRegistry configurationProvider;

    private EBusDeviceTable deviceTable;

    public EBusDeviceTableService(EBusController controller, EBusCommandRegistry configurationProvider, EBusDeviceTable deviceTable) {

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

        IEBusCommand command = configurationProvider.getConfigurationById("common.inquiry_of_existence", Type.GET);

        ByteBuffer buffer = EBusCommandUtils.buildMasterTelegram(
        		command, masterAddress, EBusConsts.BROADCAST_ADDRESS, null);

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
        IEBusCommand command = configurationProvider.getConfigurationById("common.sign_of_life", Type.BROADCAST);

        ByteBuffer buffer = EBusCommandUtils.buildMasterTelegram(command, masterAddress, EBusConsts.BROADCAST_ADDRESS,
                null);

        controller.addToSendQueue(buffer);
    }

    public void sendIdentificationRequest(byte slaveAddress) {
        byte masterAddress = deviceTable.getOwnDevice().getMasterAddress();
        IEBusCommand command = configurationProvider.getConfigurationById("common.identification", Type.GET);

        if(command == null) {
        	logger.warn("Unable to load command with id common.identification");
        	return;
        }
        
        ByteBuffer buffer = EBusCommandUtils.buildMasterTelegram(command, masterAddress, slaveAddress, null);

        controller.addToSendQueue(buffer);
    }

    public void onTelegramReceived(byte[] receivedData, Integer sendQueueId) {

        deviceTable.updateDevice(receivedData[0], null);

        if (sendQueueId != null && sendQueueId.equals(scanQueueId)) {
            logger.warn("Scan broadcast has been send out!");
            scanQueueId = -1;
        }
    }

    public void onTelegramException(EBusDataException exception, Integer sendQueueId) {
        if (sendQueueId != null && sendQueueId.equals(scanQueueId)) {
            logger.warn("Scan broadcast failed!");
            scanQueueId = -1;
        }
    }

    public void onTelegramResolved(IEBusCommand command, Map<String, Object> result,
            byte[] receivedData, Integer sendQueueId) {

        String id = command.getId();
        byte masterAddress = receivedData[0];

        if (id.equals("common.sign_of_life")) {
            deviceTable.updateDevice(masterAddress, null);

        } else if (id.equals("common.inquiry_of_existence")) {
            sendSignOfLife();

        } else if (id.equals("common.identification")) {
            deviceTable.updateDevice(masterAddress, result);
        }
    }

    public void onEBusDeviceUpdate(TYPE type, IEBusDevice device) {

        logger.info("DATA TABLE UPDATE {}", device);

        // identify new devices
        if (type.equals(TYPE.NEW)) {
            sendIdentificationRequest(device.getSlaveAddress());
        }
    }
}
