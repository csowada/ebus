/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.command.datatypes;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public abstract class EBusTypeGeneric<T> implements IEBusType<T> {

    private static final Logger logger = LoggerFactory.getLogger(EBusTypeGeneric.class);

    protected Map<Object, EBusTypeGeneric<T>> otherInstances = new HashMap<Object, EBusTypeGeneric<T>>();

    protected EBusTypeRegistry types;

    protected boolean reverseByteOrder = false;

    protected byte[] applyByteOrder(byte[] data) {

        // reverse the byte order immutable
        if (reverseByteOrder) {
            data = ArrayUtils.clone(data);
            ArrayUtils.reverse(data);
        }

        return data;
    }

    @Override
    public int getTypeLenght() {
        return 1;
    }

    @Override
    public void setTypesParent(EBusTypeRegistry types) {
        this.types = types;
    }

    @Override
    public IEBusType<T> getInstance(Map<String, Object> properties) {

        if (isReverseByteOrderSet(properties)) {

            // store this instance in our map
            EBusTypeGeneric<T> reversedInstance = otherInstances.get(IEBusType.REVERSED_BYTE_ORDER);
            if (reversedInstance == null) {
                reversedInstance = createNewInstance();
                applyNewInstanceProperties(reversedInstance, properties);
                otherInstances.put(IEBusType.REVERSED_BYTE_ORDER, reversedInstance);
            }
            return reversedInstance;
        }

        return this;
    }

    /**
     * @param properties
     * @return
     */
    protected boolean isReverseByteOrderSet(Map<String, Object> properties) {
        if (properties != null && BooleanUtils.isTrue((Boolean) properties.get(IEBusType.REVERSED_BYTE_ORDER))) {
            return true;
        }
        return false;
    }

    /**
     * @param instance
     * @param properties
     */
    protected void applyNewInstanceProperties(EBusTypeGeneric<T> instance, Map<String, Object> properties) {
        if (isReverseByteOrderSet(properties)) {
            instance.reverseByteOrder = true;
        }
    }

    /**
     * @return
     */
    protected EBusTypeGeneric<T> createNewInstance() {

        try {
            @SuppressWarnings("unchecked")
            EBusTypeGeneric<T> newInstance = this.getClass().newInstance();
            newInstance.types = this.types;
            return newInstance;

        } catch (InstantiationException e) {
            logger.error("error!", e);
        } catch (IllegalAccessException e) {
            logger.error("error!", e);
        }

        return null;
    }

    @Override
    public String toString() {
        return "EBusTypeGeneric []";
    }

}
