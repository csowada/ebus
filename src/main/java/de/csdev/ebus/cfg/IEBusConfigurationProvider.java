/**
 * Copyright (c) 2016-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg;

import java.io.InputStream;
import java.util.List;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public interface IEBusConfigurationProvider {

    /**
     * Returns a list with all configuration ids
     *
     * @return
     */
    public List<String> getConfigurationIds();

    /**
     * Returns a configuration label for an id or null if not existent.
     *
     * @param configurationId
     * @return
     */
    public String getConfigurationLabel(String configurationId);

    /**
     * Returns the input stream for a configuration id or null if not existent.
     * 
     * @param configurationId
     * @return
     */
    public InputStream getConfigurationStream(String configurationId);

}