package com.blue.requester.controller;

import com.blue.requester.dto.CollectionDTO;
import com.blue.requester.repository.CollectionRepository;
import com.blue.requester.service.CollectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CollectionController {

    private final CollectionRepository collectionRepository;
    private final CollectionService collectionService;

    @PostMapping("/item")
    public String createItem(
            @RequestParam("collectionName") String collectionName,
            @RequestParam("workspaceName") String workspaceName,
            @RequestParam("itemName") String itemName,
            @RequestParam("url") String url,
            @RequestParam("httpMethod") String httpMethod,
            @RequestParam("body") String body,
            @RequestParam(value = "headersKey", required = false) List<String> headersKeys,
            @RequestParam(value = "headersValue", required = false) List<String> headersValues
    ) {
        return collectionService.createItem(collectionName, workspaceName, itemName, url, httpMethod, body, headersKeys, headersValues);
    }

    @PostMapping("/workspace")
    public String createWorkspace(@RequestParam("collectionName") String collectionName, @RequestParam("workspaceName") String workspaceName) {
        return collectionService.createWorkspace(collectionName, workspaceName);
    }

    @PostMapping("/collection")
    public String createCollection(@RequestParam("collectionName") String collectionName) {
        collectionRepository.save(collectionName, new CollectionDTO(collectionName, new LinkedHashMap<>()));
        return "redirect:/collectionForm";
    }

    @GetMapping("/collectionForm")
    public String collectionForm(Model model) {
        return collectionService.collectionForm(model);
    }

    @GetMapping("/workspaceNameList")
    @ResponseBody
    public List<String> getWorkspaceNameList(@RequestParam("collectionName") String collectionName) {
        return collectionService.getWorkspaceNameList(collectionName);
    }

}
