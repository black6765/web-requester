package com.blue.requester.controller;

import com.blue.requester.domain.dto.EnvironmentDTO;
import com.blue.requester.service.EnvironmentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
public class EnvironmentController {

    private final EnvironmentService environmentService;

    @GetMapping("/env/selectForm")
    public String selectEnv(Model model) {
        EnvironmentDTO environmentDTO = environmentService.selectEnv();
        model.addAttribute("collections", environmentDTO.getCollections());
        model.addAttribute("envNames", environmentDTO.getEnvNames());
        model.addAttribute("exceptEnvName", environmentDTO.getExceptEnvName());
        return "selectEnv";
    }

    @GetMapping("/env")
    public String sendEnvMap(Model model, @RequestParam("envName") String envName) {
        EnvironmentDTO environmentDTO = environmentService.sendEnvMap(envName);

        model.addAttribute("collections", environmentDTO.getCollections());
        model.addAttribute("envName", environmentDTO.getEnvNames().get(0));
        model.addAttribute("variables", environmentDTO.getVariables());
        model.addAttribute("exceptEnvName", environmentDTO.getExceptEnvName());

        return "setEnv";

    }

    @PostMapping("/env/activation")
    public String activateEnv(Model model, @RequestParam("activateEnvName") String activateEnvName) {
        EnvironmentDTO environmentDTO = environmentService.activateEnv(activateEnvName);
        model.addAttribute("collections", environmentDTO.getCollections());
        model.addAttribute("envNames", environmentDTO.getEnvNames());
        model.addAttribute("exceptEnvName", environmentDTO.getExceptEnvName());
        return "selectEnv";
    }

    @PostMapping("/env")
    public String saveEnv(Model model,
                          @RequestParam("envName") String envName,
                          @RequestParam("variableKey") List<String> variableKeys,
                          @RequestParam("variableValue") List<String> variableValues) {

        EnvironmentDTO environmentDTO = environmentService.saveEnv(envName, variableKeys, variableValues);
        model.addAttribute("collections", environmentDTO.getCollections());
        model.addAttribute("envNames", environmentDTO.getEnvNames());
        model.addAttribute("exceptEnvName", environmentDTO.getExceptEnvName());
        return "selectEnv";
    }

    @DeleteMapping("/env")
    public String deleteEnv(Model model, @RequestParam("deleteEnvName") String deleteEnvName) {
        EnvironmentDTO environmentDTO = environmentService.deleteEnv(deleteEnvName);
        model.addAttribute("collections", environmentDTO.getCollections());
        model.addAttribute("envNames", environmentDTO.getEnvNames());
        model.addAttribute("exceptEnvName", environmentDTO.getExceptEnvName());
        return "selectEnv";
    }

}
