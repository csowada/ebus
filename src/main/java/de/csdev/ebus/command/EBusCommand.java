/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
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
public class EBusCommand implements IEBusCommand {

    private Map<IEBusCommandMethod.@NonNull Method, @NonNull IEBusCommandMethod> channels = new EnumMap<>(IEBusCommandMethod.Method.class);

    private @Nullable String configurationSource;

    private @Nullable String device;

    private @Nullable String id;

    private @Nullable String label;

    private @Nullable IEBusCommandCollection parentCollection;

    private @Nullable Map<String, Object> properties;

    public void addCommandChannel(IEBusCommandMethod channel) {
        Objects.requireNonNull(channel);
        channels.put(channel.getMethod(), channel);
    }

    @Override
    public @NonNull Collection<IEBusCommandMethod.@NonNull Method> getCommandChannelMethods() {
        return Objects.requireNonNull(Collections.unmodifiableCollection(channels.keySet()));
    }

    @Override
    public @Nullable IEBusCommandMethod getCommandMethod(IEBusCommandMethod.@NonNull Method channel) {
        Objects.requireNonNull(channel);
        return CollectionUtils.get(channels, channel);
    }

    @Override
    public @NonNull Collection<@NonNull IEBusCommandMethod> getCommandMethods() {
        return Objects.requireNonNull(Collections.unmodifiableCollection(channels.values()));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getConfigurationSource()
     */
    @Override
    public @Nullable String getConfigurationSource() {
        return configurationSource;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getDevice()
     */
    @Override
    public @Nullable String getDevice() {
        return device;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getId()
     */
    @Override
    public @NonNull String getId() {
        return Objects.requireNonNull(id);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getDescription()
     */
    @Override
    public @Nullable String getLabel() {
        return label;
    }

    @Override
    public IEBusCommandCollection getParentCollection() {
        return Objects.requireNonNull(parentCollection);
    }

    @Override
    public Map<@NonNull String, @NonNull Object> getProperties() {
        return Objects.requireNonNull(CollectionUtils.unmodifiableNotNullMap(properties));
    }

    public void setConfigurationSource(@Nullable String configurationSource) {
        this.configurationSource = configurationSource;
    }

    public void setDevice(@Nullable String device) {
        this.device = device;
    }

    public EBusCommand setId(String id) {
        Objects.requireNonNull(id, "id");
        this.id = id;
        return this;
    }

    public void setLabel(@Nullable String label) {
        this.label = label;
    }

    public void setParentCollection(IEBusCommandCollection parentCollection) {
        Objects.requireNonNull(parentCollection, "parentCollection");
        this.parentCollection = parentCollection;
    }

    public void setProperties(Map<String, Object> properties) {
        Objects.requireNonNull(properties, "properties");
        this.properties = new HashMap<String, Object>();
        this.properties.putAll(properties);
    }

    public void setProperty(String key, String value) {

        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(value, "value");

        this.properties = CollectionUtils.newMapIfNull(this.properties);

        Map<String, Object> propertiesx = this.properties;
        if (propertiesx != null) {
            propertiesx.put(key, value);
        }
    }

    @Override
    public String toString() {
        return "EBusCommand [configurationSource=" + configurationSource + ", label=" + label + ", device=" + device
                + ", id=" + id + ", properties=" + properties + ", channels=" + channels + "]";
    }

}
