/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes;

import java.text.MessageFormat;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusTypeException extends Exception {

    private static final long serialVersionUID = 7105215995176921667L;

    public EBusTypeException() {
        super();
    }

    public EBusTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public EBusTypeException(String message) {
        super(message);
    }

    public EBusTypeException(String message, Object... args) {
        super(MessageFormat.format(message, args));
    }

    public EBusTypeException(Throwable cause) {
        super(cause);
    }

}
