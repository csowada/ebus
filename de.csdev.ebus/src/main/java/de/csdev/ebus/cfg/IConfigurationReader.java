/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import de.csdev.ebus.cfg.datatypes.EBusTypes;
import de.csdev.ebus.command.IEBusCommand;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public interface IConfigurationReader {

    public List<IEBusCommand> loadConfiguration(InputStream inputStream)
            throws ConfigurationReaderException, IOException;

    public void setEBusTypes(EBusTypes ebusTypes);

}
