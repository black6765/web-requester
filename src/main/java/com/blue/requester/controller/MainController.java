package com.blue.requester.controller;

import com.blue.requester.dto.ItemDTO;
import com.blue.requester.repository.CollectionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Controller
public class MainController {

    ObjectMapper objectMapper;

    CollectionRepository collectionRepository;

    public MainController(ObjectMapper objectMapper, CollectionRepository collectionRepository) {
        this.objectMapper = objectMapper;
        this.collectionRepository = collectionRepository;
    }

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
    public String collection(Model model) {

        Map<String, Map<String, Map<String, ItemDTO>>> store = collectionRepository.getStore();

        List<String> collectionNameList = new ArrayList<>(store.keySet());

        model.addAttribute("collectionNameList", collectionNameList);

        return "collection";
    }

    @PostMapping("/request")
    public String postMain(
            @RequestParam("url") String url,
            @RequestParam("headersKey") String[] headersKeys,
            @RequestParam("headersValue") String[] headersValues,
            @RequestParam("body") String body,
            Model model) throws JsonProcessingException {

        Map<String, String> headers = new HashMap<>();

        // 두 배열을 순회하며 Map에 저장
        for (int i = 0; i < headersKeys.length; i++) {
            if (!headersKeys[i].isEmpty() && !headersValues[i].isEmpty()) { // 키와 값이 비어있지 않다면
                headers.put(headersKeys[i], headersValues[i]);
            }
        }

        Map<String, Map<String, Map<String, ItemDTO>>> collections = (Map<String, Map<String, Map<String, ItemDTO>>>) model.getAttribute("collections");
        model.addAttribute("collections", collections);
        model.addAttribute("result", "result");

        model.addAttribute("url", url);
        model.addAttribute("headers", headers);
        model.addAttribute("body", body);
        System.out.println(body);


        WebClient client = WebClient.builder()
                .baseUrl(url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();


        HttpHeaders httpHeaders = new HttpHeaders();

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpHeaders.add(entry.getKey(), entry.getValue());
        }

        System.out.println("url = " + url);

        String result = client.get()
                .uri(url)
                .headers(h -> h.addAll(httpHeaders))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println(result);

        return "request";
    }

    @GetMapping("/request/{collection}/{workspace}/{item}")
    public String getMain(Model model, @PathVariable String collection, @PathVariable String workspace, @PathVariable String item) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Map<String, Map<String, ItemDTO>>> resultMap = collectionRepository.getStore();

        // 결과 출력
        System.out.println(resultMap);
        model.addAttribute("collections", resultMap);


        Map<String, Map<String, ItemDTO>> collectionMap = resultMap.get(collection);
        Map<String, ItemDTO> workspaceMap = collectionMap.get(workspace);
        ItemDTO itemDTO = workspaceMap.get(item);


        String url = itemDTO.getUrl();
        Map<String, String> headers = itemDTO.getHeaders();
        String body = itemDTO.getBody();
        System.out.println("body = " + body);

        model.addAttribute("result", "result");
        model.addAttribute("url", url);
        model.addAttribute("headers", headers);
        model.addAttribute("body", body);

        WebClient client = WebClient.builder()
                .baseUrl(url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();


        HttpHeaders httpHeaders = new HttpHeaders();

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpHeaders.add(entry.getKey(), entry.getValue());
        }

        System.out.println("url = " + url);

//        String result = client.get()
//                .uri(url)
//                .headers(h -> h.addAll(httpHeaders))
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();


//        String result = client.post()
//                .uri(url)
//                .headers(h -> h.addAll(httpHeaders))
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue(body))
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();

//        System.out.println(result);

        return "request";
    }

}
