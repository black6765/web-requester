package com.blue.requester.service;

import com.blue.requester.dto.ItemDTO;
import com.blue.requester.repository.CollectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final CollectionRepository collectionRepository;

    public String requestForm(Model model, String collectionName, String workspaceName, String itemName) {
        ItemDTO itemDTO = collectionRepository.getCollectionsStore().get(collectionName).getWorkspaces().get(workspaceName).getItems().get(itemName);

        model.addAttribute("collections", collectionRepository.getCollectionsStore());
        model.addAttribute("url", itemDTO.getUrl());
        model.addAttribute("headers", itemDTO.getHeaders());
        model.addAttribute("body", itemDTO.getBody());

        return "request";
    }

    public String request(Model model, String url, String[] headersKeys, String[] headersValues, String body, String httpMethod) {
        Map<String, String> headers = new HashMap<>();

        HttpHeaders httpHeaders = new HttpHeaders();

        // 두 배열을 순회하며 Map에 저장
        for (int i = 0; i < headersKeys.length; i++) {
            if (!headersKeys[i].isEmpty() && !headersValues[i].isEmpty()) {
                headers.put(headersKeys[i], headersValues[i]);
                httpHeaders.add(headersKeys[i], headersValues[i]);
            }
        }

        WebClient client = WebClient.builder()
                .baseUrl(url)
                .build();

        String response = "";

        try {
            response = switch (httpMethod) {
                case "GET" -> client.get()
                        .uri(url)
                        .headers(h -> h.addAll(httpHeaders))
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                case "POST" -> client.post()
                        .uri(url)
                        .headers(h -> h.addAll(httpHeaders))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(body))
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                default -> "Check http method setting.";
            };
        } catch (Exception e) {
            response = "Your request failed.";
        }

        model.addAttribute("collections", collectionRepository.getCollectionsStore());
        model.addAttribute("url", url);
        model.addAttribute("headers", headers);
        model.addAttribute("body", body);
        model.addAttribute("response", response);

        return "request";
    }

}
