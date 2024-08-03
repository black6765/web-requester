package com.blue.requester.service;

import com.blue.requester.dto.ItemDTO;
import com.blue.requester.repository.CollectionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final ObjectMapper objectMapper;

    public String convertCollectionsToJson() throws JsonProcessingException {
        String collections = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(collectionRepository.getStore());

        System.out.println(collections);

        return collections;
    }
}
