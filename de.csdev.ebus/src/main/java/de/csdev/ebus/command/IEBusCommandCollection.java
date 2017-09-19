package de.csdev.ebus.command;

import java.util.List;
import java.util.Map;

public interface IEBusCommandCollection {

    List<String> getIdentification();

    String getDescription();

    String getId();

    String getLabel();

    String getAsString(String key);

    Object get(String key);

    List<IEBusCommand> getCommands();

    Map<String, Object> getProperties();

    Object getProperty(String key);

}