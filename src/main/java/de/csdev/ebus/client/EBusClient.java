/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
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
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import de.csdev.ebus.command.EBusCommandException;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.IEBusCommandCollection;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.core.EBusControllerException;
import de.csdev.ebus.core.IEBusConnectorEventListener;
import de.csdev.ebus.core.IEBusController;
import de.csdev.ebus.service.device.EBusDeviceTable;
import de.csdev.ebus.service.device.EBusDeviceTableService;
import de.csdev.ebus.service.device.IEBusDeviceTableListener;
import de.csdev.ebus.service.metrics.EBusMetricsService;
import de.csdev.ebus.service.parser.EBusParserService;
import de.csdev.ebus.service.parser.IEBusParserListener;

/**
 * A high level implementation to use all features of this library.
 *
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusClient {

    private static final String LABEL_LISTENER = "listener";

    private @NonNull EBusCommandRegistry commandRegistry;

    private @Nullable IEBusController controller;

    private @NonNull EBusDeviceTable deviceTable;

    private @Nullable EBusDeviceTableService deviceTableService;

    private @NonNull EBusMetricsService metricsService;

    private @NonNull EBusParserService resolverService;

    /**
     * Creates a new eBUS client with given configuration
     *
     * @param commandRegistry
     */
    public EBusClient(final @NonNull EBusCommandRegistry commandRegistry) {

        Objects.requireNonNull(commandRegistry);

        this.commandRegistry = commandRegistry;

        resolverService = new EBusParserService(commandRegistry);

        metricsService = new EBusMetricsService();

        deviceTable = new EBusDeviceTable();
    }

    /**
     * Add a listener
     *
     * @param listener
     * @see de.csdev.ebus.service.device.EBusDeviceTable#addEBusDeviceTableListener(IEBusDeviceTableListener)
     */
    public void addEBusDeviceTableListener(final @NonNull IEBusDeviceTableListener listener) {
        Objects.requireNonNull(listener, LABEL_LISTENER);
        deviceTable.addEBusDeviceTableListener(listener);
    }

    /**
     * Add an eBus listener to receive valid eBus telegrams
     *
     * @param listener
     * @see de.csdev.ebus.core.EBusControllerBase#addEBusEventListener(IEBusConnectorEventListener)
     */
    public void addEBusEventListener(final @NonNull IEBusConnectorEventListener listener) {
        Objects.requireNonNull(listener, LABEL_LISTENER);
        IEBusController ctrl = this.controller;

        if (ctrl != null) {
            ctrl.addEBusEventListener(listener);
        } else {
            throw new IllegalStateException("Controller is not initialized!");
        }
    }

    /**
     * Add an eBUS listener to receive parsed eBUS telegram values
     *
     * @param listener
     * @see de.csdev.ebus.client.EBusClient#addEBusParserListener(IEBusParserListener)
     */
    public void addEBusParserListener(final @NonNull IEBusParserListener listener) {
        Objects.requireNonNull(listener, LABEL_LISTENER);
        resolverService.addEBusParserListener(listener);
    }

    /**
     * Add a raw telegram to send queue
     *
     * @param buffer
     * @return
     * @throws EBusControllerException
     * @see de.csdev.ebus.core.EBusLowLevelController#addToSendQueue(byte[])
     */
    public @NonNull Integer addToSendQueue(final byte @NonNull [] buffer) throws EBusControllerException {

        Objects.requireNonNull(buffer, "buffer");
        IEBusController ctrl = this.controller;

        if (ctrl == null) {
            throw new EBusControllerException("Controller not set!");
        }

        return ctrl.addToSendQueue(buffer);
    }

    /**
     * Add a raw telegram to send queue
     *
     * @param buffer
     * @param maxAttemps
     * @return
     * @throws EBusControllerException
     * @see de.csdev.ebus.core.EBusLowLevelController#addToSendQueue(byte[], int)
     */
    public @NonNull Integer addToSendQueue(final byte @NonNull [] buffer, final int maxAttemps) throws EBusControllerException {

        IEBusController ctrl = this.controller;

        if (ctrl == null) {
            throw new EBusControllerException("Controller not set!");
        }

        return ctrl.addToSendQueue(buffer, maxAttemps);
    }

    /**
     * Builds a raw telegram based on the given command method
     *
     * @param commandMethod
     * @param destinationAddress The eBUS slave address
     * @return Returns the raw telegram or <code>null</code> if there is a problem.
     * @throws EBusTypeException
     * @throws EBusCommandException
     */
    public @NonNull ByteBuffer buildTelegram(final @NonNull IEBusCommandMethod commandMethod,
        final @NonNull Byte destinationAddress, final @Nullable Map<String, Object> values)
            throws EBusTypeException, EBusCommandException {

        Objects.requireNonNull(commandMethod, "commandMethod");
        Objects.requireNonNull(destinationAddress, "destinationAddress");

        final byte masterAddress = getDeviceTable().getOwnDevice().getMasterAddress();
        return EBusCommandUtils.buildMasterTelegram(commandMethod, masterAddress, destinationAddress, values);
    }

    /**
     * Initialize the eBUS client
     *
     * @param controller
     * @param masterAddress
     */
    public void connect(final @NonNull IEBusController controller, final byte masterAddress) {

        Objects.requireNonNull(controller, "controller");

        controller.addEBusEventListener(resolverService);

        deviceTable.setOwnAddress(masterAddress);
        
        // add device table service
        EBusDeviceTableService lDeviceTableService = new EBusDeviceTableService(controller, commandRegistry, deviceTable);
        resolverService.addEBusParserListener(lDeviceTableService);
        deviceTable.addEBusDeviceTableListener(lDeviceTableService);

        // add metrics service
        controller.addEBusEventListener(metricsService);
        resolverService.addEBusParserListener(metricsService);

        this.controller = controller;
        this.deviceTableService = lDeviceTableService;
    }

    /**
     * Disposes the eBUS client with all depended services
     */
    public void dispose() {

        IEBusController ctrl = this.controller;
        if (ctrl != null) {
            ctrl.interrupt();
            this.controller = null;
        }

        EBusDeviceTableService lDeviceTableService = this.deviceTableService;
        if (lDeviceTableService != null) {
            lDeviceTableService.dispose();
            this.deviceTableService = null;
        }

        if (deviceTable != null) {
            deviceTable.dispose();
        }

        if (resolverService != null) {
            resolverService.dispose();
        }
    }

    /**
     * @param id
     * @return
     */
    public @Nullable IEBusCommandCollection getCommandCollection(final @NonNull String id) {
        Objects.requireNonNull(id);
        return getConfigurationProvider().getCommandCollection(id);
    }

    /**
     * @return
     */
    public @NonNull Collection<@NonNull IEBusCommandCollection> getCommandCollections() {
        return getConfigurationProvider().getCommandCollections();
    }

    /**
     * Returns the eBUS configuration
     *
     * @return
     */
    public @NonNull EBusCommandRegistry getConfigurationProvider() {
        return commandRegistry;
    }

    /**
     * Returns the eBUS controller
     *
     * @return
     */
    public @Nullable IEBusController getController() {
        return controller;
    }

    /**
     * Returns the device table
     *
     * @return
     */
    public @NonNull EBusDeviceTable getDeviceTable() {
        return deviceTable;
    }

    /**
     * Returns the device table service
     *
     * @return
     */
    public @Nullable EBusDeviceTableService getDeviceTableService() {
        return deviceTableService;
    }

    /**
     * Returns the metrics service
     *
     * @return
     */
    public @NonNull EBusMetricsService getMetricsService() {
        return metricsService;
    }

    /**
     * Returns the Resolver Service (resolves the raw telegrams to result maps)
     *
     * @return
     */
    public @NonNull EBusParserService getResolverService() {
        return resolverService;
    }

    /**
     * Remove a listener
     *
     * @param listener
     * @return
     * @see de.csdev.ebus.service.device.EBusDeviceTable#removeEBusDeviceTableListener(IEBusDeviceTableListener)
     */
    public boolean removeEBusDeviceTableListener(final @NonNull IEBusDeviceTableListener listener) {
        Objects.requireNonNull(listener);
        return getDeviceTable().removeEBusDeviceTableListener(listener);
    }

    /**
     * Remove an eBUS listener
     *
     * @param listener
     * @return
     * @see de.csdev.ebus.core.EBusControllerBase#removeEBusEventListener(IEBusConnectorEventListener)
     */
    public boolean removeEBusEventListener(final @NonNull IEBusConnectorEventListener listener) {
        Objects.requireNonNull(listener);

        IEBusController ctrl = this.controller;

        if (ctrl != null) {
            return ctrl.removeEBusEventListener(listener);
        }

        return false;
    }

    /**
     * Remove an eBUS listener
     *
     * @param listener
     * @return
     * @see de.csdev.ebus.client.EBusClient#removeEBusParserListener(IEBusParserListener)
     */
    public boolean removeEBusParserListener(final @NonNull IEBusParserListener listener) {
        Objects.requireNonNull(listener);
        return getResolverService().removeEBusParserListener(listener);
    }
}
