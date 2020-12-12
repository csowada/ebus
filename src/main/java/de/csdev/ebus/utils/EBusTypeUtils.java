/**
 * Copyright (c) 2016-2020 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.utils;

import java.math.BigDecimal;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import de.csdev.ebus.command.datatypes.EBusTypeException;

/**
 *
 * @author Christian Sowada - Initial contribution
 */
public class EBusTypeUtils {

    /**
     *
     * @param obj
     * @return
     * @throws EBusTypeException
     */
    public static @NonNull BigDecimal toBigDecimal(@Nullable Object obj) throws EBusTypeException {
        BigDecimal result = NumberUtils.toBigDecimal(obj);
        if (result == null) {
            throw new EBusTypeException();

        }
        return result;
    }

}
