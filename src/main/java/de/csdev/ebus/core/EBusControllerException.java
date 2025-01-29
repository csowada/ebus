/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core;

/**
 * Invalid controller state, thread interrupted
 *
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusControllerException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public EBusControllerException() {
        super();
    }

    public EBusControllerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public EBusControllerException(final String message) {
        super(message);
    }

    public EBusControllerException(final Throwable cause) {
        super(cause);
    }

}
