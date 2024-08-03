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
public class WorkspaceName {
    @Autowired
    CollectionRepository collectionRepository;

    @GetMapping("/getWorkspaceNameList")
    public List<String> getWorkspaceNameList(@RequestParam("collectionName") String collectionName) {
        // 주어진 컬렉션 이름에 따른 데이터 목록을 가져옵니다.
        Map<String, Map<String, ItemDTO>> collectionMap = collectionRepository.getStore().get(collectionName);

        return new ArrayList<>(collectionMap.keySet()); // JSON 형식으로 반환
    }
}
