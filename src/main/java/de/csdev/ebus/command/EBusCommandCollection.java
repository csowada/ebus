/**
 * Copyright (c) 2017-2023 by the respective copyright holders.
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
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import de.csdev.ebus.utils.CollectionUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
@NonNullByDefault
public class EBusCommandCollection implements IEBusCommandCollection {

    private Map<String, IEBusCommand> commands = new HashMap<>();

    private String description;

    private String id;

    private List<String> identification = new ArrayList<>();

    private String label;

    private Map<String, Object> properties = new HashMap<>();

    private byte[] sourceHash;

    public EBusCommandCollection(final String id, final String label, final String description, final Map<String, Object> properties) {

        Objects.requireNonNull(id);
        Objects.requireNonNull(label);
        Objects.requireNonNull(description);
        Objects.requireNonNull(properties);

        this.id = id;
        this.label = label;
        this.description = description;
        this.properties.putAll(properties);
        this.sourceHash = new byte[]{};
    }

    public void addCommand(final IEBusCommand command) {
        this.commands.put(command.getId(), command);
    }

    public void addCommands(final List<IEBusCommand> commands) {
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
    public @Nullable Object getProperty(final String key) {
        return CollectionUtils.get(properties, key);
    }

    public void setIdentification(final @Nullable List<String> identification) {
        if (identification != null && !identification.isEmpty()) {
            this.identification.clear();
            this.identification.addAll(identification);
        }
    }

    public void setSourceHash(final byte[] sourceHash) {
        this.sourceHash = sourceHash;
    }

    @Override
    public String toString() {
        return "EBusCommandCollection [commands=" + commands + ", properties=" + properties + ", id=" + id + ", label="
                + label + ", identification=" + identification + "]";
    }

    @Override
    public @Nullable IEBusCommand getCommand(final String id) {
        return commands.get(id);
    }

    @Override
    public byte[] getSourceHash() {
        return sourceHash;
    }

}
