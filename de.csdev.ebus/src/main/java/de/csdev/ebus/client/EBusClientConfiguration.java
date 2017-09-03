package de.csdev.ebus.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.EBusConfigurationReader;
import de.csdev.ebus.cfg.EBusConfigurationReaderException;
import de.csdev.ebus.cfg.datatypes.EBusTypes;
import de.csdev.ebus.command.EBusCommandCollection;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.core.EBusController;

public class EBusClientConfiguration {

    private final Logger logger = LoggerFactory.getLogger(EBusClient.class);

    protected EBusConfigurationReader reader;

    protected EBusTypes dataTypes;

    protected ArrayList<EBusCommandCollection> collections;

    protected EBusCommandRegistry configurationProvider;

    public EBusClientConfiguration() {

        dataTypes = new EBusTypes();

        reader = new EBusConfigurationReader();

        reader.setEBusTypes(dataTypes);

        configurationProvider = new EBusCommandRegistry();

        collections = new ArrayList<EBusCommandCollection>();
    }

    public void clear() {
        configurationProvider = new EBusCommandRegistry();
        collections = new ArrayList<EBusCommandCollection>();
    }

    public List<String> getInternalConfigurationFiles() {
        return Arrays.asList("common-configuration.json", "wolf-cgb2-configuration.json",
                "wolf-sm1-configuration.json");
    }

    public void loadInternalConfiguration(String configurationFile) {
        logger.info("Load internal configuration {}", configurationFile);
        String configPath = "/commands/" + configurationFile;
        loadConfiguration(EBusController.class.getResourceAsStream(configPath));
    }

    public void loadInternalConfigurations() {
        for (String configurationFile : getInternalConfigurationFiles()) {
            loadInternalConfiguration(configurationFile);
        }
    }

    public void loadConfiguration(InputStream is) {

        EBusCommandCollection collection = null;
        try {

            collection = reader.loadConfigurationCollection(is);
            configurationProvider.addCommandCollection(collection);

            if (collection != null) {
                collections.add(collection);
            }

        } catch (IOException e) {
            logger.error("error!", e);
        } catch (EBusConfigurationReaderException e) {
            logger.error("error!", e);
        }
    }

    public List<EBusCommandCollection> getCollections() {
        return collections;
    }

}
