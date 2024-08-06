package com.blue.requester.repository;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class EnvironmentRepository {

    public EnvironmentRepository() {
        currentEnvName = "None";
    }

    private final Map<String, Map<String, String>> envStore = new LinkedHashMap<>();

    @Getter
    @Setter
    private String currentEnvName;

    public void save(String envName, Map<String, String> env) {
        envStore.put(envName, env);
    }

    public Map<String, Map<String, String>> getEnvStore() {
        return envStore;
    }

    public Map<String, String> getCurrentEnvVariables() {
        return envStore.get(currentEnvName);
    }

    public List<String> getEnvNames() {
        return new ArrayList<>(envStore.keySet());
    }
}
