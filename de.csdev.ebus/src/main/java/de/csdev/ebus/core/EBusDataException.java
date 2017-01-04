/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.core;

import java.io.IOException;

import de.csdev.ebus.utils.EBusUtils;

/**
 * @author Christian Sowada
 *
 */
public class EBusDataException extends IOException {

    public enum EBusError {
        NONE,
        MASTER_CRC_INVALID,
        SLAVE_CRC_INVALID,
        NO_SLAVE_RESPONSE,
        UNEXSPECTED_RESPONSE,
        INDEX_OUT_OF_BOUNDS
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private EBusError error = EBusError.NONE;

    private byte[] data;

    public EBusDataException(String message) {
        super(message);
    }

    public EBusDataException(String message, EBusError errorCode) {
        super(message);
        this.error = errorCode;
    }

    public EBusDataException(String message, EBusError errorCode, byte[] data) {
        this(message, errorCode);
        this.data = data;
    }

    public EBusError getErrorCode() {
        return error;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String getMessage() {

        StringBuffer sb = new StringBuffer();

        sb.append(super.getMessage());
        sb.append(" [");
        if (!error.equals(EBusError.NONE)) {
            sb.append("ERROR: ");
            sb.append(error.name());
            sb.append(", ");
        }

        if (data != null) {
            sb.append("DATA: ");
            sb.append(EBusUtils.toHexDumpString(data));
        }
        sb.append("]");

        return sb.toString();
    }

}
