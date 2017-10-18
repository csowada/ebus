/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
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

    public static <V, K> V get(Map<K, V> map, K key) {
        return map != null ? map.get(key) : null;
    }

    public static <K, V> Map<K, V> unmodifiableNotNullMap(Map<K, V> map) {
        if (map == null) {
            return Collections.emptyMap();
        }

        return Collections.unmodifiableMap(map);
    }

    public static <K, V> Map<K, V> newMapIfNull(Map<K, V> map) {
        return map != null ? map : new HashMap<K, V>();
    }

    public static Map<String, Object> createProps(Object... args) {
        Map<String, Object> properties = new HashMap<String, Object>();

        for (int i = 0; i < args.length; i = i + 2) {
            properties.put(args[i].toString(), args[i + 1]);
        }

        return properties;
    }
}
