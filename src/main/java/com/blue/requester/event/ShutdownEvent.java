package com.blue.requester.event;

import com.blue.requester.repository.EnvironmentRepository;
import com.blue.requester.service.CollectionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShutdownEvent implements ApplicationListener<ContextClosedEvent> {

    private final CollectionService collectionService;
    private final EnvironmentRepository environmentRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {

        log.info("Application SHOTDOWN");

        saveCollections();
        saveEnvs();
    }

    private void saveCollections() {
        try {
            String collectionsFilePath = "config/collections.json";
            BufferedWriter bw = new BufferedWriter(new FileWriter(collectionsFilePath));
            bw.write(collectionService.convertCurrentCollectionsToJson());
            bw.close();

            log.info("Collections saved [{}]", System.getProperty("user.dir") + collectionsFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveEnvs() {
        try {
            String envsFilePath = "config/environments.json";
            BufferedWriter bw = new BufferedWriter(new FileWriter(envsFilePath));

            String environments = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(environmentRepository.getEnvStore());
            log.info(environments);
            bw.write(environments);
            bw.close();

            log.info("Environments saved [{}]", System.getProperty("user.dir") + envsFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
