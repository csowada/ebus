/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.utils;

import java.lang.reflect.Field;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Utility class to replace Apache Commons Lang FieldUtils
 * 
 * @author Christian Sowada - Initial contribution
 */
public final class FieldUtil {
    private FieldUtil() {
        // Utility class
    }

    /**
     * Gets an accessible Field by name, breaking scope if requested.
     */
    public static @Nullable Field getField(final Class<?> cls, String fieldName, boolean forceAccess) {
        if (cls == null) {
            throw new IllegalArgumentException("The class must not be null");
        }
        if (fieldName == null) {
            throw new IllegalArgumentException("The field name must not be null");
        }
        
        // check up the superclass hierarchy
        for (Class<?> acls = cls; acls != null; acls = acls.getSuperclass()) {
            try {
                final Field field = acls.getDeclaredField(fieldName);
                if (!field.isAccessible()) {
                    if (forceAccess) {
                        field.setAccessible(true);
                    } else {
                        return null;
                    }
                }
                return field;
            } catch (final NoSuchFieldException ignored) { 
                // ignore
            }
        }
        return null;
    }
}