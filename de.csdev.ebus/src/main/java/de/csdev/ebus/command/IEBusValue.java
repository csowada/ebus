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
import java.util.Map;

import de.csdev.ebus.cfg.datatypes.IEBusType;

/**
 * @author Christian Sowada
 *
 */
public interface IEBusValue {

    public IEBusType getType();

    public void setType(IEBusType type);

    public Object getDefaultValue();

    public String getName();

    public Map<String, String> getMapping();

    public String getStep();

    public BigDecimal getFactor();

    public String getLabel();

    public BigDecimal getMax();

    public BigDecimal getMin();

    /**
     * Returns a map of additional properties
     *
     * @return
     */
    public Map<String, String> getProperties();

}
