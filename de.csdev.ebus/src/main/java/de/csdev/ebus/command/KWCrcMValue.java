/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command;

import java.math.BigDecimal;

import de.csdev.ebus.cfg.datatypes.IEBusType;

/**
 * @author Christian Sowada
 *
 */
public class KWCrcMValue implements IEBusValue {

    private IEBusType type;

    public KWCrcMValue(IEBusType type) {
        this.type = type;
    }

    public IEBusType getType() {
        return type;
    }

    public BigDecimal getDefaultValue() {
        return BigDecimal.valueOf(0);
    }

    public void setType(IEBusType type) {
        this.type = type;
    }

    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

}
