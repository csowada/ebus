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
package de.csdev.ebus.main;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.csdev.ebus.aaa.EBusHighLevelService;
import de.csdev.ebus.cfg.EBusConfigurationJsonReader;
import de.csdev.ebus.core.EBusController;
import de.csdev.ebus.core.connection.EBusCaptureProxyConnection;
import de.csdev.ebus.core.connection.EBusEmulatorConnection;
import de.csdev.ebus.core.connection.EBusTCPConnection;
import de.csdev.ebus.core.connection.IEBusConnection;
import de.csdev.ebus.utils.EmulatorCapture;

public class EBusMain {

    private static final Logger logger = LoggerFactory.getLogger(EBusMain.class);

    @SuppressWarnings("deprecation")
    public static void main(String[] args) {

        try {
            IEBusConnection connection = new EBusTCPConnection("my-ebus-server", 8000);

            EBusController controller = new EBusController(connection);
            EBusHighLevelService service = new EBusHighLevelService(controller);
            EBusConfigurationJsonReader jsonCfgReader = new EBusConfigurationJsonReader(
                    service.getConfigurationProvider());

            File filex = new File("src/resources/common-configuration.json");
            jsonCfgReader.loadConfigurationFile(filex.toURL());

            controller.start();

            // main thread wait
            controller.join();

        } catch (InterruptedException e) {
            logger.error("errro1", e);
        } catch (MalformedURLException e) {
            logger.error("errro1", e);
        } catch (IOException e) {
            logger.error("errro1", e);
        }
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
