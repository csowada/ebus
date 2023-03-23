/**
 * Copyright (c) 2017-2023 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class CollectionUtils {

    private CollectionUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static final @NonNull <T> List<@Nullable T> emptyList() {
        return  Collections.emptyList();
    }

    /**
     * A null checked variant of map.get
     *
     * @param map
     * @param key
     * @return
     */
    public static @Nullable <V, K> V get(@Nullable Map<K, V> map, K key) {
        return map != null ? map.get(key) : null;
    }

    /**
     * Returns a unmodifiable map or create one if <code>null</code>
     *
     * @param map
     * @return
     */
    public static @NonNull <K, V> Map<K, V> unmodifiableNotNullMap(@Nullable Map<K, V> map) {
        if (map == null) {
            Map<K, V> emptyMap = Collections.emptyMap();
            return Objects.requireNonNull(emptyMap);
        }

        return Objects.requireNonNull(Collections.unmodifiableMap(map));
    }

    /**
     * Returns the given map or create a new on eif <code>null</code>
     *
     * @param map
     * @return
     */
    public static <K, V> Map<K, V> newMapIfNull(@Nullable Map<K, V> map) {
        return map != null ? map : new HashMap<>();
    }

    /**
     * Creates a property map based on the given parameters. The first param is the key, the second the value, third key
     * etc. You need a even amount of parameters!
     *
     * @param args
     * @return
     */
    public static Map<String, Object> createProperties(Object... args) {
        Map<String, Object> properties = new HashMap<>();

        for (int i = 0; i < args.length; i = i + 2) {
            properties.put(args[i].toString(), args[i + 1]);
        }

        return properties;
    }
}
