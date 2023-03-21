/**
 * Copyright (c) 2017-2023 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.EBusConfigurationReaderException;
import de.csdev.ebus.cfg.IEBusConfigurationReader;
import de.csdev.ebus.command.IEBusCommandMethod.Type;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.core.EBusConsts;
import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCommandRegistry {

    private final Logger logger = LoggerFactory.getLogger(EBusCommandRegistry.class);

    private @NonNull Map<String, IEBusCommandCollection> collections = new HashMap<>();

    private @NonNull EBusTypeRegistry typeRegistry;

    private @NonNull IEBusConfigurationReader reader;

    public IEBusConfigurationReader getConfigurationReader() {
        return reader;
    }

    /**
     * @param readerClass
     */
    public EBusCommandRegistry(Class<? extends IEBusConfigurationReader> readerClass) {
        this(readerClass, false);
    }

    /**
     * @param readerClass
     * @param loadBuildInCommands
     */
    public EBusCommandRegistry(Class<? extends IEBusConfigurationReader> readerClass, boolean loadBuildInCommands) {

        try {
            this.typeRegistry = new EBusTypeRegistry();

            this.reader = readerClass.getDeclaredConstructor().newInstance();
            reader.setEBusTypes(this.typeRegistry);

            if (loadBuildInCommands) {
                loadBuildInCommandCollections();
            }

        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | EBusTypeException
                | EBusConfigurationReaderException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Loads all build-in command collections
     * 
     * @throws IOException
     * @throws EBusConfigurationReaderException
     */
    public void loadBuildInCommandCollections() throws EBusConfigurationReaderException, IOException {
        List<@NonNull IEBusCommandCollection> loadBuildInConfigurations = reader.loadBuildInConfigurationCollections();

        if (!loadBuildInConfigurations.isEmpty()) {
            for (IEBusCommandCollection collection : loadBuildInConfigurations) {
                addCommandCollection(collection);
            }
        }
    }

    /**
     * Loads a configuration file
     *
     * @param url
     */
    public void loadCommandCollection(@NonNull URL url) throws EBusConfigurationReaderException, IOException {
        Objects.requireNonNull(url);
        addCommandCollection(reader.loadConfigurationCollection(url));
    }

    /**
     * @param url
     */
    public void loadCommandCollectionBundle(@NonNull URL url) throws EBusConfigurationReaderException, IOException  {

        Objects.requireNonNull(url);

        for (IEBusCommandCollection collection : reader.loadConfigurationCollectionBundle(url)) {
            addCommandCollection(collection);
        }
    }

    /**
     * Adds a command collection
     *
     * @param collection
     */
    public void addCommandCollection(IEBusCommandCollection collection) {
        if (collection != null) {
            collections.put(collection.getId(), collection);
        }
    }

    /**
     * Search for a command method for the given telegram
     *
     * @param data The complete unescaped eBUS telegram
     * @return Returns the a list of all matching configuration methods or an empty list
     */
    public @NonNull List<IEBusCommandMethod> find(byte @NonNull [] data) {

        Objects.requireNonNull(data);

        ByteBuffer buffer = ByteBuffer.wrap(data);
        return find(buffer);
    }

    /**
     * Search for a command method for the given telegram
     *
     * @param data The complete unescaped eBUS telegram
     * @return Returns the a list of all matching configuration methods or an empty list
     */
    public @NonNull List<IEBusCommandMethod> find(@NonNull ByteBuffer data) {

        Objects.requireNonNull(data);

        ArrayList<IEBusCommandMethod> result = new ArrayList<>();

        for (IEBusCommandCollection collection : collections.values()) {
            for (IEBusCommand command : collection.getCommands()) {
                for (IEBusCommandMethod commandChannel : command.getCommandMethods()) {

                    // check if telegram matches
                    if (commandChannel != null && matchesCommand(commandChannel, data)) {
                        result.add(commandChannel);
                    }
                }

            }
        }

        return result;

    }

    /**
     * Returns a command by collectionId and command id or <code>null</code>
     *
     * @param collectionId
     * @param id
     * @return
     */
    public @Nullable IEBusCommand getCommandById(@NonNull String collectionId, @NonNull String id) {

        Objects.requireNonNull(collectionId);
        Objects.requireNonNull(id);

        IEBusCommandCollection collection = collections.get(collectionId);
        if (collection == null) {
            return null;
        }

        return collection.getCommand(id);
    }

    /**
     * Returns a registered command collection with given id or <code>null</code>
     *
     * @param id
     * @return
     */
    public @Nullable IEBusCommandCollection getCommandCollection(@NonNull String id) {
        Objects.requireNonNull(id);
        return collections.get(id);
    }

    /**
     * Return all registered command collections
     *
     * @return
     */
    public @NonNull List<@NonNull IEBusCommandCollection> getCommandCollections() {
        return Collections.unmodifiableList(new ArrayList<IEBusCommandCollection>(collections.values()));
    }

    /**
     * Returns a command method by collectionId and command id or <code>null</code>
     *
     * @param id
     * @param type
     * @return
     */
    public @Nullable IEBusCommandMethod getCommandMethodById(@NonNull String collectionId, @NonNull String id,
            IEBusCommandMethod.@NonNull Method type) {

        IEBusCommand command = getCommandById(collectionId, id);

        if (command != null) {
            return command.getCommandMethod(type);
        }

        return null;
    }

    public EBusTypeRegistry getTypeRegistry() {
        return typeRegistry;
    }

    /**
     * Checks if the given command method is acceptable for the unescaped telegram
     *
     * @param command
     * @param data
     * @return
     */
    public boolean matchesCommand(@NonNull IEBusCommandMethod command, @NonNull ByteBuffer data) {

        Byte sourceAddress = (Byte) ObjectUtils.defaultIfNull(command.getSourceAddress(), Byte.valueOf((byte) 0x00));

        Byte targetAddress = (Byte) ObjectUtils.defaultIfNull(command.getDestinationAddress(),
                Byte.valueOf((byte) 0x00));

        // fast check - is this the right telegram type?
        boolean isInvalidBroadcast = data.get(1) == EBusConsts.BROADCAST_ADDRESS && command.getType() != Type.BROADCAST;
        boolean isInvalidMasterMaster = EBusUtils.isMasterAddress(data.get(1)) && command.getType() != Type.MASTER_MASTER;
        boolean isInvalidMasterSlave = EBusUtils.isSlaveAddress(data.get(1)) && command.getType() != Type.MASTER_SLAVE;

        if (isInvalidBroadcast || isInvalidMasterMaster || isInvalidMasterSlave) {
            return false;
        }

        try {

            ByteBuffer masterTelegram = EBusCommandUtils.buildMasterTelegram(command, sourceAddress, targetAddress,
                    null, true);

            ByteBuffer mask = command.getMasterTelegramMask();

            for (int i = 0; i < mask.limit(); i++) {
                byte b = mask.get(i);

                if (b == (byte) 0xFF) {
                    if (masterTelegram.get(i) != data.get(i)) {
                        break;
                    }
                }
                if (i == mask.limit() - 1) {

                    // add additional check for master-slave telegrams
                    if (command.getType() == Type.MASTER_SLAVE) {

                        // is a broadcast or master-master telegram
                        if (!EBusUtils.isSlaveAddress(data.get(1))) {

                            if (logger.isWarnEnabled()) {
                                logger.warn(
                                    "Data for matching command configuration \"{}\" is not a master-slave telegram as expected!",
                                    EBusCommandUtils.getFullId(command));
                                logger.warn("DATA: {}", EBusUtils.toHexDumpString(data));
                            }

                            return false;

                            // slave data is not defined in the configuration, not good!
                            // but we accept it for now. maybe we change it later on.
                        } else if (command.getSlaveTypes() == null) {
                            logger.debug(
                                    "Warning: Master-Slave command \"{}\" has no slave configuration defined! Skip advanced match checks ...",
                                    EBusCommandUtils.getFullId(command));

                        } else {

                            int computedSlaveLen = EBusCommandUtils.getSlaveDataLength(command);
                            int slaveLenPos = masterTelegram.limit() + 1;

                            // only check if the slave part is included in the data bytes
                            if (slaveLenPos <= data.limit()) {

                                int slaveLen = data.get(slaveLenPos);

                                if (slaveLen != computedSlaveLen) {
                                    if (logger.isTraceEnabled()) {
                                        logger.trace(
                                                "Skip matching command due to invalid response data length ... [{}]",
                                                EBusCommandUtils.getFullId(command));
                                        logger.trace("DATA: {}", EBusUtils.toHexDumpString(data));
                                    }

                                    return false;
                                }
                            }
                        }

                    }

                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("DATA: {}", EBusUtils.toHexDumpString(data));
            logger.error("CMD : {}, {}", command.getParent().getParentCollection().getId(), command.getParent().getId());
            logger.error("error!", e);
        }

        return false;
    }

    public void clear() {
        reader.clear();
        collections.clear();
    }

    @Override
    public String toString() {
        return "EBusCommandRegistry [collections=" + collections + "]";
    }

}
