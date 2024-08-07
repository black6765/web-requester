package com.blue.requester.service;

import com.blue.requester.repository.CollectionRepository;
import com.blue.requester.repository.EnvironmentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class EnvironmentService {

    private final EnvironmentRepository environmentRepository;
    private final CollectionRepository collectionRepository;

    public String selectEnv(Model model) {
        model.addAttribute("collections", collectionRepository.getCollectionsStore());
        model.addAttribute("envNames", environmentRepository.getEnvNames());
        model.addAttribute("exceptEnvName", EnvironmentRepository.GLOBAL_ENV_NAME);
        return "selectEnv";
    }


    public String sendEnvMap(Model model, String envName) {
        Map<String, String> variables = environmentRepository.getEnvStore().get(envName);

        if ("NEW_ENVIRONMENT".equals(envName)) {
            envName = "";
        }

        if (EnvironmentRepository.GLOBAL_ENV_NAME.equals(envName)) {
            variables = environmentRepository.getGlobalVariables();
        }

        model.addAttribute("collections", collectionRepository.getCollectionsStore());
        model.addAttribute("envName", envName);
        model.addAttribute("variables", variables);
        model.addAttribute("exceptEnvName", EnvironmentRepository.GLOBAL_ENV_NAME);

        return "setEnv";
    }

    public String activateEnv(Model model, String activateEnvName) {
        environmentRepository.setCurrentEnvName(activateEnvName);

        model.addAttribute("collections", collectionRepository.getCollectionsStore());
        model.addAttribute("envNames", environmentRepository.getEnvNames());
        model.addAttribute("exceptEnvName", EnvironmentRepository.GLOBAL_ENV_NAME);

        return "selectEnv";
    }

    public String saveEnv(Model model, String envName, List<String> variableKeys, List<String> variableValues) {
        Map<String, String> variables = convertTwoListToMap(variableKeys, variableValues);

        environmentRepository.save(envName, variables);

        List<String> envNames = new ArrayList<>(environmentRepository.getEnvStore().keySet());

        model.addAttribute("collections", collectionRepository.getCollectionsStore());
        model.addAttribute("envNames", envNames);
        model.addAttribute("exceptEnvName", EnvironmentRepository.GLOBAL_ENV_NAME);

        return "selectEnv";
    }

    public String deleteEnv(Model model, String deleteEnvName) {
        environmentRepository.getEnvStore().remove(deleteEnvName);
        List<String> envNames = new ArrayList<>(environmentRepository.getEnvStore().keySet());

        model.addAttribute("collections", collectionRepository.getCollectionsStore());
        model.addAttribute("envNames", envNames);
        model.addAttribute("exceptEnvName", EnvironmentRepository.GLOBAL_ENV_NAME);

        return "selectEnv";
    }

    private Map<String, String> convertTwoListToMap(List<String> variableKeys, List<String> variableValues) {
        Map<String, String> variables = new LinkedHashMap<>();

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
