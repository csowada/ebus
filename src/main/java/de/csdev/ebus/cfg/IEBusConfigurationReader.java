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
import java.net.URL;
import java.util.List;

import de.csdev.ebus.command.IEBusCommandCollection;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public interface IEBusConfigurationReader {

    /**
     * Loads all build-in command collections implemented by the reader
     *
     * @return
     */
    public List<IEBusCommandCollection> loadBuildInConfigurationCollections();

    /**
     * Loads the configuration from an InputStream and returns a command collection
     *
     * @param url
     * @return
     * @throws EBusConfigurationReaderException
     * @throws IOException
     */
    public IEBusCommandCollection loadConfigurationCollection(URL url)
            throws EBusConfigurationReaderException, IOException;

    /**
     * @param url
     * @return
     */
    public List<IEBusCommandCollection> loadConfigurationCollectionBundle(URL url);

    /**
     * Sets the eBUS type registry to use
     *
     * @param ebusTypes
     */
    public void setEBusTypes(EBusTypeRegistry ebusTypes);

    /**
     * Clears all internal states
     */
    public void clear();
}
