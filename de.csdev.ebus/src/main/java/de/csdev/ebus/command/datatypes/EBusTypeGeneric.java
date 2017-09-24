/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes;

import java.util.Map;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public abstract class EBusTypeGeneric<T> implements IEBusType<T> {

    public int getTypeLenght() {
        return 1;
    }

    protected EBusTypeRegistry types;

    public void setTypesParent(EBusTypeRegistry types) {
        this.types = types;
    }

    public IEBusType<T> getInstance(Map<String, Object> properties) {
        return this;
    }

    @Override
    public String toString() {
        return "EBusTypeGeneric []";
    }

}
