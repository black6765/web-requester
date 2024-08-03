package com.blue.requester.controller;

import com.blue.requester.dto.ItemDTO;
import com.blue.requester.repository.CollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class DataName {
    @Autowired
    CollectionRepository collectionRepository;

    @GetMapping("/getDataNames")
    public List<String> getDataNames(@RequestParam("collectionName") String collectionName) {
        System.out.println("DataName.getDataNames");
        // 주어진 컬렉션 이름에 따른 데이터 목록을 가져옵니다.
        Map<String, Map<String, ItemDTO>> collectionMap = collectionRepository.getStore().get(collectionName);

        List<String> dataNames = new ArrayList<>(collectionMap.keySet());
        System.out.println(collectionName);
        System.out.println(dataNames);

        return dataNames; // JSON 형식으로 반환
    }
}
