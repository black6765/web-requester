package com.blue.requester.controller;

import com.blue.requester.domain.dto.ItemDTO;
import com.blue.requester.domain.dto.RequestFormDTO;
import com.blue.requester.domain.dto.ResultDTO;
import com.blue.requester.domain.request.Request;
import com.blue.requester.service.RequestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping("/request")
    public String Response(Model model, @ModelAttribute Request request) throws JsonProcessingException {
        ResultDTO result = requestService.request(request);

        model.addAttribute("collections", result.getCollections());
        model.addAttribute("url", request.getUrl());
        model.addAttribute("httpMethod", request.getHttpMethod());
        model.addAttribute("headers", result.getHeaders());
        model.addAttribute("contentType", request.getContentType());
        model.addAttribute("body", result.getBody());
        model.addAttribute("selectedHeaders", request.getSelectedHeaders());
        model.addAttribute("response", result.getResponse());

        model.addAttribute("collectionName", request.getCollectionName());
        model.addAttribute("workspaceName", request.getWorkspaceName());
        model.addAttribute("itemName", request.getItemName());

        return "request";
    }

    @GetMapping("/request/{collection}/{workspace}/{item}")
    public String requestForm(Model model,
                              @PathVariable("collection") String collection,
                              @PathVariable("workspace") String workspace,
                              @PathVariable("item") String item) {
        RequestFormDTO requestForm = requestService.requestForm(collection, workspace, item);
        ItemDTO itemDTO = requestForm.getItemDTO();

        model.addAttribute("collections", requestForm.getCollections());
        model.addAttribute("url", itemDTO.getUrl());
        model.addAttribute("headers", itemDTO.getHeaders());
        model.addAttribute("body", itemDTO.getBody());
        model.addAttribute("httpMethod", itemDTO.getHttpMethod());
        model.addAttribute("contentType", itemDTO.getContentType());
        model.addAttribute("selectedHeaders", itemDTO.selectedHeaders);

        model.addAttribute("collectionName", itemDTO.getCollectionName());
        model.addAttribute("workspaceName", itemDTO.workspaceName);
        model.addAttribute("itemName", itemDTO.name);

        return "request";
    }

}
