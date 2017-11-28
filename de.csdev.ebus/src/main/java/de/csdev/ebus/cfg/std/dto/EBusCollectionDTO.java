/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg.std.dto;

import java.util.List;
import java.util.Map;

import de.csdev.ebus.utils.CollectionUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCollectionDTO {

    private List<String> authors;
    
    private List<EBusCommandDTO> commands;
    
    private String description;
    
    private String id;
    
    private List<String> identification;
    
    private String label;
    
    private Map<String, Object> properties;

    private List<EBusCommandTemplatesDTO> templates;

    private String vendor;

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

    public List<String> getIdentification() {
        return identification;
    }

    public String getLabel() {
        return label;
    }

    public Map<String, Object> getProperties() {
        return CollectionUtils.unmodifiableNotNullMap(properties);
    }

    public Object getProperty(String key) {
        return CollectionUtils.get(properties, key);
    }

    public List<EBusCommandTemplatesDTO> getTemplates() {
		return templates;
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

    public void setIdentification(List<String> identification) {
        this.identification = identification;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public void setProperty(String key, Object value) {
        properties = CollectionUtils.newMapIfNull(properties);
        properties.put(key, value);
    }

    public void setTemplates(List<EBusCommandTemplatesDTO> templates) {
		this.templates = templates;
	}

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    @Override
    public String toString() {
        return "EBusCollectionDTO [authors=" + authors + ", commands=" + commands + ", description=" + description
                + ", id=" + id + ", identification=" + identification + ", label=" + label + ", vendor=" + vendor
                + ", properties=" + properties + "]";
    }

}
