/**
 * Copyright (c) 2017-2021 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.service.device;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.command.EBusCommandException;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.core.EBusConnectorEventListener;
import de.csdev.ebus.core.EBusConsts;
import de.csdev.ebus.core.EBusControllerException;
import de.csdev.ebus.core.EBusDataException;
import de.csdev.ebus.core.IEBusController;
import de.csdev.ebus.core.IEBusController.ConnectionStatus;
import de.csdev.ebus.service.parser.IEBusParserListener;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
@NonNullByDefault
public class EBusDeviceTableService extends EBusConnectorEventListener
        implements IEBusParserListener, IEBusDeviceTableListener {

    private static final Logger logger = LoggerFactory.getLogger(EBusDeviceTableService.class);

    private @NonNull IEBusController controller;

    private @NonNull Integer scanQueueId = -1;

    private @NonNull EBusCommandRegistry configurationProvider;

    private @NonNull EBusDeviceTable deviceTable;

    private boolean disableIdentificationRequests = false;

    private byte scanSlaveAddress = 0;

    private boolean scanRunning = false;

    public EBusDeviceTableService(@NonNull IEBusController controller,
            @NonNull EBusCommandRegistry configurationProvider, @NonNull EBusDeviceTable deviceTable) {

        Objects.requireNonNull(controller);
        Objects.requireNonNull(configurationProvider);
        Objects.requireNonNull(deviceTable);

        this.controller = controller;
        this.configurationProvider = configurationProvider;
        this.deviceTable = deviceTable;
        this.controller.addEBusEventListener(this);
    }

    public boolean isDisableIdentificationRequests() {
        return disableIdentificationRequests;
    }

    public void setDisableIdentificationRequests(boolean disableIdentificationRequests) {
        this.disableIdentificationRequests = disableIdentificationRequests;
    }

    /**
     *
     */
    public void inquiryDeviceExistence() {

        if (scanQueueId != -1) {
            logger.debug("Inquiry is still in progress ...");
            return;
        }

        if (controller.getConnectionStatus() != ConnectionStatus.CONNECTED) {
            logger.debug("Skip eBUS scan due to connection issues ...");
            return;
        }

        logger.debug("Start eBUS scan  ...");

        byte masterAddress = deviceTable.getOwnDevice().getMasterAddress();

        IEBusCommandMethod command = configurationProvider.getCommandMethodById(EBusConsts.COLLECTION_STD,
                EBusConsts.COMMAND_INQ_EXISTENCE, IEBusCommandMethod.Method.BROADCAST);

        if (command == null) {
            throw new IllegalStateException("Unable to load command COMMAND_INQ_EXISTENCE!");
        }

        try {
            ByteBuffer buffer = EBusCommandUtils.buildMasterTelegram(command, masterAddress,
                    EBusConsts.BROADCAST_ADDRESS, null);

            scanQueueId = controller.addToSendQueue(EBusUtils.toByteArray(buffer), 2);
        } catch (EBusTypeException | EBusControllerException | EBusCommandException e) {
            logger.error(EBusConsts.LOG_ERR_DEF, e);
        }
    }

    public void startDeviceScan() {

        if (controller.getConnectionStatus() != ConnectionStatus.CONNECTED) {
            logger.debug("Skip eBUS device scan due to connection issues ...");
            return;
        }

        if (scanRunning) {
            logger.debug("eBUS scan is already running! Skip start ...");
            return;
        }

        if (scanQueueId != -1) {
            logger.debug("Inquiry is still in progress ...");
            return;
        }

        // first slave address
        scanSlaveAddress = 0x02;

        scanRunning = true;

        scanDevice2(false);
    }

    public void stopDeviceScan() {
        scanRunning = false;
    }

    private synchronized boolean scanDevice2(boolean nextDevice) {

        if (nextDevice) {
            Byte addr = nextSlaveAddress(scanSlaveAddress);

            if (addr == null) {
                return false;
            }

            scanSlaveAddress = addr;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Scan address {} ...", EBusUtils.toHexDumpString(scanSlaveAddress));
        }

        byte masterAddress = deviceTable.getOwnDevice().getMasterAddress();

        IEBusCommandMethod command = configurationProvider.getCommandMethodById(EBusConsts.COLLECTION_STD,
                EBusConsts.COMMAND_IDENTIFICATION, IEBusCommandMethod.Method.GET);

        if (command == null) {
            throw new IllegalStateException("Unable to load command COMMAND_IDENTIFICATION!");
        }

        try {
            ByteBuffer buffer = EBusCommandUtils.buildMasterTelegram(command, masterAddress, scanSlaveAddress, null);

            scanQueueId = controller.addToSendQueue(EBusUtils.toByteArray(buffer), 2);

            return true;

        } catch (EBusTypeException | EBusControllerException | EBusCommandException e) {
            logger.error(EBusConsts.LOG_ERR_DEF, e);
        }

        return false;
    }

    private @Nullable Byte nextSlaveAddress(byte slaveAddress) {

        if (slaveAddress == (byte) 0xFD) {
            return null;
        }

        do {
            slaveAddress++;
        } while (EBusUtils.isMasterAddress(slaveAddress) || slaveAddress == EBusConsts.BROADCAST_ADDRESS);

        return slaveAddress;
    }

    /**
     *
     */
    public void dispose() {
        this.controller.removeEBusEventListener(this);
    }

    /**
     *
     */
    private void sendSignOfLife() {
        byte masterAddress = deviceTable.getOwnDevice().getMasterAddress();
        IEBusCommandMethod command = configurationProvider.getCommandMethodById(EBusConsts.COLLECTION_STD,
                EBusConsts.COMMAND_SIGN_OF_LIFE, IEBusCommandMethod.Method.BROADCAST);

        if (command == null) {
            throw new IllegalStateException("Unable to load command COMMAND_SIGN_OF_LIFE!");
        }

        try {
            ByteBuffer buffer = EBusCommandUtils.buildMasterTelegram(command, masterAddress,
                    EBusConsts.BROADCAST_ADDRESS, null);

            controller.addToSendQueue(EBusUtils.toByteArray(buffer), 2);
        } catch (EBusTypeException | EBusControllerException | EBusCommandException e) {
            logger.error("error!", e);
        }
    }

    /**
     * @param slaveAddress
     */
    public void sendIdentificationRequest(byte slaveAddress) {

        if (controller.getConnectionStatus() != ConnectionStatus.CONNECTED) {
            logger.debug("Skip eBUS identification due to connection issues ...");
            return;
        }

        byte masterAddress = deviceTable.getOwnDevice().getMasterAddress();
        IEBusCommandMethod command = configurationProvider.getCommandMethodById(EBusConsts.COLLECTION_STD,
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
        } catch (EBusTypeException | EBusControllerException | EBusCommandException e) {
            logger.error("error!", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.EBusConnectorEventListener#onTelegramReceived(byte[],
     * java.lang.Integer)
     */
    @Override
    public void onTelegramReceived(byte @NonNull [] receivedData, @Nullable Integer sendQueueId) {

        deviceTable.updateDevice(receivedData[0], null);
        deviceTable.updateDevice(receivedData[1], null);

        if (sendQueueId != null && sendQueueId.equals(scanQueueId)) {

            if (scanRunning) {
                if (!scanDevice2(true)) {
                    stopDeviceScan();
                }

            } else {
                // inquiry
                logger.debug("Scan broadcast has been send out!");

            }

            scanQueueId = -1;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.csdev.ebus.core.EBusConnectorEventListener#onTelegramException(de.csdev.
     * ebus.core.EBusDataException, java.lang.Integer)
     */
    @Override
    public void onTelegramException(@NonNull EBusDataException exception, @Nullable Integer sendQueueId) {
        if (sendQueueId != null && sendQueueId.equals(scanQueueId)) {

            if (scanRunning) {
                if (!scanDevice2(true)) {
                    stopDeviceScan();
                }

            } else {
                logger.debug("Scan broadcast failed!");
            }

            scanQueueId = -1;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.csdev.ebus.service.parser.EBusParserListener#onTelegramResolved(de.csdev.
     * ebus.command.IEBusCommand, java.util.Map, byte[], java.lang.Integer)
     */
    @Override
    public void onTelegramResolved(@NonNull IEBusCommandMethod commandChannel,
            @NonNull Map<@NonNull String, @Nullable Object> result, byte @NonNull [] receivedData,
            @Nullable Integer sendQueueId) {

        String id = commandChannel.getParent().getId();
        Byte slaveAddress = receivedData[1];

        if (id.equals(EBusConsts.COMMAND_SIGN_OF_LIFE)) {
            deviceTable.updateDevice(slaveAddress, null);

        } else if (id.equals(EBusConsts.COMMAND_INQ_EXISTENCE)) {
            sendSignOfLife();

        } else if (id.equals(EBusConsts.COMMAND_IDENTIFICATION)) {
            deviceTable.updateDevice(slaveAddress, result);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.csdev.ebus.service.device.EBusDeviceTableListener#onEBusDeviceUpdate(de.
     * csdev.ebus.service.device. EBusDeviceTableListener.TYPE,
     * de.csdev.ebus.service.device.IEBusDevice)
     */
    @Override
    public void onEBusDeviceUpdate(IEBusDeviceTableListener.@NonNull TYPE type, @NonNull IEBusDevice device) {

        if (!type.equals(TYPE.UPDATE_ACTIVITY)) {
            logger.debug("DATA TABLE UPDATE {}", device);
        }

        // identify new devices
        if (type.equals(IEBusDeviceTableListener.TYPE.NEW) && !disableIdentificationRequests) {
            sendIdentificationRequest(device.getSlaveAddress());
        }
    }

    @Override
    public void onTelegramResolveFailed(@Nullable IEBusCommandMethod commandChannel, byte @Nullable [] receivedData,
            @Nullable Integer sendQueueId, @Nullable String exceptionMessage) {
        // noop
    }
}
