/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.EBusConfigurationReaderException;
import de.csdev.ebus.cfg.IEBusConfigurationReader;
import de.csdev.ebus.command.datatypes.EBusTypeException;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCommandRegistry {

    private Map<String, IEBusCommandCollection> collections = new HashMap<String, IEBusCommandCollection>();

    private final Logger logger = LoggerFactory.getLogger(EBusCommandRegistry.class);

    private EBusTypeRegistry typeRegistry;

    private IEBusConfigurationReader reader;

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

        typeRegistry = new EBusTypeRegistry();

        try {
            this.reader = readerClass.newInstance();
            this.reader.setEBusTypes(typeRegistry);

        } catch (InstantiationException e) {
            logger.error("error!", e);
        } catch (IllegalAccessException e) {
            logger.error("error!", e);
        }

        if (loadBuildInCommands) {
            loadBuildInCommandCollections();
        }
    }

    /**
     * Loads all build-in command collections
     */
    public void loadBuildInCommandCollections() {
        List<IEBusCommandCollection> loadBuildInConfigurations = reader.loadBuildInConfigurationCollections();

        if (loadBuildInConfigurations != null && !loadBuildInConfigurations.isEmpty()) {
            for (IEBusCommandCollection collection : loadBuildInConfigurations) {
                addCommandCollection(collection);
            }
        }
    }

    /**
     * Loads a configuration file
     * 
     * @param inputStream
     */
    public void loadCommandCollection(InputStream inputStream) {

        try {
            addCommandCollection(reader.loadConfigurationCollection(inputStream));

        } catch (EBusConfigurationReaderException e) {
            logger.error("error!", e);
        } catch (IOException e) {
            logger.error("error!", e);
        }

    }
    
    /**
     * @param url
     */
    public void loadCommandCollectionBundle(URL url) {
    	List<IEBusCommandCollection> collections = reader.loadConfigurationCollectionBundle(url);
    	
    	for (IEBusCommandCollection collection : collections) {
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
    public List<IEBusCommandMethod> find(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        return find(buffer);
    }

    /**
     * Search for a command method for the given telegram
     *
     * @param data The complete unescaped eBUS telegram
     * @return Returns the a list of all matching configuration methods or an empty list
     */
    public List<IEBusCommandMethod> find(ByteBuffer data) {

        ArrayList<IEBusCommandMethod> result = new ArrayList<IEBusCommandMethod>();

        for (IEBusCommandCollection collection : collections.values()) {
            for (IEBusCommand command : collection.getCommands()) {
                for (IEBusCommandMethod commandChannel : command.getCommandMethods()) {

                    // check if telegram matches
                    if (matchesCommand(commandChannel, data)) {
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
    public IEBusCommand getCommandById(String collectionId, String id) {

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
    public IEBusCommandCollection getCommandCollection(String id) {
        return collections.get(id);
    }

    /**
     * Return all registered command collections
     *
     * @return
     */
    public List<IEBusCommandCollection> getCommandCollections() {
        return Collections.unmodifiableList(new ArrayList<IEBusCommandCollection>(collections.values()));
    }

    /**
     * Returns a command method by collectionId and command id or <code>null</code>
     *
     * @param id
     * @param type
     * @return
     */
    public IEBusCommandMethod getCommandMethodById(String collectionId, String id, IEBusCommandMethod.Method type) {

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
    public boolean matchesCommand(IEBusCommandMethod command, ByteBuffer data) {

        Byte sourceAddress = (Byte) ObjectUtils.defaultIfNull(command.getSourceAddress(), Byte.valueOf((byte) 0x00));

        Byte targetAddress = (Byte) ObjectUtils.defaultIfNull(command.getDestinationAddress(),
                Byte.valueOf((byte) 0x00));

        try {

            ByteBuffer masterTelegram = EBusCommandUtils.buildMasterTelegram(command, sourceAddress, targetAddress,
                    null);

            ByteBuffer mask = command.getMasterTelegramMask();

            for (int i = 0; i < mask.limit(); i++) {
                byte b = mask.get(i);

                if (b == (byte) 0xFF) {
                    if (masterTelegram.get(i) != data.get(i)) {
                        break;
                    }
                }
                if (i == mask.limit() - 1) {
                    return true;
                }
            }
        } catch (EBusTypeException e) {
            logger.error("error!", e);
        }

        return false;
    }

    public void clear() {
        collections.clear();
    }

    @Override
    public String toString() {
        return "EBusCommandRegistry [collections=" + collections + "]";
    }

}
