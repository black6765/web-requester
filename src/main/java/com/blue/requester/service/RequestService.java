package com.blue.requester.service;

import com.blue.requester.dto.ItemDTO;
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
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestService {

    private final CollectionRepository collectionRepository;
    private final EnvironmentRepository environmentRepository;
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

    public String replaceVariables(final String origin, Map<String, String> environmentVariable) {
        // 원본 문자열을 StringBuilder로 변환
        StringBuilder result = new StringBuilder(origin);

        // 환경 변수를 순회
        for (Map.Entry<String, String> entry : environmentVariable.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // ${key} 형태의 문자열을 ${value}로 변경
            String placeholder = "${" + key + "}";
            int index = result.indexOf(placeholder);

            while (index != -1) {
                // 문자열 교체
                result.replace(index, index + placeholder.length(), value);
                // 다음 인덱스 찾기
                index = result.indexOf(placeholder, index + value.length());
            }
        }

        // 최종 결과 반환
        return result.toString();
    }

    public String request(Model model, final String url, final List<String> headersKeys, final List<String> headersValues, final String body, final String httpMethod,
                          final String collectionName, final String workspaceName, final String itemName) throws JsonProcessingException {

        Map<String, String> headers = new LinkedHashMap<>();

        HttpHeaders httpHeaders = getHttpHeaders(headersKeys, headersValues, headers);
        saveItem(url, body, httpMethod, collectionName, workspaceName, itemName, headers);

        String replacedUrl = url;

        String currentEnvName = environmentRepository.getCurrentEnvName();

        if (currentEnvName != null && !"None".equals(currentEnvName)) {
            replacedUrl = replaceVariables(url, environmentRepository.getCurrentEnvVariables());
        }

        if (!environmentRepository.getGlobalVariables().isEmpty()) {
            replacedUrl = replaceVariables(url, environmentRepository.getGlobalVariables());
        }
        
        // Todo : header value에서 ${randomNumber}에 대해 랜덤 난수 발생시키는 기능 추가

        String response = sendRequestAndGetResponse(replacedUrl, body, httpMethod, httpHeaders);

        Map<String, String> replacedHeaders = new LinkedHashMap<>();


        response = getStringtoPrettyJson(response);


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

    private String getStringtoPrettyJson(final String response) {
        try {
            Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<>() {});
            String jsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseMap);
            log.info("Json parsing success. response:\n" + jsonResponse);
            return jsonResponse;
        } catch (JsonProcessingException e) {
            log.info("Json parsing failed. return response original string:\n" + response);
            return response;
        }
    }

    private String sendRequestAndGetResponse(final String replacedUrl, final String body, final String httpMethod, final HttpHeaders httpHeaders) {
        WebClient client = WebClient.builder()
                .build();

        String responseBody = "{}";

        try {
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(body))
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                case "PUT" -> client.put()
                        .uri(replacedUrl)
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
            responseBody = e.getMessage();
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
                    // Hidden function: header value "${#RANDOM_NUMBER}" replaced 100000 ~ 999999(6 digits number)
                    httpHeaders.add(headersKeys.get(i),
                            headersValues.get(i).replace("${#RANDOM_NUMBER}", String.valueOf((int) (Math.random() * 899999) + 100000)));
                }
            }
        }

        return httpHeaders;
    }

}
