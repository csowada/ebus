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
import java.util.List;
import java.util.Map;

/**
 * @author Christian Sowada
 *
 */
public interface IEBusCommand {

    public enum Type {
        /** xxx */
        GET,

        SET,

        BROADCAST
    }

    /**
     * Returns device information from database
     *
     * @return
     */
    public String getDevice();

    /**
     * Returns defined destination address or null if not defined
     *
     * @return
     */
    public Byte getDestinationAddress();

    /**
     * Returns defined source address or null if not defined
     *
     * @return
     */
    public Byte getSourceAddress();

    /**
     * Returns the telegram mask
     *
     * @return
     */
    public ByteBuffer getMasterTelegramMask();

    public List<IEBusValue> getExtendCommandValue();

    /**
     * Get ordered list of eBus data types for the master part
     *
     * @return
     */
    public List<IEBusValue> getMasterTypes();

    /**
     * Get ordered list of eBus data types for the slave part
     *
     * @return
     */
    public List<IEBusValue> getSlaveTypes();

    /**
     * Returns the eBus command bytes
     *
     * @return
     */
    public byte[] getCommand();

    /**
     * Returns the source (file) of this command
     *
     * @return
     */
    public String getConfigurationSource();

    /**
     * Get a short description to this command
     *
     * @return
     */
    public String getDescription();

    /**
     * Returns the id of this command
     *
     * @return
     */
    public String getId();

    /**
     * Returns the type of this command
     *
     * @return
     */
    public Type getType();

    /**
     * Returns a map of additional properties
     * 
     * @return
     */
    public Map<String, String> getProperties();
}
