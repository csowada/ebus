/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import de.csdev.ebus.command.IEBusCommandCollection;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public interface IEBusConfigurationReader {

    /**
     * Loads the configuration from an InputStream and returns a command collection
     *
     * @param inputStream
     * @return
     * @throws EBusConfigurationReaderException
     * @throws IOException
     */
    public IEBusCommandCollection loadConfigurationCollection(InputStream inputStream)
            throws EBusConfigurationReaderException, IOException;

    /**
     * Sets the eBUS type registry to use
     *
     * @param ebusTypes
     */
    public void setEBusTypes(EBusTypeRegistry ebusTypes);

    public List<IEBusCommandCollection> loadBuildInConfigurations();
}
