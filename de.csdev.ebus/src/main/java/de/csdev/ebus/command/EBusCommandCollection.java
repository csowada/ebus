package de.csdev.ebus.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class EBusCommandCollection {

    private Map<String, Object> properties = new HashMap<String, Object>();
    private List<IEBusCommand> commands = new ArrayList<IEBusCommand>();

    public EBusCommandCollection(Map<String, Object> properties, List<IEBusCommand> commands) {

        // copy over all execpt commands block
        for (Entry<String, Object> entry : properties.entrySet()) {
            if (!entry.getKey().equals("commands")) {
                this.properties.put(entry.getKey(), entry.getValue());
            }
        }

        this.commands.addAll(commands);
    }

    public String getId() {
        return getAsString("id");
    }

    public String getLabel() {
        return getAsString("label");
    }

    public String getAsString(String key) {
        Object object = get(key);
        return object instanceof String ? (String) object : null;
    }

    public Object get(String key) {
        return properties.get(key);
    }

    public List<IEBusCommand> getCommands() {
        return Collections.unmodifiableList(commands);
    }

}
