/**
 * Copyright (c) 2017-2021 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

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
    public @NonNull List<String> getIdentification();

    /**
     * Returns the long description
     *
     * @return
     */
    public @Nullable String getDescription();

    /**
     * Returns the id of this collection
     *
     * @return
     */
    public @NonNull String getId();

    /**
     * Returns the label of this collection
     *
     * @return
     */
    public @Nullable String getLabel();

    /**
     * Returns the list of all commands
     *
     * @return
     */
    public @NonNull Collection<IEBusCommand> getCommands();

    /**
     * Returns the requested command or null
     *
     * @param id
     * @return
     */
    public @Nullable IEBusCommand getCommand(String id);

    /**
     * Returns a map of all properties
     *
     * @return
     */
    public @NonNull Map<String, Object> getProperties();

    /**
     * Returns a property value or <code>null</code>
     *
     * @param key
     * @return
     */
    public @Nullable Object getProperty(String key);

    /**
     * Returns usually a MD5 hash of the source configuration file
     *
     * @return
     */
    public byte[] getSourceHash();

}