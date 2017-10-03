/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
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
import java.util.Map;

import de.csdev.ebus.command.IEBusCommandMethod.Method;
import de.csdev.ebus.utils.CollectionUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCommand implements IEBusCommand {

    private Map<Integer, IEBusCommandMethod> channels;

    private String configurationSource;

    private String device;

    private String id;

    private String label;

    private IEBusCommandCollection parentCollection;

    private Map<String, Object> properties;

    public void addCommandChannel(IEBusCommandMethod channel) {
        // if (channels == null) {
        // channels = new EnumMap<Method, IEBusCommandMethod>(IEBusCommandMethod.Method.class);
        // }
        channels = CollectionUtils.newMapIfNull(channels);
        channels.put(channel.getMethod().ordinal(), channel);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        EBusCommand other = (EBusCommand) obj;
        if (channels == null) {
            if (other.channels != null) {
                return false;
            }
        } else if (!channels.equals(other.channels)) {
            return false;
        }
        if (configurationSource == null) {
            if (other.configurationSource != null) {
                return false;
            }
        } else if (!configurationSource.equals(other.configurationSource)) {
            return false;
        }
        if (device == null) {
            if (other.device != null) {
                return false;
            }
        } else if (!device.equals(other.device)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (label == null) {
            if (other.label != null) {
                return false;
            }
        } else if (!label.equals(other.label)) {
            return false;
        }
        if (properties == null) {
            if (other.properties != null) {
                return false;
            }
        } else if (!properties.equals(other.properties)) {
            return false;
        }
        return true;
    }

    @Override
    public Collection<IEBusCommandMethod.Method> getCommandChannelMethods() {

        Collection<IEBusCommandMethod.Method> x = new ArrayList<IEBusCommandMethod.Method>();

        // channels.containsKey(key)

        for (Method method : IEBusCommandMethod.Method.values()) {
            if (channels.containsKey(method.ordinal())) {
                x.add(method);
            }
        }

        // if (channels != null) {
        // return Collections.unmodifiableCollection(channels.keySet());
        // }
        return Collections.emptyList();
    }

    @Override
    public IEBusCommandMethod getCommandMethod(IEBusCommandMethod.Method method) {
        return CollectionUtils.get(channels, method.ordinal());
    }

    @Override
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
    public String getId() {
        return id;
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
    public IEBusCommandCollection getParentCollection() {
        return parentCollection;
    }

    @Override
    public Map<String, Object> getProperties() {
        return CollectionUtils.unmodifiableNotNullMap(properties);
    }

    @Override
    public int hashCode() {
        System.out.println("EBusCommand.hashCode()" + channels.hashCode());

        final int prime = 31;
        int result = 1;
        result = prime * result + ((channels == null) ? 0 : channels.hashCode());
        result = prime * result + ((configurationSource == null) ? 0 : configurationSource.hashCode());
        result = prime * result + ((device == null) ? 0 : device.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        return result;
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
