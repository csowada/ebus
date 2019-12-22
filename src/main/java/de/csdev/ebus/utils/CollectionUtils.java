/**
 * Copyright (c) 2016-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.csdev.ebus.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Christian Sowada - Initial contribution
 *
 */
public class CollectionUtils {

    /**
     * A null checked variant of map.get
     *
     * @param map
     * @param key
     * @return
     */
    public static <V, K> V get(Map<K, V> map, K key) {
        return map != null ? map.get(key) : null;
    }

    /**
     * Returns a unmodifiable map or create one if <code>null</code>
     *
     * @param map
     * @return
     */
    public static <K, V> Map<K, V> unmodifiableNotNullMap(Map<K, V> map) {
        if (map == null) {
            return Collections.emptyMap();
        }

        return Collections.unmodifiableMap(map);
    }

    /**
     * Returns the given map or create a new on eif <code>null</code>
     *
     * @param map
     * @return
     */
    public static <K, V> Map<K, V> newMapIfNull(Map<K, V> map) {
        return map != null ? map : new HashMap<K, V>();
    }

    /**
     * Creates a property map based on the given parameters. The first param is the key, the second the value, third key
     * etc. You need a even amount of parameters!
     *
     * @param args
     * @return
     */
    public static Map<String, Object> createProperties(Object... args) {
        Map<String, Object> properties = new HashMap<String, Object>();

        for (int i = 0; i < args.length; i = i + 2) {
            properties.put(args[i].toString(), args[i + 1]);
        }

        return properties;
    }
}
