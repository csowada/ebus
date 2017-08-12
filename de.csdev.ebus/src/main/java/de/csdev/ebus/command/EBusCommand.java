/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.csdev.ebus.utils.CollectionUtils;

/**
 * @author Christian Sowada
 *
 */
public class EBusCommand implements IEBusCommandWritable {

    private String configurationSource;

    private String description;

    private String device;

    private String id;

    private Map<String, Object> properties;

    private Map<Type, IEBusCommandChannel> channels;

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getConfigurationSource()
     */
    public String getConfigurationSource() {
        return configurationSource;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getDevice()
     */
    public String getDevice() {
        return device;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getId()
     */
    public String getId() {
        return id;
    }

    public Map<String, Object> getProperties() {
        return CollectionUtils.unmodifiableNotNullMap(properties);
    }

    public void setConfigurationSource(String configurationSource) {
        this.configurationSource = configurationSource;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public EBusCommand setId(String id) {
        this.id = id;
        return this;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = new HashMap<String, Object>();
        this.properties.putAll(properties);
    }

    public void setProperty(String key, String value) {
        properties = CollectionUtils.newMapIfNull(properties);
        properties.put(key, value);
    }

    public IEBusCommandChannel getCommandChannel(Type channel) {
        return CollectionUtils.get(channels, channel);
    }

    public Collection<Type> getCommandChannelTypes() {
        return Collections.unmodifiableCollection(channels.keySet());
    }

    public Collection<IEBusCommandChannel> getCommandChannels() {
        return Collections.unmodifiableCollection(channels.values());
    }

    public void addCommandChannel(IEBusCommandChannel channel) {
        channels = CollectionUtils.newMapIfNull(channels);
        channels.put(channel.getType(), channel);
    }

}
