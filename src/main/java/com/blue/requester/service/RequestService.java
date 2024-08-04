package com.blue.requester.service;

import com.blue.requester.dto.ItemDTO;
import com.blue.requester.repository.CollectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final CollectionRepository collectionRepository;

    public String requestForm(Model model, String collectionName, String workspaceName, String itemName) {
        ItemDTO itemDTO = getItem(collectionName, workspaceName, itemName);

        model.addAttribute("collections", collectionRepository.getCollectionsStore());
        model.addAttribute("url", itemDTO.getUrl());
        model.addAttribute("headers", itemDTO.getHeaders());
        model.addAttribute("body", itemDTO.getBody());
        model.addAttribute("httpMethod", itemDTO.getHttpMethod());

        model.addAttribute("collectionName", collectionName);
        model.addAttribute("workspaceName", workspaceName);
        model.addAttribute("itemName", itemName);

        return "request";
    }

    private ItemDTO getItem(String collectionName, String workspaceName, String itemName) {
        return collectionRepository.getCollectionsStore().get(collectionName).getWorkspaces().get(workspaceName).getItems().get(itemName);
    }

    public String request(Model model, String url, List<String> headersKeys, List<String> headersValues, String body, String httpMethod,
                          String collectionName, String workspaceName, String itemName) {

        Map<String, String> headers = new HashMap<>();

        HttpHeaders httpHeaders = getHttpHeaders(headersKeys, headersValues, headers);
        saveItem(url, body, httpMethod, collectionName, workspaceName, itemName, headers);

        WebClient client = WebClient.builder()
                .baseUrl(url)
                .build();

        String response = sendRequestAndGetResponse(url, body, httpMethod, httpHeaders, client);

        model.addAttribute("collections", collectionRepository.getCollectionsStore());
        model.addAttribute("url", url);
        model.addAttribute("httpMethod", httpMethod);
        model.addAttribute("headers", headers);
        model.addAttribute("body", body);
        model.addAttribute("response", response);

        model.addAttribute("collectionName", collectionName);
        model.addAttribute("workspaceName", workspaceName);
        model.addAttribute("itemName", itemName);

        return "request";
    }

    private String sendRequestAndGetResponse(String url, String body, String httpMethod, HttpHeaders httpHeaders, WebClient client) {
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

        return response;
    }

    private void saveItem(String url, String body, String httpMethod, String collectionName, String workspaceName, String itemName, Map<String, String> headers) {
        ItemDTO itemDTO = getItem(collectionName, workspaceName, itemName);
        itemDTO.setUrl(url);
        itemDTO.setHttpMethod(httpMethod);
        itemDTO.setHeaders(headers);
        itemDTO.setBody(body);
    }

    private HttpHeaders getHttpHeaders(List<String> headersKeys, List<String> headersValues, Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();

        if (headersKeys != null && headersValues != null && !headersKeys.isEmpty() && !headersValues.isEmpty()) {
            for (int i = 0; i < headersKeys.size(); i++) {
                if (!ObjectUtils.isEmpty(headersKeys.get(i)) && !ObjectUtils.isEmpty(headersValues.get(i))) {
                    headers.put(headersKeys.get(i), headersValues.get(i));
                    httpHeaders.add(headersKeys.get(i), headersValues.get(i));
                }
            }
        }

        return httpHeaders;
    }

}
