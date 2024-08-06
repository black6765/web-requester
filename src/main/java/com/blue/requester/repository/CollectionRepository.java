package com.blue.requester.repository;

import com.blue.requester.dto.CollectionDTO;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CollectionRepository {

    Map<String, CollectionDTO> collectionsStore = new LinkedHashMap<>();

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
