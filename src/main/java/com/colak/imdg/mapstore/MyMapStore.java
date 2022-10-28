package com.colak.imdg.mapstore;

import com.hazelcast.map.MapStore;

import java.util.Collection;
import java.util.Map;

public class MyMapStore implements MapStore<Long, String> {
    @Override
    public void store(Long key, String value) {

    }

    @Override
    public void storeAll(Map<Long, String> map) {

    }

    @Override
    public void delete(Long key) {

    }

    @Override
    public void deleteAll(Collection<Long> keys) {

    }

    @Override
    public String load(Long key) {
        return "null";
    }

    @Override
    public Map<Long, String> loadAll(Collection<Long> keys) {
        return null;
    }

    @Override
    public Iterable<Long> loadAllKeys() {
        return null;
    }
}
