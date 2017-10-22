/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.command.datatypes.EBusTypeException;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCommandRegistry {

    private final Logger logger = LoggerFactory.getLogger(EBusCommandRegistry.class);

    private Map<String, IEBusCommandCollection> collections = new HashMap<String, IEBusCommandCollection>();

    /**
     * Adds a command collection
     *
     * @param collection
     */
    public void addCommandCollection(IEBusCommandCollection collection) {
        collections.put(collection.getId(), collection);
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
    public Collection<IEBusCommandCollection> getCommandCollections() {
        return Collections.unmodifiableCollection(collections.values());
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

    @Override
    public String toString() {
        return "EBusCommandRegistry [collections=" + collections + "]";
    }

}
