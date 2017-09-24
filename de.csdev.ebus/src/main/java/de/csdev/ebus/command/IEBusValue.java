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

import de.csdev.ebus.command.datatypes.IEBusType;

/**
 * An IEBusValue represents one value from a raw eBUS telegram.
 *
 * @author Christian Sowada - Initial contribution
 *
 */
public interface IEBusValue extends Cloneable {

    /**
     * @return
     */
    public IEBusType<?> getType();

    /**
     * @return
     */
    public Object getDefaultValue();

    /**
     * @return
     */
    public String getName();

    /**
     * @return
     */
    public Map<String, String> getMapping();

    /**
     * @return
     */
    public BigDecimal getStep();

    /**
     * @return
     */
    public BigDecimal getFactor();

    /**
     * @return
     */
    public String getLabel();

    /**
     * @return
     */
    public BigDecimal getMax();

    /**
     * @return
     */
    public BigDecimal getMin();

    /**
     * @return
     */
    public String getFormat();

    /**
     * Returns a map of additional properties
     *
     * @return
     */
    public Map<String, Object> getProperties();

    /**
     * @return
     */
    public IEBusCommandMethod getParent();

}
