package com.blue.requester.controller;

import com.blue.requester.repository.EnvironmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@AllArgsConstructor
public class EnvironmentController {

    private final EnvironmentRepository environmentRepository;

    @GetMapping("/env/selectForm")
    public String selectEnv(Model model) {

        model.addAttribute("envNames", environmentRepository.getEnvNames());

        return "selectEnv";
    }

    @GetMapping("/env")
    public String sendEnvMap(Model model, @RequestParam("envName") String envName) {
        Map<String, String> variables = environmentRepository.getEnvStore().get(envName);

        if ("NEW_ENVIRONMENT".equals(envName)) {
            envName = "";
        }

        model.addAttribute("envName", envName);
        model.addAttribute("variables", variables);

        return "setEnv";
    }

    @PostMapping("/env/activation")
    public String activateEnv(Model model, @RequestParam("activateEnvName") String activateEnvName) {

        environmentRepository.setCurrentEnvName(activateEnvName);
        model.addAttribute("envNames", environmentRepository.getEnvNames());

        return "selectEnv";
    }

    @PostMapping("/env")
    public String saveEnv(Model model,
                         @RequestParam("envName") String envName,
                         @RequestParam("variableKey") List<String> variableKeys,
                         @RequestParam("variableValue") List<String> variableValues) {

        Map<String, String> variables = convertTwoListToMap(variableKeys, variableValues);

        environmentRepository.save(envName, variables);

        List<String> envNames = new ArrayList<>(environmentRepository.getEnvStore().keySet());
        model.addAttribute("envNames", envNames);

        return "selectEnv";
    }

    // Todo : 유틸로 빼서 다른 서비스에 있는 코드도 사용하도록 하기
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
