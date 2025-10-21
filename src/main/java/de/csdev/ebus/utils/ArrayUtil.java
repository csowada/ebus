/**
 * Copyright (c) 2017-2025 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.utils;

import java.util.Arrays;
import java.util.Objects;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Utility class to replace Apache Commons Lang ArrayUtils
 * @author Christian Sowada - Initial contribution
 */
public final class ArrayUtil {
    private ArrayUtil() {
        // Utility class
    }

    /**
     * Checks if an array is empty or null.
     */
    public static boolean isEmpty(byte @Nullable [] array) {
        return array == null || array.length == 0;
    }

    /**
     * Checks if an array is not empty and not null.
     */
    public static boolean isNotEmpty(byte @Nullable [] array) {
        return !isEmpty(array);
    }

    /**
     * Copies the specified array, truncating or padding with zeros (if necessary)
     * so the copy has the specified length.
     */
    public static byte[] copyOf(byte @Nullable [] original, int newLength) {
        Objects.requireNonNull(original, "original must not be null");
        return Arrays.copyOf(original, newLength);
    }

    /**
     * Reverses the order of the given array.
     */
    public static void reverse(byte @Nullable [] array) {
        Objects.requireNonNull(array, "array must not be null");
        int i = 0;
        int j = array.length - 1;
        byte tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    /**
     * Creates a new array containing the specified elements.
     */
    public static byte[] toPrimitive(@NonNull Byte[] array) {
        Objects.requireNonNull(array, "array must not be null");
        final byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i] != null ? array[i] : 0;
        }
        return result;
    }

    /**
     * Converts an array of primitive bytes to objects.
     */
    public static @NonNull Byte[] toObject(byte @Nullable [] array) {
        Objects.requireNonNull(array, "array must not be null");
        final Byte[] result = new Byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i];
        }
        return result;
    }

    /**
     * Creates a copy of an array, or returns null if the array is null.
     */
    public static byte @Nullable [] clone(byte @Nullable [] array) {
        return array == null ? null : Arrays.copyOf(array, array.length);
    }
}