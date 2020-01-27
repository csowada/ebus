/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
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
    protected EBusCommandNestedValue createInstance() {
        return new EBusCommandNestedValue();
    }

    @Override
    public EBusCommandValue clone() {
        EBusCommandNestedValue clone = (EBusCommandNestedValue) super.clone();

        // deep clone list
        for (IEBusValue value : list) {
            clone.add(value.clone());
        }

        return clone;
    }

    @Override
    public String toString() {
        return "EBusCommandNestedValue [list=" + list + "]";
    }

}
