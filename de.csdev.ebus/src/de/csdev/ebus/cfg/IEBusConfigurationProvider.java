package de.csdev.ebus.cfg;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public interface IEBusConfigurationProvider {
    public void loadConfigurationFile(URL url) throws IOException;

    public void clear();

    public boolean isEmpty();

    public List<IEBusConfiguration> getCommandsByFilter(String bufferString);

    public IEBusConfiguration getCommandById(String commandId);
}
