package com.blue.requester.event;

import com.blue.requester.domain.dto.CollectionDTO;
import com.blue.requester.repository.EnvironmentRepository;
import com.blue.requester.service.CollectionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartEvent implements ApplicationListener<ContextRefreshedEvent> {

    private final CollectionService collectionService;
    private final EnvironmentRepository environmentRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        getCollectionsWhenStart();
        getEnvsWhenStart();
    }

    private void getCollectionsWhenStart() {
        try {
            String collectionsFilePath = "config/collections.json";
            BufferedReader br = new BufferedReader(new FileReader(collectionsFilePath));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);

            String collectionsFile = sb.toString();
            Map<String, CollectionDTO> store = collectionService.convertJsonToCollectionsMapAndNewStore(collectionsFile);
            log.info("Load collections in {} success.\n"
                    + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(store)
                    + "\n================= End of Collections Info =================", System.getProperty("user.dir") + collectionsFilePath);

        } catch (FileNotFoundException e) {
            log.info("Collections file not found");
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private void getEnvsWhenStart() {
        try {
            String envsFilePath = "config/environments.json";
            BufferedReader br = new BufferedReader(new FileReader(envsFilePath));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);

            String envsFile = sb.toString();
            TypeReference<TreeMap<String, Map<String, String>>> typeReference = new TypeReference<>() {
            };
            Map<String, Map<String, String>> store = objectMapper.readValue(envsFile, typeReference);

            environmentRepository.newStore(store);

            log.info("Load environments in {} success.\n"
                    + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(store)
                    + "\n================= End of Environments Info =================", System.getProperty("user.dir") + envsFilePath);

        } catch (FileNotFoundException e) {
            log.info("Environments file not found");
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
