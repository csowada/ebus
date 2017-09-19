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

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public interface IEBusCommandMethod {

    enum Type {

        MASTER_SLAVE,

        MASTER_MASTER,

        BROADCAST

    }

    enum Method {

        GET,

        SET,

        BROADCAST
    }

    public IEBusCommandMethod.Type getType();

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

}
