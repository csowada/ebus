/**
 * Copyright (c) 2017-2021 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class EBusCommandNestedValue extends EBusCommandValue implements IEBusNestedValue {

    private @NonNull List<@NonNull IEBusValue> list = new ArrayList<>();

    @Override
    public void setParent(@Nullable EBusCommandMethod parent) {
        if (parent != null) {
            super.setParent(parent);

            for (IEBusValue value : list) {
                ((EBusCommandValue) value).setParent(parent);
            }
        }
    }

    public boolean add(@NonNull IEBusValue value) {
        Objects.requireNonNull(value, "value");
        return list.add(value);
    }

    @Override
    public @NonNull List<@NonNull IEBusValue> getChildren() {
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
    public @NonNull EBusCommandValue getClonedInstance() {
        EBusCommandNestedValue clone = (EBusCommandNestedValue) super.getClonedInstance();

        // deep clone list
        for (IEBusValue value : list) {
            clone.add(value.getClonedInstance());
        }

        return clone;
    }

    @Override
    public String toString() {
        return "EBusCommandNestedValue [list=" + list + "]";
    }

}
