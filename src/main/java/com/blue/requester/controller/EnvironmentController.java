package com.blue.requester.controller;

import com.blue.requester.repository.CollectionRepository;
import com.blue.requester.repository.EnvironmentRepository;
import com.blue.requester.service.EnvironmentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@AllArgsConstructor
public class EnvironmentController {

    private final EnvironmentService environmentService;

    @GetMapping("/env/selectForm")
    public String selectEnv(Model model) {
        return environmentService.selectEnv(model);
    }

    @GetMapping("/env")
    public String sendEnvMap(Model model, @RequestParam("envName") String envName) {
        return environmentService.sendEnvMap(model, envName);
    }

    @PostMapping("/env/activation")
    public String activateEnv(Model model, @RequestParam("activateEnvName") String activateEnvName) {
        return environmentService.activateEnv(model, activateEnvName);
    }

    @PostMapping("/env")
    public String saveEnv(Model model,
                         @RequestParam("envName") String envName,
                         @RequestParam("variableKey") List<String> variableKeys,
                         @RequestParam("variableValue") List<String> variableValues) {

        return environmentService.saveEnv(model, envName, variableKeys, variableValues);
    }

    @DeleteMapping("/env")
    public String deleteEnv(Model model, @RequestParam("deleteEnvName") String deleteEnvName) {
        return environmentService.deleteEnv(model, deleteEnvName);
    }

}
