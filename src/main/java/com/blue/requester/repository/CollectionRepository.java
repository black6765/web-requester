package com.blue.requester.repository;

import com.blue.requester.dto.CollectionDTO;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.TreeMap;

@Component
public class CollectionRepository {

    private Map<String, CollectionDTO> collectionsStore = new TreeMap<>();

    public void newStore(Map<String, CollectionDTO> collections) {
        this.collectionsStore = collections;

    }

    public void save(String collectionName, CollectionDTO collection) {
        collectionsStore.put(collectionName, collection);
    }

    public Map<String, CollectionDTO> getCollectionsStore() {
        return collectionsStore;
    }
}
