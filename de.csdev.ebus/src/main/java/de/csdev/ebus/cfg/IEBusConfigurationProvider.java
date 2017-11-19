package de.csdev.ebus.cfg;

import java.io.InputStream;
import java.util.List;

public interface IEBusConfigurationProvider {

    public List<String> getConfigurationIds();

    public String getConfigurationLabel(String configurationId);

    public InputStream getConfigurationStream(String configurationId);

}