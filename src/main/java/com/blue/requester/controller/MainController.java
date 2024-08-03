package com.blue.requester.controller;

import com.blue.requester.dto.ItemDTO;
import com.blue.requester.repository.CollectionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    CollectionRepository collectionRepository;


    @PostMapping("/createItem")
    public String createItem(
            @RequestParam("collectionName") String collectionName,
            @RequestParam("dataName") String dataName,
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

        collectionRepository.getStore().get(collectionName).get(dataName).put(itemName, itemDTO);

        return "redirect:/collection";
    }

    @PostMapping("/createData")
    public String createData(@RequestParam("collectionName") String collectionName, @RequestParam("dataName") String dataName) {
        collectionRepository.getStore().get(collectionName).put(dataName, new LinkedHashMap<>());

        System.out.println("collection Name = " + collectionName);
        for(String name : collectionRepository.getStore().get(collectionName).keySet()) {
            System.out.println(name);
        }
        return "redirect:/collection";
    }

//    @GetMapping("/getDataNames")
//    public List<String> getDataNames(@RequestParam("collectionName") String collectionName) {
//        // 주어진 컬렉션 이름에 따른 데이터 목록을 가져옵니다.
//        Map<String, Map<String, ItemDTO>> collectionMap = collectionRepository.getStore().get(collectionName);
//
//        List<String> dataNames = new ArrayList<>(collectionMap.keySet());
//
//        return dataNames; // JSON 형식으로 반환
//    }

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

//        Map<String, String> DATA1 = Map.of("item 1", Base64.getEncoder().encodeToString("item 1".getBytes()), "item 2", Base64.getEncoder().encodeToString("item 2".getBytes()));
//        Map<String, String> DATA2 = Map.of("item 3", Base64.getEncoder().encodeToString("item 3".getBytes()), "item 4", Base64.getEncoder().encodeToString("item 4".getBytes()));
//
//        Map<String, Object> datas1 = new TreeMap<>();
//        datas1.put("DATA1", DATA1);
//        datas1.put("DATA2", DATA2);
//
//        Map<String, String> DATA3 = Map.of("item 1", Base64.getEncoder().encodeToString("item 1".getBytes()), "item 2", Base64.getEncoder().encodeToString("item 2".getBytes()));
//        Map<String, String> DATA4 = Map.of("item 3", Base64.getEncoder().encodeToString("item 3".getBytes()), "item 4", Base64.getEncoder().encodeToString("item 4".getBytes()));
//
//
//        Map<String, Object> datas2 = new TreeMap<>();
//        datas2.put("DATA3", DATA3);
//        datas2.put("DATA4", DATA4);
//
//        Map<String, Map<String, Object>> collections = new HashMap<>();
//        collections.put("COLLECTION1", datas1);
//        collections.put("COLLECTION2", datas2);
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

    @GetMapping("/request/{collection}/{data}/{item}")
    public String getMain(Model model, @PathVariable String collection, @PathVariable String data, @PathVariable String item) throws JsonProcessingException {

//        Map<String, String> DATA1 = Map.of("item 1", Base64.getEncoder().encodeToString("item 1".getBytes()), "item 2", Base64.getEncoder().encodeToString("item 2".getBytes()));
//        Map<String, String> DATA2 = Map.of("item 3", Base64.getEncoder().encodeToString("item 3".getBytes()), "item 4", Base64.getEncoder().encodeToString("item 4".getBytes()));
//
//        Map<String, Object> datas1 = new TreeMap<>();
//        datas1.put("DATA1", DATA1);
//        datas1.put("DATA2", DATA2);
//
//        Map<String, String> DATA3 = Map.of("item 1", Base64.getEncoder().encodeToString("item 1".getBytes()), "item 2", Base64.getEncoder().encodeToString("item 2".getBytes()));
//        Map<String, String> DATA4 = Map.of("item 3", Base64.getEncoder().encodeToString("item 3".getBytes()), "item 4", Base64.getEncoder().encodeToString("item 4".getBytes()));
//
//
//        Map<String, Object> datas2 = new TreeMap<>();
//        datas2.put("DATA3", DATA3);
//        datas2.put("DATA4", DATA4);
//
//        Map<String, Map<String, Object>> collections = new HashMap<>();
//        collections.put("COLLECTION1", datas1);
//        collections.put("COLLECTION2", datas2);
//
//        model.addAttribute("collections", collections);

//        String jsonData = "{\n" +
//                "    \"collection1\": {\n" +
//                "        \"data1\": {\n" +
//                "            \"item1\" : {\n" +
//                "                \"url\":\"http://localhost:8080/basic.html\",\n" +
//                "                \"headers\" : {\n" +
//                "                    \"header1\":\"value1\",\n" +
//                "                    \"header2\":\"value2\"\n" +
//                "                },\n" +
//                "                \"body\" : \"body\"\n" +
//                "            }\n" +
//                "        },\n" +
//                "        \"data2\": {\n" +
//                "            \"item1\" : {\n" +
//                "                \"url\":\"http://naver.com\",\n" +
//                "                \"headers\" : {\n" +
//                "                    \"header1\":\"value1\",\n" +
//                "                    \"header2\":\"value2\"\n" +
//                "                },\n" +
//                "                \"body\" : \"body\"\n" +
//                "            }\n" +
//                "        }\n" +
//                "    }\n" +
//                "}";


            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Map<String, Map<String, ItemDTO>>> resultMap = collectionRepository.getStore();

            // 결과 출력
            System.out.println(resultMap);
            model.addAttribute("collections", resultMap);


            Map<String, Map<String, ItemDTO>> collectionMap = resultMap.get(collection);
            Map<String, ItemDTO> dataMap = collectionMap.get(data);
            ItemDTO itemDTO = dataMap.get(item);


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
