package edu.stanford.cs276.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Created by kavinyao on 4/27/14.
 */
public class MapUtility {
    // for convenience
    private static final Integer ZERO = 0;

    public static <T> void incrementCount(T key, Map<T, Integer> counts) {
        Integer count = counts.get(key);
        int val = count == null ? ZERO : count;
        counts.put(key, val+1);
    }

    public static <T, V> V getWithFallback(Map<T, V> map, T key, V defval) {
        V res = map.get(key);
        return res == null ? defval : res;
    }

    /**
     * Count occurrences of unique elements in collection.
     * @param collection
     * @param <T>
     * @return
     */
    public static <T> Map<T, Integer> count(Collection<T> collection) {
        Map<T, Integer> counts = new HashMap<>();

        for (T t : collection) {
            incrementCount(t, counts);
        }

        return counts;
    }

    /**
     * Apply op on each element in map.
     */
    public static <K, V> Map<K, V> map(Map<K, V> map, UnaryOperator<V> op) {
        Map<K, V> newMap = new HashMap<>(map);
        iMap(newMap, op);
        return newMap;
    }

    /**
     * Apply op on each element in map in place.
     */
    public static <K, V> Map<K, V> iMap(Map<K, V> map, UnaryOperator<V> op) {
        for (Map.Entry<K, V> e : map.entrySet()) {
            map.put(e.getKey(), op.apply(e.getValue()));
        }

        return map;
    }
}
