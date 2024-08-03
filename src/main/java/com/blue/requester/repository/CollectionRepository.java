package com.blue.requester.repository;

import com.blue.requester.dto.ItemDTO;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CollectionRepository {

    Map<String, Map<String, Map<String, ItemDTO>>> store = new LinkedHashMap<>();

    public void save(String collectionName, Map<String, Map<String, ItemDTO>> data) {
        store.put(collectionName, data);
    }

    public  Map<String, Map<String, Map<String, ItemDTO>>> getStore() {
        return store;
    }
}
