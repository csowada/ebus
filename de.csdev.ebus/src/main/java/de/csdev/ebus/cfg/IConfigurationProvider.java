package de.csdev.ebus.cfg;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import de.csdev.ebus.cfg.datatypes.EBusTypes;
import de.csdev.ebus.command.EBusCommand;

public interface IConfigurationProvider {

    public List<EBusCommand> loadConfiguration(URL url) throws IOException;

    public EBusTypes getEBusTypes();

    public void setEBusTypes(EBusTypes ebusTypes);

}
