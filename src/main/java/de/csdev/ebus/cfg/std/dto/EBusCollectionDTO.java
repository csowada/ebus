/**
 * Copyright (c) 2017-2021 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg.std.dto;

import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import de.csdev.ebus.utils.CollectionUtils;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCollectionDTO {

    private @Nullable List<String> authors;

    private @Nullable List<EBusCommandDTO> commands;

    private @Nullable String description;

    private @Nullable String id;

    private @Nullable List<String> identification;

    private @Nullable String label;

    private @Nullable Map<String, Object> properties;

    private @Nullable List<EBusCommandTemplatesDTO> templates;

    private @Nullable String vendor;

    public @Nullable List<String> getAuthors() {
        return authors;
    }

    public @Nullable List<EBusCommandDTO> getCommands() {
        return commands;
    }

    public @Nullable String getDescription() {
        return description;
    }

    public @Nullable String getId() {
        return id;
    }

    public @Nullable List<String> getIdentification() {
        return identification;
    }

    public @Nullable String getLabel() {
        return label;
    }

    public @Nullable Map<String, Object> getProperties() {
        return CollectionUtils.unmodifiableNotNullMap(properties);
    }

    public @Nullable Object getProperty(String key) {
        return CollectionUtils.get(properties, key);
    }

    public @Nullable List<EBusCommandTemplatesDTO> getTemplates() {
        return templates;
    }

    public @Nullable String getVendor() {
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
        if (properties != null) {
            properties.put(key, value);
        }
    }

    public void setTemplates(List<EBusCommandTemplatesDTO> templates) {
        this.templates = templates;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    @Override
    public String toString() {
        return "EBusCollectionDTO [" + (authors != null ? "authors=" + authors + ", " : "")
                + (commands != null ? "commands=" + commands + ", " : "")
                + (description != null ? "description=" + description + ", " : "")
                + (id != null ? "id=" + id + ", " : "")
                + (identification != null ? "identification=" + identification + ", " : "")
                + (label != null ? "label=" + label + ", " : "")
                + (properties != null ? "properties=" + properties + ", " : "")
                + (templates != null ? "templates=" + templates + ", " : "")
                + (vendor != null ? "vendor=" + vendor : "") + "]";
    }

}
