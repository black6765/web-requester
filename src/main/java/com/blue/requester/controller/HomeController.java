package com.blue.requester.controller;

import com.blue.requester.repository.CollectionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class HomeController {

    private final CollectionRepository collectionRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("collections", collectionRepository.getCollectionsStore());
        return "home";
    }
}
