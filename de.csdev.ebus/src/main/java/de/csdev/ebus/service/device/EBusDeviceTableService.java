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
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.core.EBusConsts;
import de.csdev.ebus.core.EBusController;
import de.csdev.ebus.core.EBusDataException;
import de.csdev.ebus.core.IEBusConnectorEventListener;
import de.csdev.ebus.service.parser.IEBusParserListener;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusDeviceTableService
        implements IEBusConnectorEventListener, IEBusParserListener, IEBusDeviceTableListener {

    private static final Logger logger = LoggerFactory.getLogger(EBusDeviceTableService.class);

    private EBusController controller;

    private Integer scanQueueId = -1;

    private EBusCommandRegistry configurationProvider;

    private EBusDeviceTable deviceTable;

    private boolean disableIdentificationRequests = false;

    public EBusDeviceTableService(EBusController controller, EBusCommandRegistry configurationProvider,
            EBusDeviceTable deviceTable) {

        this.controller = controller;
        this.configurationProvider = configurationProvider;
        this.deviceTable = deviceTable;
        this.controller.addEBusEventListener(this);
    }

    /**
     *
     */
    public void startDeviceScan() {

        if (scanQueueId != -1) {
            logger.warn("Scan is still in progress ...");
            return;
        }

        logger.info("Start scan process ...");

        byte masterAddress = deviceTable.getOwnDevice().getMasterAddress();

        IEBusCommandMethod command = configurationProvider.getConfigurationById(EBusConsts.COLLECTION_STD,
                EBusConsts.COMMAND_INQ_EXISTENCE, IEBusCommandMethod.Method.BROADCAST);

        try {
            ByteBuffer buffer = EBusCommandUtils.buildMasterTelegram(command, masterAddress,
                    EBusConsts.BROADCAST_ADDRESS, null);

            scanQueueId = controller.addToSendQueue(EBusUtils.toByteArray(buffer), 2);
        } catch (EBusTypeException e) {
            logger.error("error!", e);
        }
    }

    /**
     *
     */
    public void dispose() {
        this.controller.removeEBusEventListener(this);
        this.controller = null;
    }

    /**
     *
     */
    private void sendSignOfLife() {
        byte masterAddress = deviceTable.getOwnDevice().getMasterAddress();
        IEBusCommandMethod command = configurationProvider.getConfigurationById(EBusConsts.COLLECTION_STD,
                EBusConsts.COMMAND_SIGN_OF_LIFE, IEBusCommandMethod.Method.BROADCAST);

        try {
            ByteBuffer buffer = EBusCommandUtils.buildMasterTelegram(command, masterAddress,
                    EBusConsts.BROADCAST_ADDRESS, null);

            controller.addToSendQueue(EBusUtils.toByteArray(buffer), 2);
        } catch (EBusTypeException e) {
            logger.error("error!", e);
        }
    }

    /**
     * @param slaveAddress
     */
    public void sendIdentificationRequest(byte slaveAddress) {
        byte masterAddress = deviceTable.getOwnDevice().getMasterAddress();
        IEBusCommandMethod command = configurationProvider.getConfigurationById(EBusConsts.COLLECTION_STD,
                EBusConsts.COMMAND_IDENTIFICATION, IEBusCommandMethod.Method.GET);

        if (command == null) {
            logger.warn("Unable to load command with id common.identification");
            return;
        }

        if (EBusUtils.isMasterAddress(slaveAddress)) {
            logger.error("The given address is a master address, not a slave address!");
            return;
        }

        try {
            ByteBuffer buffer = EBusCommandUtils.buildMasterTelegram(command, masterAddress, slaveAddress, null);

            controller.addToSendQueue(EBusUtils.toByteArray(buffer), 2);
        } catch (EBusTypeException e) {
            logger.error("error!", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.EBusConnectorEventListener#onTelegramReceived(byte[], java.lang.Integer)
     */
    @Override
    public void onTelegramReceived(byte[] receivedData, Integer sendQueueId) {

        deviceTable.updateDevice(receivedData[0], null);

        if (sendQueueId != null && sendQueueId.equals(scanQueueId)) {
            logger.warn("Scan broadcast has been send out!");
            scanQueueId = -1;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.EBusConnectorEventListener#onTelegramException(de.csdev.ebus.core.EBusDataException,
     * java.lang.Integer)
     */
    @Override
    public void onTelegramException(EBusDataException exception, Integer sendQueueId) {
        if (sendQueueId != null && sendQueueId.equals(scanQueueId)) {
            logger.warn("Scan broadcast failed!");
            scanQueueId = -1;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.service.parser.EBusParserListener#onTelegramResolved(de.csdev.ebus.command.IEBusCommand,
     * java.util.Map, byte[], java.lang.Integer)
     */
    @Override
    public void onTelegramResolved(IEBusCommandMethod commandChannel, Map<String, Object> result, byte[] receivedData,
            Integer sendQueueId) {

        String id = commandChannel.getParent().getId();
        Byte masterAddress = null;

        if (EBusUtils.isMasterAddress(receivedData[1])) {
            masterAddress = receivedData[1];
        } else {
            masterAddress = EBusUtils.getMasterAddress(receivedData[1]);
        }

        if (id.equals(EBusConsts.COMMAND_SIGN_OF_LIFE)) {
            deviceTable.updateDevice(masterAddress, null);

        } else if (id.equals(EBusConsts.COMMAND_INQ_EXISTENCE)) {
            sendSignOfLife();

        } else if (id.equals(EBusConsts.COMMAND_IDENTIFICATION)) {
            deviceTable.updateDevice(masterAddress, result);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.service.device.EBusDeviceTableListener#onEBusDeviceUpdate(de.csdev.ebus.service.device.
     * EBusDeviceTableListener.TYPE, de.csdev.ebus.service.device.IEBusDevice)
     */
    @Override
    public void onEBusDeviceUpdate(IEBusDeviceTableListener.TYPE type, IEBusDevice device) {

        if (!type.equals(TYPE.UPDATE_ACTIVITY)) {
            logger.info("DATA TABLE UPDATE {}", device);
        }

        // identify new devices
        if (type.equals(IEBusDeviceTableListener.TYPE.NEW)) {
            if (!disableIdentificationRequests) {
                sendIdentificationRequest(device.getSlaveAddress());
            }
        }
    }

    @Override
    public void onConnectionException(Exception e) {
        // noop
    }
}
