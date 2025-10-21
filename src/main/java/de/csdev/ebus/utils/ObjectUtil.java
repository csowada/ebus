/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.utils;

import java.util.Objects;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Utility class to replace Apache Commons Lang ObjectUtils
 */
public final class ObjectUtil {
    private ObjectUtil() {
        // Utility class
    }

    /**
     * Returns the first non-null value in the array
     */
    @SafeVarargs
    public static <T> @Nullable T firstNonNull(@Nullable T... values) {
        if (values == null) {
            return null;
        }
        for (T val : values) {
            if (val != null) {
                return val;
            }
        }
        return null;
    }

    /**
     * Compares two objects for equality, handling nulls
     */
    public static boolean equals(@Nullable Object a, @Nullable Object b) {
        return Objects.equals(a, b);
    }

    /**
     * Returns a default value if the object passed is null
     */
    public static <T> @NonNull T defaultIfNull(@Nullable T object, @NonNull T defaultValue) {
        Objects.requireNonNull(defaultValue, "defaultValue must not be null");
        return object != null ? object : defaultValue;
    }

    /**
     * Returns a hash code for an object, handling null
     */
    public static int hashCode(@Nullable Object obj) {
        return obj != null ? obj.hashCode() : 0;
    }

    /**
     * Gets a toString for the object, handling null
     */
    public static @NonNull String toString(@Nullable Object obj) {
        return obj != null ? obj.toString() : "null";
    }
}