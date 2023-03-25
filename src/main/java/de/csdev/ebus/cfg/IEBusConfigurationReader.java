/**
 * Copyright (c) 2017-2023 by the respective copyright holders.
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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import de.csdev.ebus.command.IEBusCommandCollection;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
@NonNullByDefault
public interface IEBusConfigurationReader {

        /**
         * Loads all build-in command collections implemented by the reader
         *
         * @return
         */
        public List<IEBusCommandCollection> loadBuildInConfigurationCollections()
                        throws EBusConfigurationReaderException, IOException;

        /**
         * Loads the configuration from an InputStream and returns a command collection
         *
         * @param url
         * @return
         * @throws EBusConfigurationReaderException
         * @throws IOException
         */
        public @Nullable IEBusCommandCollection loadConfigurationCollection(final URL url)
                        throws EBusConfigurationReaderException, IOException;

        /**
         * @param url
         * @return
         */
        public List<IEBusCommandCollection> loadConfigurationCollectionBundle(final URL url)
                        throws EBusConfigurationReaderException, IOException;

        /**
         * Sets the eBUS type registry to use
         *
         * @param ebusTypes
         */
        public void setEBusTypes(final EBusTypeRegistry ebusTypes);

        /**
         * Clears all internal states
         */
        public void clear();
}
