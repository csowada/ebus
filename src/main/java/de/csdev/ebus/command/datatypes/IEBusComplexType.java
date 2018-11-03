/**
 * Copyright (c) 2016-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public interface IEBusComplexType<T> {

    /**
     * Allows to decode complex values with the complete telegram
     *
     * @param rawData
     * @param pos
     * @return
     * @throws EBusTypeException
     */
    public T decodeComplex(byte[] rawData, int pos) throws EBusTypeException;

    /**
     * ???
     *
     * @param data
     * @return
     * @throws EBusTypeException
     */
    public byte[] encodeComplex(Object data) throws EBusTypeException;
}
