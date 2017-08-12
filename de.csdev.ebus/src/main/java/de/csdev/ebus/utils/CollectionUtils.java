package de.csdev.ebus.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
}
