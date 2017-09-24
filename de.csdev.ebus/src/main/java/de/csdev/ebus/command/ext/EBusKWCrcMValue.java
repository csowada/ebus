/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.ext;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import de.csdev.ebus.command.EBusCommandMethod;
import de.csdev.ebus.command.IEBusCommandMethod;
import de.csdev.ebus.command.IEBusValue;
import de.csdev.ebus.command.datatypes.IEBusType;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusKWCrcMValue implements IEBusValue {

    private IEBusCommandMethod parent;

    private IEBusType<?> type;

    /**
     * @param type
     */
    public EBusKWCrcMValue(IEBusType<?> type) {
        this.type = type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.csdev.ebus.command.IEBusValue#getType()
     */
    public IEBusType<?> getType() {
        return type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.csdev.ebus.command.IEBusValue#getDefaultValue()
     */
    public BigDecimal getDefaultValue() {
        return BigDecimal.valueOf(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.csdev.ebus.command.IEBusValue#setType(de.csdev.ebus.command.datatypes.IEBusType)
     */
    public void setType(IEBusType<?> type) {
        this.type = type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.csdev.ebus.command.IEBusValue#getName()
     */
    public String getName() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.csdev.ebus.command.IEBusValue#getMapping()
     */
    public Map<String, String> getMapping() {
        return Collections.emptyMap();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.csdev.ebus.command.IEBusValue#getStep()
     */
    public BigDecimal getStep() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.csdev.ebus.command.IEBusValue#getFactor()
     */
    public BigDecimal getFactor() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.csdev.ebus.command.IEBusValue#getLabel()
     */
    public String getLabel() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.csdev.ebus.command.IEBusValue#getMax()
     */
    public BigDecimal getMax() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.csdev.ebus.command.IEBusValue#getMin()
     */
    public BigDecimal getMin() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.csdev.ebus.command.IEBusValue#getProperties()
     */
    public Map<String, Object> getProperties() {
        return Collections.emptyMap();

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.csdev.ebus.command.IEBusValue#getFormat()
     */
    public String getFormat() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "EBusKWCrcMValue [type=" + type + "]";
    }

    /**
     * @param parent
     */
    public void setParent(EBusCommandMethod parent) {
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.csdev.ebus.command.IEBusValue#getParent()
     */
    public IEBusCommandMethod getParent() {
        return parent;
    }

}
