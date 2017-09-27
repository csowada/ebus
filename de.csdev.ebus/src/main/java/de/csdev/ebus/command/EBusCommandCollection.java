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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.csdev.ebus.utils.CollectionUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCommandCollection implements IEBusCommandCollection {

    private List<IEBusCommand> commands = new ArrayList<IEBusCommand>();

    private String description;

    private String id;

    private List<String> identification = new ArrayList<String>();

    private String label;

    private Map<String, Object> properties;

    public EBusCommandCollection(String id, String label, String description, Map<String, Object> properties) {

        this.id = id;
        this.label = label;
        this.description = description;

        if (properties != null) {
            this.properties = CollectionUtils.newMapIfNull(this.properties);
            this.properties.putAll(properties);
        }

        this.commands.addAll(commands);
    }

    public void addCommand(IEBusCommand command) {
        this.commands.add(command);
    }

    public void addCommandw(List<IEBusCommand> commands) {
        this.commands.addAll(commands);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.csdev.ebus.command.IEBusCommandCollection#getCommands()
     */
    @Override
    public List<IEBusCommand> getCommands() {
        return Collections.unmodifiableList(commands);
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

    public void setIdentification(List<String> identification) {
        if (identification == null) {
            this.identification = new ArrayList<String>();
        }
        this.identification = identification;
    }

    @Override
    public String toString() {
        return "EBusCommandCollection [commands=" + commands + ", properties=" + properties + ", id=" + id + ", label="
                + label + ", identification=" + identification + "]";
    }

}
