/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.cfg.EBusConfigurationReaderException;
import de.csdev.ebus.cfg.std.EBusConfigurationReader;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.IEBusCommandCollection;
import de.csdev.ebus.command.datatypes.EBusTypeRegistry;
import de.csdev.ebus.core.EBusController;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusClientConfiguration {

    private final Logger logger = LoggerFactory.getLogger(EBusClient.class);

    protected EBusConfigurationReader reader;

    protected EBusTypeRegistry dataTypes;

    protected ArrayList<IEBusCommandCollection> collections;

    protected EBusCommandRegistry configurationProvider;

    /**
     *
     */
    public EBusClientConfiguration() {

        dataTypes = new EBusTypeRegistry();

        reader = new EBusConfigurationReader();

        reader.setEBusTypes(dataTypes);

        configurationProvider = new EBusCommandRegistry();

        collections = new ArrayList<IEBusCommandCollection>();
    }

    /**
     *
     */
    public void clear() {
        configurationProvider = new EBusCommandRegistry();
        collections = new ArrayList<IEBusCommandCollection>();
    }

    /**
     * @return
     */
    public List<String> getInternalConfigurationFiles() {
        return Arrays.asList("common-configuration.json", "wolf-cgb2-configuration.json", "wolf-sm1-configuration.json",
                "vaillant-bai00-configuration.json", "vaillant-vrc-configuration.json");
    }

    /**
     * @param configurationFile
     */
    public void loadInternalConfiguration(String configurationFile) {
        logger.info("Load internal configuration {}", configurationFile);
        String configPath = "/commands/" + configurationFile;

        InputStream inputStream = EBusController.class.getResourceAsStream(configPath);
        if (inputStream == null) {
            throw new RuntimeException(String.format("Unable to load internal configuration \"%s\" ...", configPath));
        }

        loadConfiguration(inputStream);
    }

    /**
     *
     */
    public void loadInternalConfigurations() {
        for (String configurationFile : getInternalConfigurationFiles()) {
            loadInternalConfiguration(configurationFile);
        }
    }

    /**
     * @param is
     */
    public void loadConfiguration(InputStream is) {

        IEBusCommandCollection collection = null;
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

    /**
     * @return
     */
    public List<IEBusCommandCollection> getCollections() {
        return collections;
    }

}
