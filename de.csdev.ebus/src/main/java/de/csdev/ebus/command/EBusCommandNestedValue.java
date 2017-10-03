/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCommandNestedValue extends EBusCommandValue implements IEBusNestedValue {

    private List<IEBusValue> list;

    public boolean add(IEBusValue value) {

        if (list == null) {
            list = new ArrayList<IEBusValue>();
        }

        return list.add(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        EBusCommandNestedValue other = (EBusCommandNestedValue) obj;
        if (list == null) {
            if (other.list != null) {
                return false;
            }
        } else if (!list.equals(other.list)) {
            return false;
        }
        return true;
    }

    @Override
    public List<IEBusValue> getChildren() {

        if (list == null) {
            list = new ArrayList<IEBusValue>();
        }

        return list;
    }

    @Override
    public boolean hasChildren() {

        return list != null && !list.isEmpty();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((list == null) ? 0 : list.hashCode());

        // System.out.println("EBusCommandNestedValue.hashCode() " + result);

        return result;
    }

    @Override
    public String toString() {
        return "EBusCommandNestedValue [list=" + list + "]";
    }

}
