/**
 * Copyright (c) 2016-2018 by the respective copyright holders.
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
     * Returns the type of this value
     *
     * @return
     */
    public IEBusType<?> getType();

    /**
     * Returns the default value if set, can be <code>null</code>
     *
     * @return
     */
    public Object getDefaultValue();

    /**
     * Returns the name of the value, can be <code>null</code>
     *
     * @return
     */
    public String getName();

    /**
     * Returns the mapping, can be <code>null</code>
     *
     * @return
     */
    public Map<String, String> getMapping();

    /**
     * Returns the allowed step width of the value, can be <code>null</code>
     *
     * @return
     */
    public BigDecimal getStep();

    /**
     * Returns the factor of the value, can be <code>null</code>
     *
     * @return
     */
    public BigDecimal getFactor();

    /**
     * Returns the label of the value, can be <code>null</code>
     *
     * @return
     */
    public String getLabel();

    /**
     * Returns the allowed max of the value, can be <code>null</code>
     *
     * @return
     */
    public BigDecimal getMax();

    /**
     * Returns the allowed min of the value, can be <code>null</code>
     *
     * @return
     */
    public BigDecimal getMin();

    /**
     * Returns the formatter, can be <code>null</code>
     *
     * @return
     * @see String#format(String, Object...)
     */
    public String getFormat();

    /**
     * Returns a map of additional properties, can be <code>null</code>
     *
     * @return
     */
    public Map<String, Object> getProperties();

    /**
     * Returns the parent command method
     *
     * @return
     */
    public IEBusCommandMethod getParent();

}
