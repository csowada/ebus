/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class ConfigurationReaderException extends Exception {

    private static final long serialVersionUID = 1L;

    public ConfigurationReaderException(String message, Throwable cause, Object... args) {
        super(String.format(message, args), cause);
    }

    public ConfigurationReaderException(String message, Object... args) {
        super(String.format(message, args));
    }

    public ConfigurationReaderException(String message) {
        super(message);
    }

    public ConfigurationReaderException(String message, Throwable cause) {
        super(message, cause);
    }

}
