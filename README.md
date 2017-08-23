# Introduction

This library handles the communication with heating engineering via the eBUS specification. This protocol is used by many heating manufacturers in Europe.

_Notice: This is not an offical eBUS library!_

# Example

```java
/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;

import de.csdev.ebus.client.EBusClient;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.command.IEBusCommand;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.core.EBusController;
import de.csdev.ebus.core.connection.EBusTCPConnection;
import de.csdev.ebus.core.connection.IEBusConnection;
import de.csdev.ebus.service.parser.EBusParserListener;

public class EBusMain {

    private static final Logger logger = LoggerFactory.getLogger(EBusMain.class);

    @SuppressWarnings("deprecation")
    public static void main(String[] args) {

        // load a json configuration file from jar
        InputStream inputStream = ConfigurationReader.class
                .getResourceAsStream("/commands/wolf-sm1-configuration.json");
        
        // create a connection
        IEBusConnection connection = new EBusTCPConnection("my-ebus-server", 8000);
        
        
        // create the working horse controller
        EBusController controller = new EBusController(connection);

        
        // create the high level client
        EBusClient client = new EBusClient(controller, (byte) 0xFF);

        // read the configuration from included json configurations and add it to the provider
        ConfigurationReader reader = new ConfigurationReader();
        List<IEBusCommand> configurationList = reader.loadConfiguration(inputStream);
        client.getConfigurationProvider().addTelegramConfigurationList(configurationList);

        client.getResolverService().addEBusParserListener(new EBusParserListener() {
            
            public void onTelegramResolved(IEBusCommandMethod commandChannel, Map<String, Object> result, byte[] receivedData,
                    Integer sendQueueId) {
                // valid parsed telegram received !
            }
        });
        
        // start the controller thread
        controller.start();

        // main thread wait
        controller.join();

    }
}
```

#Dependencies

## Required

* slf4j
* commons-io
* commons-lang
* jackson

## Optional

* nrjavaserial
* jssc

For more information see Maven _pom.xml_ file

# Trademark Disclaimer

Product names, logos, brands and other trademarks referred to within this project are the property of their respective trademark holders. These trademark holders are not affiliated with our website. They do not sponsor or endorse our materials.
