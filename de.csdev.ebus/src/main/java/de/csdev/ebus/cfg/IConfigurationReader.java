package de.csdev.ebus.cfg;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import de.csdev.ebus.cfg.datatypes.EBusTypes;
import de.csdev.ebus.command.EBusCommand;

public interface IConfigurationReader {

    public List<EBusCommand> loadConfiguration(InputStream inputStream) throws IOException;

    public void setEBusTypes(EBusTypes ebusTypes);

}
