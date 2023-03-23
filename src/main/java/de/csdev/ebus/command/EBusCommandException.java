/**
 * Copyright (c) 2017-2023 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command;

import java.text.MessageFormat;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
@NonNullByDefault
public class EBusCommandException extends Exception {

    private static final long serialVersionUID = 7105215995176921667L;

    public EBusCommandException() {
        super();
    }

    public EBusCommandException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public EBusCommandException(final String message) {
        super(message);
    }

    public EBusCommandException(final String message, final Object... args) {
        super(MessageFormat.format(message, args));
    }

    public EBusCommandException(final Throwable cause) {
        super(cause);
    }

}
