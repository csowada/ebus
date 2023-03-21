/**
 * Copyright (c) 2017-2023 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command;

import java.nio.ByteBuffer;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
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
    public IEBusCommandMethod.@NonNull Type getType();

    /**
     * Returns the parent command
     *
     * @return
     */
    public @NonNull IEBusCommand getParent();

    /**
     * Returns the type of this command
     *
     * @return
     */
    public IEBusCommandMethod.@NonNull Method getMethod();

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
    public @NonNull ByteBuffer getMasterTelegramMask();

    /**
     * Get ordered list of eBus data types for the master part
     *
     * @return
     */
    public @Nullable List<@NonNull IEBusValue> getMasterTypes();

    /**
     * Get ordered list of eBus data types for the slave part
     *
     * @return
     */
    public @Nullable List<@NonNull IEBusValue> getSlaveTypes();

    /**
     * Returns the eBus command bytes
     *
     * @return
     */
    public byte @NonNull [] getCommand();

}
