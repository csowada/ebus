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
 * Utility class to replace Apache Commons Lang StringUtils
 * 
 * @author Christian Sowada - Initial contribution
 */
public final class StringUtil {
    private StringUtil() {
        // Utility class
    }

    /**
     * Checks if a String is empty ("") or null.
     */
    public static boolean isEmpty(@Nullable String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Checks if a String is not empty ("") and not null.
     */
    public static boolean isNotEmpty(@Nullable String str) {
        return !isEmpty(str);
    }

    /**
     * Returns either the passed in String, or if the String is empty or null, 
     * the value supplied by defaultStr.
     */
    public static @NonNull String defaultIfEmpty(@Nullable String str, @NonNull String defaultStr) {
        Objects.requireNonNull(defaultStr, "defaultStr must not be null");
        return isEmpty(str) ? defaultStr : Objects.requireNonNull(str);
    }

    /**
     * Checks if a CharSequence contains a search CharSequence irrespective of case.
     */
    public static boolean containsIgnoreCase(@Nullable String str, @Nullable String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        return str.toLowerCase().contains(searchStr.toLowerCase());
    }

    /**
     * Check if a String starts with a specified prefix (case-sensitive).
     */
    public static boolean startsWith(@Nullable String str, @Nullable String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        return str.startsWith(prefix);
    }

    /**
     * Repeat a String the specified number of times to form a new String.
     */
    public static @NonNull String repeat(@NonNull String str, int repeat) {
        Objects.requireNonNull(str, "str must not be null");
        if (repeat <= 0) {
            return "";
        }
        if (repeat == 1) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < repeat; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
}