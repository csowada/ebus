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
 * @author Christian Sowada - Initial contribution
 *
 */
public interface IEBusCommand {

    public Collection<IEBusCommandMethod> getCommandMethods();

    public IEBusCommandMethod getCommandMethod(IEBusCommandMethod.Method method);

    public Collection<IEBusCommandMethod.Method> getCommandChannelMethods();

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
     * Get a short label to this command
     *
     * @return
     */
    public String getLabel();

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
