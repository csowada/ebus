/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.cfg.datatypes;

import java.util.Map;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public interface IEBusType {

    public <T> T decode(byte[] data) throws EBusTypeException;

    public byte[] encode(Object data) throws EBusTypeException;

    public String[] getSupportedTypes();

    public void setTypesParent(EBusTypes types);

    public int getTypeLenght();

    public IEBusType getInstance(Map<String, Object> properties);

}
