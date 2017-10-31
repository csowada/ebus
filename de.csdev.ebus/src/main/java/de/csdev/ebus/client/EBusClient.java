/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.client;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.IEBusCommandCollection;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.core.EBusController;
import de.csdev.ebus.core.IEBusConnectorEventListener;
import de.csdev.ebus.service.device.EBusDeviceTable;
import de.csdev.ebus.service.device.EBusDeviceTableService;
import de.csdev.ebus.service.device.IEBusDeviceTableListener;
import de.csdev.ebus.service.parser.EBusParserService;
import de.csdev.ebus.service.parser.IEBusParserListener;

/**
 * A high level implementation to use all features of this library.
 *
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusClient {

    private final Logger logger = LoggerFactory.getLogger(EBusClient.class);

    private EBusDeviceTable deviceTable;

    private EBusController controller;

    private EBusParserService resolverService;

    private EBusDeviceTableService deviceTableService;

    private EBusCommandRegistry commandRegistry;

    // private EBusClientConfiguration configuration;

    /**
     * Disposes the eBUS client with all dependend services
     */
    public void dispose() {
        controller.interrupt();

        if (commandRegistry != null) {
            commandRegistry = null;
        }

        if (deviceTableService != null) {
            deviceTableService.dispose();
            deviceTableService = null;
        }

        if (deviceTable != null) {
            deviceTable.dispose();
            deviceTable = null;
        }

        if (resolverService != null) {
            resolverService.dispose();
            resolverService = null;
        }
    }

    // /**
    // * Creates a new eBUS client
    // */
    // public EBusClient() {
    // this(new EBusClientConfiguration());
    // }

    /**
     * Creates a new eBUS client with given configuration
     *
     * @param configuration
     */
    public EBusClient(EBusCommandRegistry commandRegistry) {

        this.commandRegistry = commandRegistry;

        deviceTable = new EBusDeviceTable();
        resolverService = new EBusParserService(commandRegistry);
    }

    /**
     * Initialize the eBUS client
     *
     * @param controller
     * @param masterAddress
     */
    public void connect(EBusController controller, byte masterAddress) {

        this.controller = controller;

        this.controller.addEBusEventListener(resolverService);

        deviceTable.setOwnAddress(masterAddress);

        deviceTableService = new EBusDeviceTableService(controller, commandRegistry, deviceTable);

        resolverService.addEBusParserListener(deviceTableService);
        deviceTable.addEBusDeviceTableListener(deviceTableService);
    }

    /**
     * Builds a raw telegram based on the given command method
     *
     * @param commandMethod
     * @param destinationAddress The eBUS slave address
     * @return Returns the raw telegram or <code>null</code> if there is a problem.
     * @throws EBusTypeException
     */
    public ByteBuffer buildTelegram(IEBusCommandMethod commandMethod, Byte destinationAddress,
            Map<String, Object> values) throws EBusTypeException {

        if (destinationAddress == null) {
            logger.warn("No destination address defined!");
            return null;
        }

        if (commandMethod == null) {
            logger.warn("Command method is null!");
            return null;
        }

        final byte masterAddress = getDeviceTable().getOwnDevice().getMasterAddress();
        return EBusCommandUtils.buildMasterTelegram(commandMethod, masterAddress, destinationAddress, values);
    }

    /**
     * Return the data type registry
     *
     * @return
     */
    public EBusTypeRegistry getDataTypes() {
        return commandRegistry.getTypeRegistry();
    }

    /**
     * Returns the eBUS controller
     *
     * @return
     */
    public EBusController getController() {
        return controller;
    }

    /**
     * Returns the eBUS configuration
     *
     * @return
     */
    public EBusCommandRegistry getConfigurationProvider() {
        return commandRegistry;
    }

    /**
     * Returns the Resolver Service (resolves the raw telegrams to result maps)
     *
     * @return
     */
    public EBusParserService getResolverService() {
        return resolverService;
    }

    /**
     * Returns the device table service
     *
     * @return
     */
    public EBusDeviceTableService getDeviceTableService() {
        return deviceTableService;
    }

    /**
     * Returns the device table
     *
     * @return
     */
    public EBusDeviceTable getDeviceTable() {
        return deviceTable;
    }

    /**
     * Add a listener
     *
     * @param listener
     * @see de.csdev.ebus.service.device.EBusDeviceTable#addEBusDeviceTableListener(IEBusDeviceTableListener)
     */
    public void addEBusDeviceTableListener(IEBusDeviceTableListener listener) {
        getDeviceTable().addEBusDeviceTableListener(listener);
    }

    /**
     * Remove a listener
     *
     * @param listener
     * @return
     * @see de.csdev.ebus.service.device.EBusDeviceTable#removeEBusDeviceTableListener(IEBusDeviceTableListener)
     */
    public boolean removeEBusDeviceTableListener(IEBusDeviceTableListener listener) {
        return getDeviceTable().removeEBusDeviceTableListener(listener);
    }

    /**
     * Add an eBus listener to receive valid eBus telegrams
     *
     * @param listener
     * @see de.csdev.ebus.core.EBusControllerBase#addEBusEventListener(IEBusConnectorEventListener)
     */
    public void addEBusEventListener(IEBusConnectorEventListener listener) {
        getController().addEBusEventListener(listener);
    }

    /**
     * Remove an eBUS listener
     *
     * @param listener
     * @return
     * @see de.csdev.ebus.core.EBusControllerBase#removeEBusEventListener(IEBusConnectorEventListener)
     */
    public boolean removeEBusEventListener(IEBusConnectorEventListener listener) {
        return getController().removeEBusEventListener(listener);
    }

    /**
     * Add an eBUS listener to receive parsed eBUS telegram values
     *
     * @param listener
     * @see de.csdev.ebus.client.EBusClient#addEBusParserListener(IEBusParserListener)
     */
    public void addEBusParserListener(IEBusParserListener listener) {
        getResolverService().addEBusParserListener(listener);
    }

    /**
     * Remove an eBUS listener
     *
     * @param listener
     * @return
     * @see de.csdev.ebus.client.EBusClient#removeEBusParserListener(IEBusParserListener)
     */
    public boolean removeEBusParserListener(IEBusParserListener listener) {
        return getResolverService().removeEBusParserListener(listener);
    }

    /**
     * Add a raw telegram to send queue
     *
     * @param buffer
     * @param maxAttemps
     * @return
     * @see de.csdev.ebus.core.EBusController#addToSendQueue(byte[], int)
     */
    public Integer addToSendQueue(byte[] buffer, int maxAttemps) {
        return getController().addToSendQueue(buffer, maxAttemps);
    }

    /**
     * Add a raw telegram to send queue
     *
     * @param buffer
     * @return
     * @see de.csdev.ebus.core.EBusController#addToSendQueue(byte[])
     */
    public Integer addToSendQueue(byte[] buffer) {
        return getController().addToSendQueue(buffer);
    }

    /**
     * @return
     */
    public Collection<IEBusCommandCollection> getCommandCollections() {
        return getConfigurationProvider().getCommandCollections();
    }

    /**
     * @param id
     * @return
     */
    public IEBusCommandCollection getCommandCollection(String id) {
        return getConfigurationProvider().getCommandCollection(id);
    }
}
