/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.service.parser;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.EBusCommandUtils;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.core.EBusConnectorEventListener;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusParserService extends EBusConnectorEventListener {

    private static final Logger logger = LoggerFactory.getLogger(EBusParserService.class);

    /** the list for listeners */
    private final @NonNull List<IEBusParserListener> listeners = new CopyOnWriteArrayList<>();

    /** */
    private @NonNull EBusCommandRegistry commandRegistry;

    /**
     * @param configurationProvider
     */
    public EBusParserService(@NonNull EBusCommandRegistry configurationProvider) {
        Objects.requireNonNull(configurationProvider);
        this.commandRegistry = configurationProvider;
    }

    /**
     *
     */
    public void dispose() {
        listeners.clear();
    }

    /**
     * Add an eBUS listener to receive parsed eBUS telegram values
     *
     * @param listener
     */
    public void addEBusParserListener(@NonNull IEBusParserListener listener) {
        Objects.requireNonNull(listener);
        listeners.add(listener);
    }

    /**
     * Remove an eBUS listener
     *
     * @param listener
     * @return
     */
    public boolean removeEBusParserListener(@NonNull IEBusParserListener listener) {
        Objects.requireNonNull(listener);
        return listeners.remove(listener);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.core.EBusConnectorEventListener#onTelegramReceived(byte[], java.lang.Integer)
     */
    @Override
    public void onTelegramReceived(byte @NonNull [] receivedData, @Nullable Integer sendQueueId) {

        final List<IEBusCommandMethod> commandChannelList = commandRegistry.find(receivedData);

        if (commandChannelList.isEmpty()) {
            if (logger.isTraceEnabled()) {
                logger.trace("No command method matches the telegram {} ...", EBusUtils.toHexDumpString(receivedData));
            }
            fireOnTelegramFailed(null, receivedData, sendQueueId, "No command method matches the telegram!");
            return;
        }

        if (!commandChannelList.isEmpty()) {
            for (IEBusCommandMethod commandChannel : commandChannelList) {

                try {
                    if (commandChannel != null) {
                        Map<String, Object> map = EBusCommandUtils.decodeTelegram(commandChannel, receivedData);
                        fireOnTelegramResolved(commandChannel, map, receivedData, sendQueueId);
                    }
                } catch (EBusTypeException e) {
                    fireOnTelegramFailed(commandChannel, receivedData, sendQueueId, e.getMessage());
                    logger.error("Parsing error details >> Data: {} - {} {}", EBusUtils.toHexDumpString(receivedData),
                            commandChannel.getParent(), commandChannel.getType());
                    logger.error("error!", e);
                }
            }
        }

    }

    /**
     * @param commandChannel
     * @param result
     * @param receivedData
     * @param sendQueueId
     */
    private void fireOnTelegramResolved(@NonNull IEBusCommandMethod commandChannel,
            @NonNull Map<@NonNull String, @Nullable Object> result, byte @NonNull [] receivedData,
            @Nullable Integer sendQueueId) {

        for (IEBusParserListener listener : listeners) {
            try {
                listener.onTelegramResolved(commandChannel, result, receivedData, sendQueueId);
            } catch (Exception e) {
                logger.error("Error while firing onTelegramResolved events!", e);
            }
        }
    }

    /**
     * @param commandChannel
     * @param result
     * @param receivedData
     * @param sendQueueId
     */
    private void fireOnTelegramFailed(@Nullable IEBusCommandMethod commandChannel, byte @NonNull [] receivedData,
            @Nullable Integer sendQueueId, @NonNull String exceptionMessage) {

        for (IEBusParserListener listener : listeners) {
            try {
                listener.onTelegramResolveFailed(commandChannel, receivedData, sendQueueId, exceptionMessage);
            } catch (Exception e) {
                logger.error("Error while firing onTelegramResolved events!", e);
            }
        }
    }

}
