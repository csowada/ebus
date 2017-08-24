/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg.dto;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.utils.CollectionUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCollectionDTO {

    private final Logger logger = LoggerFactory.getLogger(EBusCollectionDTO.class);

    private List<String> authors;
    private List<EBusCommandDTO> commands;
    private String description;
    private String id;
    private String identification;
    private String label;
    private String vendor;

    private Map<String, Object> properties;

    public void setProperty(String key, Object value) {
        logger.info("Add custom property \"{}\" with value \"{}\"", key, value);
        properties = CollectionUtils.newMapIfNull(properties);
        properties.put(key, value);
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Object getProperty(String key) {
        return CollectionUtils.get(properties, key);
    }

    public Map<String, Object> getProperties() {
        return CollectionUtils.unmodifiableNotNullMap(properties);
    }

    public List<String> getAuthors() {
        return authors;
    }

    public List<EBusCommandDTO> getCommands() {
        return commands;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public String getIdentification() {
        return identification;
    }

    public String getLabel() {
        return label;
    }

    public String getVendor() {
        return vendor;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public void setCommands(List<EBusCommandDTO> commands) {
        this.commands = commands;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

}
