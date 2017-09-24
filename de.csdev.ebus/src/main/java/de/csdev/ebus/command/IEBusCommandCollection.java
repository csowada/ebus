/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command;

import java.util.List;
import java.util.Map;

/**
 * A command is the link to encode/decode an eBUS byte telegram.
 *
 * @author Christian Sowada - Initial contribution
 *
 */
public interface IEBusCommandCollection {

    /**
     * Returns the list of eBUS device ids that are compatible with this collection.
     *
     * @return
     */
    List<String> getIdentification();

    /**
     * Returns the long description
     *
     * @return
     */
    String getDescription();

    /**
     * Returns the id of this collection
     *
     * @return
     */
    String getId();

    /**
     * Returns the label of this collection
     *
     * @return
     */
    String getLabel();

    /**
     * Returns the list of all commands
     * 
     * @return
     */
    List<IEBusCommand> getCommands();

    /**
     * Returns a map of all properties
     * 
     * @return
     */
    Map<String, Object> getProperties();

    /**
     * Returns a property value or <code>null</code>
     * 
     * @param key
     * @return
     */
    Object getProperty(String key);

}