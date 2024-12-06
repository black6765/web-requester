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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @GetMapping("/request/{collectionName}/{workspaceName}/{itemName}")
    public String requestForm(Model model,
                              @PathVariable("collectionName") String collectionName,
                              @PathVariable("workspaceName") String workspaceName,
                              @PathVariable("itemName") String itemName) {
        RequestFormDTO requestForm = requestService.requestForm(collectionName, workspaceName, itemName);
        ItemDTO itemDTO = requestForm.getItemDTO();

        model.addAttribute("collections", requestForm.getCollections());
        model.addAttribute("itemDTO", itemDTO);

        return "requestForm";
    }

    @PostMapping("/request")
    public String request(RedirectAttributes redirectAttributes, @ModelAttribute Request request) throws JsonProcessingException {
        ResultDTO result = requestService.request(request);

        redirectAttributes.addFlashAttribute("response", result.getResponse());
        redirectAttributes.addFlashAttribute("status", result.getStatus());

        redirectAttributes.addAttribute("collectionName", request.getCollectionName());
        redirectAttributes.addAttribute("workspaceName", request.getWorkspaceName());
        redirectAttributes.addAttribute("itemName", request.getItemName());

        return "redirect:/request/{collectionName}/{workspaceName}/{itemName}";
    }

}
