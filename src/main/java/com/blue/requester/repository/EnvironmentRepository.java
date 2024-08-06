package com.blue.requester.repository;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class EnvironmentRepository {

    Map<String, Map<String, String>> envStore = new LinkedHashMap<>();

    public void newStore(Map<String, Map<String, String>> envs) {
        this.envStore = envs;

    }

    public void save(String envName, Map<String, String> env) {
        envStore.put(envName, env);
    }

    public Map<String, Map<String, String>> getEnvStore() {
        return envStore;
    }

}
