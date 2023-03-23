/**
 * Copyright (c) 2017-2023 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
@NonNullByDefault
public class EBusConfigurationReaderException extends Exception {

    private static final long serialVersionUID = 1L;

    public EBusConfigurationReaderException(final String message) {
        super(message);
    }

    public EBusConfigurationReaderException(final String message, final Object... args) {
        super(String.format(message, args));
    }

    public EBusConfigurationReaderException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public EBusConfigurationReaderException(final String message, final Throwable cause, final Object... args) {
        super(String.format(message, args), cause);
    }
}
