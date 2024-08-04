package com.blue.requester.service;

import com.blue.requester.dto.CollectionDTO;
import com.blue.requester.dto.ItemDTO;
import com.blue.requester.repository.CollectionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final ObjectMapper objectMapper;

    public String convertCurrentCollectionsToJson() throws JsonProcessingException {
        String collections = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(collectionRepository.getCollectionsStore());
        log.info(collections);
        return collections;
    }

    public Map<String, CollectionDTO> convertJsonToCollectionsMapAndNewStore(String json) throws IOException {
        Map<String, CollectionDTO> store = objectMapper.readValue(json, new TypeReference<>(){});
        collectionRepository.newStore(store);
        log.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
        return store;
    }
}
