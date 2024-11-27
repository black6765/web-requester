package com.blue.requester.service;

import com.blue.requester.domain.dto.EnvironmentDTO;
import com.blue.requester.repository.CollectionRepository;
import com.blue.requester.repository.EnvironmentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@AllArgsConstructor
@Slf4j
public class EnvironmentService {

    private final EnvironmentRepository environmentRepository;
    private final CollectionRepository collectionRepository;

    public EnvironmentDTO selectEnv() {
        return new EnvironmentDTO(collectionRepository.getCollectionsStore(), environmentRepository.getEnvNames(), EnvironmentRepository.GLOBAL_ENV_NAME, null);
    }


    public EnvironmentDTO sendEnvMap(String envName) {
        Map<String, String> variables = environmentRepository.getEnvStore().get(envName);

        if ("NEW_ENVIRONMENT".equals(envName)) {
            envName = "";
        }

        if (EnvironmentRepository.GLOBAL_ENV_NAME.equals(envName)) {
            variables = environmentRepository.getGlobalVariables();
        }

        return new EnvironmentDTO(collectionRepository.getCollectionsStore(), List.of(envName), EnvironmentRepository.GLOBAL_ENV_NAME, variables);
    }

    public EnvironmentDTO activateEnv(String activateEnvName) {
        environmentRepository.setCurrentEnvName(activateEnvName);
        return new EnvironmentDTO(collectionRepository.getCollectionsStore(), environmentRepository.getEnvNames(), EnvironmentRepository.GLOBAL_ENV_NAME, null);
    }

    public EnvironmentDTO saveEnv(String envName, List<String> variableKeys, List<String> variableValues) {
        Map<String, String> variables = convertTwoListToMap(variableKeys, variableValues);
        environmentRepository.save(envName, variables);
        return new EnvironmentDTO(collectionRepository.getCollectionsStore(), environmentRepository.getEnvNames(), EnvironmentRepository.GLOBAL_ENV_NAME, null);
    }

    public EnvironmentDTO deleteEnv(String deleteEnvName) {
        environmentRepository.getEnvStore().remove(deleteEnvName);
        return new EnvironmentDTO(collectionRepository.getCollectionsStore(), environmentRepository.getEnvNames(), EnvironmentRepository.GLOBAL_ENV_NAME, null);

    }

    private Map<String, String> convertTwoListToMap(List<String> variableKeys, List<String> variableValues) {
        Map<String, String> variables = new TreeMap<>();

        if (variableKeys != null && variableValues != null && !variableKeys.isEmpty() && !variableValues.isEmpty()) {
            for (int i = 0; i < variableKeys.size(); i++) {
                if (!ObjectUtils.isEmpty(variableKeys.get(i)) && !ObjectUtils.isEmpty(variableValues.get(i))) {
                    variables.put(variableKeys.get(i), variableValues.get(i));
                }
            }
        }
        return variables;
    }
}
