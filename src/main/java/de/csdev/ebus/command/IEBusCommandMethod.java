/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command;

import java.nio.ByteBuffer;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
@NonNullByDefault
public interface IEBusCommandMethod {

    /**
     * Type of the method
     */
    public enum Type {

        /** master slave telegram */
        MASTER_SLAVE,

        /** master master telegram */
        MASTER_MASTER,

        /** broadcast telegram */
        BROADCAST

    }

    /**
     * Kind of the method
     */
    public enum Method {

        /** A getter command */
        GET,

        /** A setter command */
        SET,

        /** A broadcast command */
        BROADCAST
    }

    /**
     * Returns the method enum of this method
     *
     * @return
     */
    public IEBusCommandMethod.Type getType();

    /**
     * Returns the parent command
     *
     * @return
     */
    public IEBusCommand getParent();

    /**
     * Returns the type of this command
     *
     * @return
     */
    public IEBusCommandMethod.Method getMethod();

    /**
     * Returns defined destination address or null if not defined
     *
     * @return
     */
    public @Nullable Byte getDestinationAddress();

    /**
     * Returns defined source address or null if not defined
     *
     * @return
     */
    public @Nullable Byte getSourceAddress();

    /**
     * Returns the telegram mask
     *
     * @return
     */
    public ByteBuffer getMasterTelegramMask();

    /**
     * Get ordered list of eBus data types for the master part
     *
     * @return
     */
    public @Nullable List<IEBusValue> getMasterTypes();

    /**
     * Get ordered list of eBus data types for the slave part
     *
     * @return
     */
    public @Nullable List<IEBusValue> getSlaveTypes();

    /**
     * Returns the eBus command bytes
     *
     * @return
     */
    public byte[] getCommand();

}
