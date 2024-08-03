package com.blue.requester.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class MainController {

    @PostMapping("/request")
    public String postMain(
            @RequestParam("url") String url,
            @RequestParam("headersKey") String[] headersKeys,
            @RequestParam("headersValue") String[] headersValues,
            @RequestParam("body") String body,
            Model model) {

        Map<String, String> headers = new HashMap<>();

        // 두 배열을 순회하며 Map에 저장
        for (int i = 0; i < headersKeys.length; i++) {
            if (!headersKeys[i].isEmpty() && !headersValues[i].isEmpty()) { // 키와 값이 비어있지 않다면
                headers.put(headersKeys[i], headersValues[i]);
            }
        }

        Map<String, String> DATA1 = Map.of("item 1", Base64.getEncoder().encodeToString("item 1".getBytes()), "item 2", Base64.getEncoder().encodeToString("item 2".getBytes()));
        Map<String, String> DATA2 = Map.of("item 3", Base64.getEncoder().encodeToString("item 3".getBytes()), "item 4", Base64.getEncoder().encodeToString("item 4".getBytes()));

        Map<String, Object> datas1 = new TreeMap<>();
        datas1.put("DATA1", DATA1);
        datas1.put("DATA2", DATA2);

        Map<String, String> DATA3 = Map.of("item 1", Base64.getEncoder().encodeToString("item 1".getBytes()), "item 2", Base64.getEncoder().encodeToString("item 2".getBytes()));
        Map<String, String> DATA4 = Map.of("item 3", Base64.getEncoder().encodeToString("item 3".getBytes()), "item 4", Base64.getEncoder().encodeToString("item 4".getBytes()));


        Map<String, Object> datas2 = new TreeMap<>();
        datas2.put("DATA3", DATA3);
        datas2.put("DATA4", DATA4);

        Map<String, Map<String, Object>> collections = new HashMap<>();
        collections.put("COLLECTION1", datas1);
        collections.put("COLLECTION2", datas2);

        model.addAttribute("collections", collections);
        model.addAttribute("body", body);
        model.addAttribute("result", "result");
        System.out.println(body);

        return "request";
    }

    @GetMapping("/request")
    public String getMain(Model model) {

        Map<String, String> DATA1 = Map.of("item 1", Base64.getEncoder().encodeToString("item 1".getBytes()), "item 2", Base64.getEncoder().encodeToString("item 2".getBytes()));
        Map<String, String> DATA2 = Map.of("item 3", Base64.getEncoder().encodeToString("item 3".getBytes()), "item 4", Base64.getEncoder().encodeToString("item 4".getBytes()));

        Map<String, Object> datas1 = new TreeMap<>();
        datas1.put("DATA1", DATA1);
        datas1.put("DATA2", DATA2);

        Map<String, String> DATA3 = Map.of("item 1", Base64.getEncoder().encodeToString("item 1".getBytes()), "item 2", Base64.getEncoder().encodeToString("item 2".getBytes()));
        Map<String, String> DATA4 = Map.of("item 3", Base64.getEncoder().encodeToString("item 3".getBytes()), "item 4", Base64.getEncoder().encodeToString("item 4".getBytes()));


        Map<String, Object> datas2 = new TreeMap<>();
        datas2.put("DATA3", DATA3);
        datas2.put("DATA4", DATA4);

        Map<String, Map<String, Object>> collections = new HashMap<>();
        collections.put("COLLECTION1", datas1);
        collections.put("COLLECTION2", datas2);

        model.addAttribute("collections", collections);

        return "request";
    }

}
