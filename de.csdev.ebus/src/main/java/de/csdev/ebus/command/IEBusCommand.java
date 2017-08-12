/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command;

import java.util.Collection;
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

    public Collection<IEBusCommandChannel> getCommandChannels();

    public IEBusCommandChannel getCommandChannel(Type channel);

    public Collection<Type> getCommandChannelTypes();

    /**
     * Returns device information from database
     *
     * @return
     */
    public String getDevice();

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
     * Returns a map of additional properties
     *
     * @return
     */
    public Map<String, Object> getProperties();
}
