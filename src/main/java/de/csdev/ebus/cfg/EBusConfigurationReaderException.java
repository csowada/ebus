/**
 * Copyright (c) 2016-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg;

import java.text.MessageFormat;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusConfigurationReaderException extends Exception {

    private static final long serialVersionUID = 1L;

    public EBusConfigurationReaderException(String message, Throwable cause, Object... args) {
        super(String.format(message, args), cause);
    }

    public EBusConfigurationReaderException(String message, Object... args) {
        super(MessageFormat.format(message, args));
    }

    public EBusConfigurationReaderException(String message) {
        super(message);
    }

    public EBusConfigurationReaderException(String message, Throwable cause) {
        super(message, cause);
    }

}
