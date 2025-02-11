/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command;

import java.util.Collection;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * A command is the link to encode/decode an eBUS byte telegram.
 *
 * @author Christian Sowada - Initial contribution
 *
 */
@NonNullByDefault
public interface IEBusCommand {

    /**
     * Returns the available command methods
     *
     * @return
     */
    public Collection<IEBusCommandMethod> getCommandMethods();

    /**
     * Returns the command method if available. Returns <code>null</code> if not availble.
     *
     * @param method
     * @return
     */
    public @Nullable IEBusCommandMethod getCommandMethod(final IEBusCommandMethod.Method method);

    /**
     * Returns the available command method enums.
     *
     * @return
     */
    public Collection<IEBusCommandMethod.Method> getCommandChannelMethods();

    /**
     * Returns device information from database
     *
     * @return
     */
    public @Nullable String getDevice();

    /**
     * Returns the source (file) of this command
     *
     * @return
     */
    public @Nullable String getConfigurationSource();

    /**
     * Get a short label to this command
     *
     * @return
     */
    public @Nullable String getLabel();

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

    /**
     * Returns the parent collection
     *
     * @return
     */
    public IEBusCommandCollection getParentCollection();
}
