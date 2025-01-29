/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg;

import java.io.InputStream;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
@NonNullByDefault
public interface IEBusConfigurationProvider {

    /**
     * Returns a list with all configuration ids
     *
     * @return Returns a list with all configuration ids
     */
    public List<String> getConfigurationIds();

    /**
     * Returns a configuration label for an id or null if not existent.
     *
     * @param configurationId
     * @return Returns a configuration label for an id or null if not existent.
     */
    public @Nullable String getConfigurationLabel(final String configurationId);

    /**
     * Returns the input stream for a configuration id or null if not existent.
     *
     * @param configurationId
     * @return Returns the input stream for a configuration id or null if not existent.
     */
    public @Nullable InputStream getConfigurationStream(final String configurationId);

}