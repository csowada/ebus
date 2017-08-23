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
public class EBusCommandCollection {

    private List<IEBusCommand> commands = new ArrayList<IEBusCommand>();
    private Map<String, Object> properties;
    private String id;
    private String label;

    public EBusCommandCollection(String id, String label, Map<String, Object> properties, List<IEBusCommand> commands) {

        this.id = id;
        this.label = label;

        if (properties != null) {
            this.properties = CollectionUtils.newMapIfNull(this.properties);
            this.properties.putAll(properties);
        }

        this.commands.addAll(commands);
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getAsString(String key) {
        Object object = get(key);
        return object instanceof String ? (String) object : null;
    }

    public Object get(String key) {
        return CollectionUtils.get(properties, key);
    }

    public List<IEBusCommand> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    public Map<String, Object> getProperties() {
        return CollectionUtils.unmodifiableNotNullMap(properties);
    }

    public Object getProperty(String key) {
        return CollectionUtils.get(properties, key);
    }
}
