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
     * @return
     */
    List<String> getIdentification();

    /**
     * @return
     */
    String getDescription();

    /**
     * @return
     */
    String getId();

    /**
     * @return
     */
    String getLabel();

    /**
     * @param key
     * @return
     */
    String getAsString(String key);

    /**
     * @param key
     * @return
     */
    Object get(String key);

    /**
     * @return
     */
    List<IEBusCommand> getCommands();

    /**
     * @return
     */
    Map<String, Object> getProperties();

    /**
     * @param key
     * @return
     */
    Object getProperty(String key);

}