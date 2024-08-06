package com.blue.requester.controller;

import com.blue.requester.dto.ItemDTO;
import com.blue.requester.service.RequestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping("/request")
    public String request(Model model,
                          @RequestParam("url") String url,
                          @RequestParam(value = "headersKey", required = false) List<String> headersKeys,
                          @RequestParam(value = "headersValue", required = false) List<String> headersValues,
                          @RequestParam("body") String body,
                          @RequestParam("httpMethod") String httpMethod,
                          @RequestParam(value = "collectionName", required = false) String collectionName,
                          @RequestParam(value = "workspaceName", required = false) String workspaceName,
                          @RequestParam(value = "itemName", required = false) String itemName,
                          @RequestParam(value = "envNames", required = false) List<String> envNames,
                          @RequestParam(value = "envName", required = false) String envName) throws JsonProcessingException {

        return requestService.request(model, url, headersKeys, headersValues, body,
                httpMethod, collectionName, workspaceName, itemName, envNames, envName);
    }

    @GetMapping("/request/{collection}/{workspace}/{item}")
    public String requestForm(Model model,
                              @PathVariable("collection") String collection,
                              @PathVariable("workspace") String workspace,
                              @PathVariable("item") String item) {
        return requestService.requestForm(model, collection, workspace, item);
    }

}
