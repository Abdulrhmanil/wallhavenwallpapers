package com.abdulrhmanil.wallhavenwallpapers.datastructures;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Map with fixed size that can't change, you must determine the size when you create the map.
 * Map that build for the caching purpose, behave like queue, when reach the max size, and you
 * try add a new element, the map will remove the oldest one, and then enter the new element.
 * We use this Map in a matter to cache the photos, while NOT to fill the memory RAM.
 * @param <K> is the key that you want to enter in the map, to search the value with later.
 * @param <V> is the value of the key you want to save to search it later.
 */
public class OptimizedMap<K, V> extends LinkedHashMap<K, V> {

    /** The load factor of the map, when to resize the map, we don't use it, just pass it to super*/
    final private static float loadFactor = 0.75f;


    /** The max elements you can enter in the map, before start removing the oldest*/
    private final int maxSize;



    /**
     * Constructor that init and create an instance of OptimizedMap.
     * @param maxSize is the max elements you can enter in the map, before start removing the oldest
     */
    public OptimizedMap(int maxSize) {
        super(calculateInitialCapacity(maxSize, loadFactor));
        this.maxSize = maxSize;
    }


    /**
     * Put method that enter a new pair (key, value) to the OptimizedMap, if we didn't reach the
     * max count of elements, if we did, then the we remove the oldest pair, and then we enter the
     * new pair (K key, V value).
     * @param key key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     * @return the previous value associated with key, or null if there was no mapping for key.
     * (A null return can also indicate that the map previously associated null with key.)
     */
    @Override
    public V put(K key, V value) {
        if (size() >= maxSize) {
            K firstElement = entrySet().iterator().next().getKey();
            remove(firstElement);
        }
        return super.put(key, value);
    }



    /**
     * Copies all of the mappings from the specified map to this map.
     * These mappings will replace any mappings that this map had for
     * any of the keys currently in the specified map.
     *
     * @param m mappings to be stored in this map
     * @throws NullPointerException if the specified map is null
     * @throws RuntimeException if the size of m is grater than {@link #maxSize}
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        if (m.size() > maxSize){
            throw new RuntimeException("The size of m must be less than maxSize");
        }
        super.putAll(m);
    }



    /**
     * Static util method that calculate the initial capacity of the the Map.
     * Notice: the initial capacity size different from the the max size,
     * the max size is the max count of elements that the map can contain logically,
     * when the initial capacity is the real size of the map table, that distribute
     * the keys in that table.
     * @param maxSize is the max count of elements that the map can contain logically.
     * @param loadFactor is load factor of the map.
     * @return the initial capacity, the real size of the map table.
     */
    private static int calculateInitialCapacity(int maxSize, float loadFactor) {
        return (int) (((maxSize+1)/loadFactor)+2);
    }
}

