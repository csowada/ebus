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

    public boolean hasChildren() {

        return list != null && !list.isEmpty();
    }

    public List<IEBusValue> getChildren() {

        if (list == null) {
            list = new ArrayList<IEBusValue>();
        }

        return list;
    }

    public boolean add(IEBusValue value) {

        if (list == null) {
            list = new ArrayList<IEBusValue>();
        }

        return list.add(value);
    }

    @Override
    public String toString() {
        return "EBusCommandNestedValue [list=" + list + "]";
    }

}
