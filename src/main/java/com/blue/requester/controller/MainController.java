package com.blue.requester.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class MainController {

    @GetMapping("/test")
    public String test(Model model) {

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

        return "test";
    }

    @GetMapping("/")
    public String mainPage(Model model) {

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

//        List<String> DATA1 = List.of("item 1", "item 2");
//        List<String> DATA2 = List.of("item 3", "item 4");
//
//        Map<String, Object> datas1 = new TreeMap<>();
//        datas1.put("DATA1", DATA1);
//        datas1.put("DATA2", DATA2);
//
//        List<String> DATA3 = List.of("item 5", "item 6");
//        List<String> DATA4 = List.of("item 7", "item 8");
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
        return "index";
    }

}
