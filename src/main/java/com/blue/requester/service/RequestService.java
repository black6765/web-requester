package com.blue.requester.service;

import com.blue.requester.domain.dto.ItemDTO;
import com.blue.requester.domain.dto.RequestFormDTO;
import com.blue.requester.domain.dto.ResultDTO;
import com.blue.requester.domain.request.Request;
import com.blue.requester.exception.EmptyBodyException;
import com.blue.requester.exception.InvalidHttpMethodException;
import com.blue.requester.message.ExceptionMessage;
import com.blue.requester.repository.CollectionRepository;
import com.blue.requester.repository.EnvironmentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestService {

    private final CollectionRepository collectionRepository;
    private final EnvironmentRepository environmentRepository;
    private final ObjectMapper objectMapper;

    public RequestFormDTO requestForm(final String collectionName, final String workspaceName, final String itemName) {
        ItemDTO itemDTO = getItem(collectionName, workspaceName, itemName);
        new RequestFormDTO(itemDTO, collectionRepository.getCollectionsStore());
        return new RequestFormDTO(itemDTO, collectionRepository.getCollectionsStore());
    }

    private ItemDTO getItem(final String collectionName, final String workspaceName, final String itemName) {
        return collectionRepository.getCollectionsStore().get(collectionName).getWorkspaces().get(workspaceName).getItems().get(itemName);
    }

    public String replaceVariables(final String origin, Map<String, String> environmentVariable) {
        StringBuilder result = new StringBuilder(origin);

        for (Map.Entry<String, String> entry : environmentVariable.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            String placeholder = "{{" + key + "}}";
            int index = result.indexOf(placeholder);

            while (index != -1) {
                result.replace(index, index + placeholder.length(), value);
                index = result.indexOf(placeholder, index + value.length());
            }
        }

        return result.toString();
    }

    public ResultDTO request(Request request) throws JsonProcessingException {

        Map<String, String> headers = new TreeMap<>();

        HttpHeaders httpHeaders = getHttpHeadersByHeadersMap(request, headers);
        saveItem(request, headers);

        String replacedUrl = request.getUrl();
        String currentEnvName = environmentRepository.getCurrentEnvName();

        if (currentEnvName != null && !"None".equals(currentEnvName)) {
            replacedUrl = replaceVariables(request.getUrl(), environmentRepository.getCurrentEnvVariables());
        }

        if (!environmentRepository.getGlobalVariables().isEmpty()) {
            replacedUrl = replaceVariables(replacedUrl, environmentRepository.getGlobalVariables());
        }

        String response = sendRequestAndGetResponse(replacedUrl, request, httpHeaders);

        return new ResultDTO(collectionRepository.getCollectionsStore(), headers, convertStringToPrettyJson(request.getBody()), convertStringToPrettyJson(response));
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

    private String convertStringToPrettyJson(final String response) {
        try {
            Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<>() {
            });
            String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseMap);
            log.info("Json parsing success. response:\n" + jsonResponse);
            return jsonResponse;
        } catch (JsonProcessingException e) {
            log.info("Json parsing failed. return response original string:\n" + response);
            return response;
        }
    }

    private String sendRequestAndGetResponse(final String replacedUrl, final Request request, final HttpHeaders httpHeaders) {
        WebClient client = WebClient.builder()
                .build();

        Boolean isValidBodyAndHttpMethod = true;

        String httpMethod = request.getHttpMethod();
        String body = request.getBody();
        String contentType = request.getContentType();

        if ("POST".equals(httpMethod) || "PUT".equals(httpMethod)) {
            if (body == null || body.isEmpty())
                isValidBodyAndHttpMethod = false;
        }

        MediaType selectedContentType = switch (contentType.toLowerCase()) {
            case "json" -> MediaType.APPLICATION_JSON;
            case "text" -> MediaType.TEXT_PLAIN;
            case "form-urlencoded" -> MediaType.APPLICATION_FORM_URLENCODED;
            default -> MediaType.TEXT_PLAIN;
        };

        String responseBody = "{}";

        try {
            if (!isValidBodyAndHttpMethod) {
                throw new EmptyBodyException();
            }

            responseBody = switch (httpMethod) {
                case "GET" -> client.get()
                        .uri(replacedUrl)
                        .headers(h -> h.addAll(httpHeaders))
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                case "DELETE" -> client.delete()
                        .uri(replacedUrl)
                        .headers(h -> h.addAll(httpHeaders))
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                case "POST" -> client.post()
                        .uri(replacedUrl)
                        .headers(h -> h.addAll(httpHeaders))
                        .contentType(selectedContentType)
                        .body(BodyInserters.fromValue(body))
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                case "PUT" -> client.put()
                        .uri(replacedUrl)
                        .headers(h -> h.addAll(httpHeaders))
                        .contentType(selectedContentType)
                        .body(BodyInserters.fromValue(body))
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                default -> throw new InvalidHttpMethodException();
            };
        } catch (InvalidHttpMethodException e) {
            responseBody = ExceptionMessage.HTTP_METHOD_UNAVAILABLE;
        } catch (Exception e) {
            responseBody = e.getMessage();
        }

        return responseBody;
    }

    private void saveItem(final Request request, final Map<String, String> headers) {
        ItemDTO itemDTO = getItem(request.getCollectionName(), request.getWorkspaceName(), request.getItemName());
        itemDTO.setUrl(request.getUrl());
        itemDTO.setHttpMethod(request.getHttpMethod());
        itemDTO.setContentType(request.getContentType());
        itemDTO.setHeaders(headers);
        itemDTO.setBody(request.getBody());
        itemDTO.setSelectedHeaders(request.getSelectedHeaders());
    }

    private HttpHeaders getHttpHeadersByHeadersMap(Request request, final Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        List<String> headersKeys = request.getHeaderKeys();
        List<String> headersValues = request.getHeaderValues();
        Set<String> selectedHeaders = request.getSelectedHeaders();

        if (headersKeys != null && headersValues != null && !headersKeys.isEmpty() && !headersValues.isEmpty()) {
            for (int i = 0; i < headersKeys.size(); i++) {
                if (!ObjectUtils.isEmpty(headersKeys.get(i)) && !ObjectUtils.isEmpty(headersValues.get(i))) {

                    headers.put(headersKeys.get(i), headersValues.get(i));
                    if (selectedHeaders != null && selectedHeaders.contains(headersKeys.get(i))) {
                        // Hidden function: header value "{{#NUM}}" replaced 100000 ~ 999999(6 digits number)
                        httpHeaders.add(headersKeys.get(i), headersValues.get(i).replace("{{#NUM}}", String.valueOf((int) (Math.random() * 899999) + 100000)));
                    }
                }
            }
        }

        return httpHeaders;
    }

}
