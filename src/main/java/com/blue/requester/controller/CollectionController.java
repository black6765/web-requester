package com.blue.requester.controller;

import com.blue.requester.repository.CollectionRepository;
import com.blue.requester.service.CollectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CollectionController {

    private final CollectionRepository collectionRepository;
    private final CollectionService collectionService;

    @GetMapping("/collection/createForm")
    public String collectionForm(Model model) {
        return collectionService.collectionForm(model);
    }

    @PostMapping("/collection")
    public String createCollection(@RequestParam("collectionName") String collectionName) {
        return collectionService.createCollection(collectionName);
    }

    @PostMapping("/workspace")
    public String createWorkspace(@RequestParam("collectionName") String collectionName, @RequestParam("workspaceName") String workspaceName) {
        return collectionService.createWorkspace(collectionName, workspaceName);
    }

    @PostMapping("/item")
    public String createItem(
            @RequestParam("collectionName") String collectionName,
            @RequestParam("workspaceName") String workspaceName,
            @RequestParam("itemName") String itemName,
            @RequestParam("url") String url,
            @RequestParam("httpMethod") String httpMethod,
            @RequestParam("contentType") String contentType,
            @RequestParam("body") String body,
            @RequestParam(value = "headersKey", required = false) List<String> headersKeys,
            @RequestParam(value = "headersValue", required = false) List<String> headersValues,
            @RequestParam("selectedHeaderIndexes") List<Integer> selectedHeaderIndexes) {
        collectionService.createItem(collectionName, workspaceName, itemName, url, httpMethod, body, headersKeys, headersValues, contentType, selectedHeaderIndexes);
        return "redirect:/collection/createForm";
    }

    @GetMapping("/collection/deleteForm")
    public String deleteCollection(Model model) {
        model.addAttribute("collections", collectionRepository.getCollectionsStore());
        return "deleteCollectionForm";
    }

    @DeleteMapping("/collection")
    public String deleteCollection(
            @RequestParam("collectionSelect") String collection,
            @RequestParam(value = "workspaceSelect", required = false) String workspace,
            @RequestParam(value = "itemSelect", required = false) String item) {

        return collectionService.deleteCollection(collection, workspace, item);
    }

    @GetMapping("/collection/renameForm")
    public String renameCollection(Model model) {
        model.addAttribute("collections", collectionRepository.getCollectionsStore());
        return "renameCollectionForm";
    }

    @PutMapping("/collection/rename")
    public String renameCollection(
            @RequestParam("collectionSelect") String collection,
            @RequestParam(value = "workspaceSelect", required = false) String workspace,
            @RequestParam(value = "itemSelect", required = false) String item,
            @RequestParam(value = "name") String name) {
        collectionService.renameCollection(collection, workspace, item, name);
        return "renameCollectionForm";
    }

    @GetMapping("/collection/copyForm")
    public String copyCollection(Model model) {
        model.addAttribute("collections", collectionRepository.getCollectionsStore());
        return "copyCollectionForm";
    }

    @PutMapping("/collection/copy")
    public String copyCollection(
            @RequestParam("collectionSelect") String collection,
            @RequestParam(value = "workspaceSelect", required = false) String workspace,
            @RequestParam(value = "itemSelect", required = false) String item,
            @RequestParam(value = "name") String name) {
        collectionService.copyCollection(collection, workspace, item, name);
        return "copyCollectionForm";
    }

    @GetMapping("/collectionNameList")
    @ResponseBody
    public List<String> getCollectionsList() {
        return collectionService.getCollectionNameList();
    }

    @GetMapping("/workspaceNameList")
    @ResponseBody
    public List<String> getWorkspaceNameList(@RequestParam("collectionName") String collectionName) {
        return collectionService.getWorkspaceNameList(collectionName);
    }

    @GetMapping("/itemNameList")
    @ResponseBody
    public List<String> getItemeList(@RequestParam("collectionName") String collectionName,
                                     @RequestParam("workspaceName") String workspaceName) {
        return collectionService.getItemNameList(collectionName, workspaceName);
    }

}
