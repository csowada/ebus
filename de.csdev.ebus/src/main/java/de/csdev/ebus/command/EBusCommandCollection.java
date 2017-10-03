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
import java.util.List;
import java.util.Map;

import de.csdev.ebus.utils.CollectionUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCommandCollection implements IEBusCommandCollection {

    private Map<String, IEBusCommand> commands = new HashMap<String, IEBusCommand>();

    private String description;

    private String id;

    private List<String> identification = new ArrayList<String>();

    private String label;

    private Map<String, Object> properties;

    private byte[] sourceHash;

    public EBusCommandCollection(String id, String label, String description, Map<String, Object> properties) {

        this.id = id;
        this.label = label;
        this.description = description;

        if (properties != null) {
            this.properties = CollectionUtils.newMapIfNull(this.properties);
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
        EBusCommandCollection other = (EBusCommandCollection) obj;
        if (commands == null) {
            if (other.commands != null) {
                return false;
            }
        } else if (!commands.equals(other.commands)) {
            return false;
        }
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (identification == null) {
            if (other.identification != null) {
                return false;
            }
        } else if (!identification.equals(other.identification)) {
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

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommandCollection#getCommands()
     */
    @Override
    public Collection<IEBusCommand> getCommands() {
        return Collections.unmodifiableCollection((commands.values()));
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
    public String getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommandCollection#getIdentification()
     */
    @Override
    public List<String> getIdentification() {
        if (identification == null) {
            identification = new ArrayList<String>();
        }
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
    public Map<String, Object> getProperties() {
        return CollectionUtils.unmodifiableNotNullMap(properties);
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((commands == null) ? 0 : commands.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((identification == null) ? 0 : identification.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        return result;
    }

    public void setIdentification(List<String> identification) {
        if (identification == null) {
            this.identification = new ArrayList<String>();
        }
        this.identification = identification;
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
