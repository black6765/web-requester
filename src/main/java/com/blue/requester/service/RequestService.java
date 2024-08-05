package com.blue.requester.service;

import com.blue.requester.dto.ItemDTO;
import com.blue.requester.exception.InvalidHttpMethodException;
import com.blue.requester.message.ExceptionMessage;
import com.blue.requester.repository.CollectionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final CollectionRepository collectionRepository;
    private final ObjectMapper objectMapper;

    public String requestForm(Model model, final String collectionName, final String workspaceName, final String itemName) {
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

    private ItemDTO getItem(final String collectionName, final String workspaceName, final String itemName) {
        return collectionRepository.getCollectionsStore().get(collectionName).getWorkspaces().get(workspaceName).getItems().get(itemName);
    }

    public String request(Model model, final String url, final List<String> headersKeys, final List<String> headersValues, final String body, final String httpMethod,
                          final String collectionName, final String workspaceName, final String itemName) throws JsonProcessingException {

        Map<String, String> headers = new HashMap<>();

        HttpHeaders httpHeaders = getHttpHeaders(headersKeys, headersValues, headers);
        saveItem(url, body, httpMethod, collectionName, workspaceName, itemName, headers);

        WebClient client = WebClient.builder()
                .baseUrl(url)
                .build();

        String response = sendRequestAndGetResponse(url, body, httpMethod, httpHeaders, client);

        if (isJsonType(response)) {
            response = getStringtoPrettyJson(response);
        }

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

    private boolean isJsonType(String response) {
        boolean isJsonType;
        try {
            objectMapper.readTree(response);
            isJsonType = true;
        } catch (IOException e) {
            isJsonType = false;
        }
        return isJsonType;
    }

    private String getStringtoPrettyJson(final String response) throws JsonProcessingException {
        Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<>(){});
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseMap);
    }

    private String sendRequestAndGetResponse(final String url, final String body, final String httpMethod, final HttpHeaders httpHeaders, final WebClient client) {
        String responseBody = "{}";

        try {
            responseBody = switch (httpMethod) {
                case "GET" -> client.get()
                        .uri(url)
                        .headers(h -> h.addAll(httpHeaders))
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                case "DELETE" -> client.delete()
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
                case "PUT" -> client.put()
                        .uri(url)
                        .headers(h -> h.addAll(httpHeaders))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(body))
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                default -> throw new InvalidHttpMethodException();
            };
        } catch (InvalidHttpMethodException e) {
            responseBody = ExceptionMessage.HTTP_METHOD_UNAVAILABLE;
        } catch (Exception e) {
            responseBody = ExceptionMessage.REQUEST_FAILED_WTIH_EXCEPTION;
        }

        return responseBody;
    }

    private void saveItem(final String url, final String body, final String httpMethod,
                          final String collectionName, final String workspaceName, final String itemName, final Map<String, String> headers) {
        ItemDTO itemDTO = getItem(collectionName, workspaceName, itemName);
        itemDTO.setUrl(url);
        itemDTO.setHttpMethod(httpMethod);
        itemDTO.setHeaders(headers);
        itemDTO.setBody(body);
    }

    private HttpHeaders getHttpHeaders(final List<String> headersKeys, final List<String> headersValues, final Map<String, String> headers) {
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
