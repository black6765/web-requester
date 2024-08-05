package com.blue.requester.exception.handler;

import com.blue.requester.repository.CollectionRepository;
import lombok.AllArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@AllArgsConstructor
public class GlobalControllerAdviser {

    CollectionRepository collectionRepository;

    @ExceptionHandler(value = Exception.class)
    public String exception(Model model, Exception e){
        model.addAttribute("collections", collectionRepository.getCollectionsStore());
        model.addAttribute("message", e.getMessage());
        return "error";
    }
}
