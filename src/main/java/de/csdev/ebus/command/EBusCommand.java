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
import org.eclipse.jdt.annotation.Nullable;

import de.csdev.ebus.utils.CollectionUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCommand implements IEBusCommand {

    private @NonNull Map<IEBusCommandMethod.@NonNull Method, @Nullable IEBusCommandMethod> channels = new EnumMap<IEBusCommandMethod.@NonNull Method, @Nullable IEBusCommandMethod>(
            IEBusCommandMethod.Method.class);

    private String configurationSource;

    private String device;

    private String id;

    private String label;

    private IEBusCommandCollection parentCollection;

    private Map<String, Object> properties;

    public void addCommandChannel(@NonNull IEBusCommandMethod channel) {
        Objects.requireNonNull(channel);
        channels.put(channel.getMethod(), channel);
    }

    @Override
    public @NonNull Collection<IEBusCommandMethod.Method> getCommandChannelMethods() {
        return Objects.requireNonNull(Collections.unmodifiableCollection(channels.keySet()));
    }

    @Override
    public @Nullable IEBusCommandMethod getCommandMethod(IEBusCommandMethod.@NonNull Method channel) {
        Objects.requireNonNull(channel);
        return CollectionUtils.get(channels, channel);
    }

    @Override
    public @NonNull Collection<IEBusCommandMethod> getCommandMethods() {
        return Objects.requireNonNull(Collections.unmodifiableCollection(channels.values()));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getConfigurationSource()
     */
    @Override
    public String getConfigurationSource() {
        return configurationSource;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getDevice()
     */
    @Override
    public String getDevice() {
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
    public String getLabel() {
        return label;
    }

    @Override
    public @NonNull IEBusCommandCollection getParentCollection() {
        return Objects.requireNonNull(parentCollection);
    }

    @Override
    public @NonNull Map<String, Object> getProperties() {
        return Objects.requireNonNull(CollectionUtils.unmodifiableNotNullMap(properties));
    }

    public void setConfigurationSource(String configurationSource) {
        this.configurationSource = configurationSource;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public EBusCommand setId(String id) {
        this.id = id;
        return this;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setParentCollection(IEBusCommandCollection parentCollection) {
        this.parentCollection = parentCollection;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = new HashMap<String, Object>();
        this.properties.putAll(properties);
    }

    public void setProperty(String key, String value) {
        properties = CollectionUtils.newMapIfNull(properties);
        properties.put(key, value);
    }

    @Override
    public String toString() {
        return "EBusCommand [configurationSource=" + configurationSource + ", label=" + label + ", device=" + device
                + ", id=" + id + ", properties=" + properties + ", channels=" + channels + "]";
    }

}
