package com.github.kingschan1204.easycrawl.helper.map;

import java.util.HashMap;
import java.util.Map;

public class MapUtil<K, V> {
    Map<K, V> map;

    public MapUtil() {
        this.map = new HashMap<>();
    }

    public MapUtil<K, V> put(K key, V val) {
        this.map.put(key, val);
        return this;
    }

    public Map<K, V> getMap() {
        return this.map;
    }

}
