package com.blue.requester.controller;

import com.blue.requester.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping("/request")
    public String request(Model model,
                          @RequestParam("url") String url,
                          @RequestParam("headersKey") String[] headersKeys,
                          @RequestParam("headersValue") String[] headersValues,
                          @RequestParam("body") String body,
                          @RequestParam("httpMethod") String httpMethod) {

        return requestService.request(model, url, headersKeys, headersValues, body, httpMethod);
    }

    @GetMapping("/request/{collection}/{workspace}/{item}")
    public String requestForm(Model model,
                              @PathVariable("collection") String collection,
                              @PathVariable("workspace") String workspace,
                              @PathVariable("item") String item) {
        return requestService.requestForm(model, collection, workspace, item);
    }

}
