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
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCommand implements IEBusCommandWritable {

    private Map<IEBusCommandMethod.Method, IEBusCommandMethod> channels;

    private String configurationSource;

    private String device;

    private String id;

    private String label;

    private IEBusCommandCollection parentCollection;

    private Map<String, Object> properties;

    public void addCommandChannel(IEBusCommandMethod channel) {
        channels = CollectionUtils.newMapIfNull(channels);
        channels.put(channel.getMethod(), channel);
    }

    public Collection<IEBusCommandMethod.Method> getCommandChannelMethods() {
        if (channels != null) {
            return Collections.unmodifiableCollection(channels.keySet());
        }
        return Collections.emptyList();
    }

    public IEBusCommandMethod getCommandMethod(IEBusCommandMethod.Method channel) {
        return CollectionUtils.get(channels, channel);
    }

    public Collection<IEBusCommandMethod> getCommandMethods() {
        if (channels != null) {
            return Collections.unmodifiableCollection(channels.values());
        }
        return Collections.emptyList();
    }

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

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommand#getDescription()
     */
    public String getLabel() {
        return label;
    }

    public IEBusCommandCollection getParentCollection() {
        return parentCollection;
    }

    public Map<String, Object> getProperties() {
        return CollectionUtils.unmodifiableNotNullMap(properties);
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
