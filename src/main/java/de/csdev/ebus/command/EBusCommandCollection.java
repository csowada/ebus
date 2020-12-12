/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import de.csdev.ebus.utils.CollectionUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCommandCollection implements IEBusCommandCollection {

    private @NonNull Map<String, IEBusCommand> commands = new HashMap<String, IEBusCommand>();

    private @NonNull String description;

    private @NonNull String id;

    private @NonNull List<String> identification = new ArrayList<String>();

    private @NonNull String label;

    private @NonNull Map<String, Object> properties;

    private byte[] sourceHash;

    public EBusCommandCollection(String id, String label, String description, Map<String, Object> properties) {

        Objects.requireNonNull(id);
        Objects.requireNonNull(label);
        Objects.requireNonNull(description);

        this.id = id;
        this.label = label;
        this.description = description;

        this.properties = new HashMap<String, Object>();

        if (properties != null) {
            // this.properties = CollectionUtils.newMapIfNull(this.properties);
            this.properties.putAll(properties);
        }

    }

    public void addCommand(IEBusCommand command) {
        this.commands.put(command.getId(), command);
    }

    public void addCommands(List<IEBusCommand> commands) {
        for (IEBusCommand command : commands) {
            addCommand(command);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommandCollection#getCommands()
     */
    @Override
    public @NonNull Collection<IEBusCommand> getCommands() {
        Collection<IEBusCommand> collection = Collections.unmodifiableCollection((commands.values()));
        return Objects.requireNonNull(collection);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommandCollection#getDescription()
     */
    @Override
    public String getDescription() {
        return description;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommandCollection#getId()
     */
    @Override
    public @NonNull String getId() {
        return Objects.requireNonNull(id);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommandCollection#getIdentification()
     */
    @Override
    public @NonNull List<String> getIdentification() {
        return identification;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommandCollection#getLabel()
     */
    @Override
    public String getLabel() {
        return label;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommandCollection#getProperties()
     */
    @Override
    public @NonNull Map<String, Object> getProperties() {
        return Objects.requireNonNull(CollectionUtils.unmodifiableNotNullMap(properties));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommandCollection#getProperty(java.lang.String)
     */
    @Override
    public Object getProperty(String key) {
        return CollectionUtils.get(properties, key);
    }

    public void setIdentification(@Nullable List<String> identification) {
        if (identification != null && !identification.isEmpty()) {
            this.identification.clear();
            this.identification.addAll(identification);
        }
    }

    public void setSourceHash(byte[] sourceHash) {
        this.sourceHash = sourceHash;
    }

    @Override
    public String toString() {
        return "EBusCommandCollection [commands=" + commands + ", properties=" + properties + ", id=" + id + ", label="
                + label + ", identification=" + identification + "]";
    }

    @Override
    public IEBusCommand getCommand(String id) {
        return commands.get(id);
    }

    @Override
    public byte[] getSourceHash() {
        return sourceHash;
    }

}
