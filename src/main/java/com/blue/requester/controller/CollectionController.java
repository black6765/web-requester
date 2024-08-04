package com.blue.requester.controller;

import com.blue.requester.dto.CollectionDTO;
import com.blue.requester.dto.ItemDTO;
import com.blue.requester.dto.WorkspaceDTO;
import com.blue.requester.repository.CollectionRepository;
import com.blue.requester.service.CollectionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CollectionController {

    private final CollectionRepository collectionRepository;
    private final CollectionService collectionService;

    @PostMapping("/createItem")
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
        Map<String, String> headers = new LinkedHashMap<>();

        if (headersKeys != null && headersValues != null && !headersKeys.isEmpty() && !headersValues.isEmpty()) {
            for (int i = 0; i < headersKeys.size(); i++) {
                if (!ObjectUtils.isEmpty(headersKeys.get(i)) && !ObjectUtils.isEmpty(headersValues.get(i))) { // 키와 값이 비어있지 않다면
                    headers.put(headersKeys.get(i), headersValues.get(i));
                }
            }
        }

        ItemDTO itemDTO = new ItemDTO(itemName, workspaceName, url, httpMethod, headers, body);
        collectionRepository.getCollectionsStore().get(collectionName).getWorkspaces().get(workspaceName).getItems().put(itemName, itemDTO);

        return "redirect:/collection";
    }

    @PostMapping("/createWorkspace")
    public String createWorkspace(@RequestParam("collectionName") String collectionName, @RequestParam("workspaceName") String workspaceName) {
        collectionRepository.getCollectionsStore().get(collectionName).getWorkspaces().put(workspaceName, new WorkspaceDTO(workspaceName, collectionName, new LinkedHashMap<>()));
        return "redirect:/collection";
    }

    @PostMapping("/createCollection")
    public String createCollection(@RequestParam("collectionName") String collectionName) {
        collectionRepository.save(collectionName, new CollectionDTO(collectionName, new LinkedHashMap<>()));
        return "redirect:/collection";
    }

    @GetMapping("/collection")
    public String collection(Model model) {
        Map<String, CollectionDTO> store = collectionRepository.getCollectionsStore();
        List<String> collectionNameList = new ArrayList<>(store.keySet());
        model.addAttribute("collectionNameList", collectionNameList);
        model.addAttribute("collections", store);

        return "collection";
    }

    @GetMapping("/workspaceNameList")
    @ResponseBody
    public List<String> getWorkspaceNameList(@RequestParam("collectionName") String collectionName) {
        // 주어진 컬렉션 이름에 따른 데이터 목록을 가져옵니다.
        CollectionDTO collectionMap = collectionRepository.getCollectionsStore().get(collectionName);
        return new ArrayList<>(collectionMap.getWorkspaces().keySet()); // JSON 형식으로 반환
    }
}
