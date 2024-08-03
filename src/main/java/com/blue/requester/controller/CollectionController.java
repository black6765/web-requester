package com.blue.requester.controller;

import com.blue.requester.dto.ItemDTO;
import com.blue.requester.repository.CollectionRepository;
import com.blue.requester.service.CollectionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
            @RequestParam("body") String body,
            @RequestParam("headersKey") String[] headersKeys,
            @RequestParam("headersValue") String[] headersValues
    ) {
        Map<String, String> headers = new LinkedHashMap<>();

        // 두 배열을 순회하며 Map에 저장
        for (int i = 0; i < headersKeys.length; i++) {
            if (!headersKeys[i].isEmpty() && !headersValues[i].isEmpty()) { // 키와 값이 비어있지 않다면
                headers.put(headersKeys[i], headersValues[i]);
            }
        }

        System.out.println(body);

        ItemDTO itemDTO = new ItemDTO(itemName, url, headers, body);

        collectionRepository.getStore().get(collectionName).get(workspaceName).put(itemName, itemDTO);

        return "redirect:/collection";
    }

    @PostMapping("/createWorkspace")
    public String createWorkspace(@RequestParam("collectionName") String collectionName, @RequestParam("workspaceName") String workspaceName) {
        collectionRepository.getStore().get(collectionName).put(workspaceName, new LinkedHashMap<>());
        return "redirect:/collection";
    }

    @PostMapping("/createCollection")
    public String createCollection(@RequestParam("collectionName") String collectionName) {
        collectionRepository.save(collectionName, new LinkedHashMap<>());

        return "redirect:/collection";
    }

    @GetMapping("/collection")
    public String collection(Model model) throws JsonProcessingException {


        Map<String, Map<String, Map<String, ItemDTO>>> store = collectionRepository.getStore();
        List<String> collectionNameList = new ArrayList<>(store.keySet());
        model.addAttribute("collectionNameList", collectionNameList);

        model.addAttribute("collections", collectionRepository.getStore());

        return "collection";
    }

    @GetMapping("/workspaceNameList")
    @ResponseBody
    public List<String> getWorkspaceNameList(@RequestParam("collectionName") String collectionName) {
        // 주어진 컬렉션 이름에 따른 데이터 목록을 가져옵니다.
        Map<String, Map<String, ItemDTO>> collectionMap = collectionRepository.getStore().get(collectionName);
        return new ArrayList<>(collectionMap.keySet()); // JSON 형식으로 반환
    }
}
