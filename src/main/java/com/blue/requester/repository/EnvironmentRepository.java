package com.blue.requester.repository;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
public class EnvironmentRepository {

    public EnvironmentRepository() {
        currentEnvName = "None";
        envStore.put(GLOBAL_ENV_NAME, new TreeMap<>());
    }

    private Map<String, Map<String, String>> envStore = new TreeMap<>();

    public static final String GLOBAL_ENV_NAME = "GLOBAL_ENVIRONMENT";

    public Map<String, String> getGlobalVariables() {
        return envStore.get(GLOBAL_ENV_NAME);
    }

    @Getter
    @Setter
    private String currentEnvName;

    public void newStore(Map<String, Map<String, String>> environments) {
        this.envStore = environments;
    }

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
