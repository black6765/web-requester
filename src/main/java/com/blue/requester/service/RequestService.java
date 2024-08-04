package com.blue.requester.service;

import com.blue.requester.dto.ItemDTO;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final CollectionRepository collectionRepository;
    private final ObjectMapper objectMapper;

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
                          String collectionName, String workspaceName, String itemName) throws JsonProcessingException {

        Map<String, String> headers = new HashMap<>();

        HttpHeaders httpHeaders = getHttpHeaders(headersKeys, headersValues, headers);
        saveItem(url, body, httpMethod, collectionName, workspaceName, itemName, headers);

        WebClient client = WebClient.builder()
                .baseUrl(url)
                .build();

        String response = sendRequestAndGetResponse(url, body, httpMethod, httpHeaders, client);
        String responseJson = getStringtoPrettyJson(response);

        model.addAttribute("collections", collectionRepository.getCollectionsStore());
        model.addAttribute("url", url);
        model.addAttribute("httpMethod", httpMethod);
        model.addAttribute("headers", headers);
        model.addAttribute("body", body);
        model.addAttribute("response", responseJson);

        model.addAttribute("collectionName", collectionName);
        model.addAttribute("workspaceName", workspaceName);
        model.addAttribute("itemName", itemName);

        return "request";
    }

    private String getStringtoPrettyJson(String response) throws JsonProcessingException {
        Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<>(){});
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseMap);
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
                default -> "{\"msg\":\"Your request failed.\"}";
            };
        } catch (Exception e) {
            response = "{\n" +
                    "  \"1\" : {\n" +
                    "    \"name\" : \"1\",\n" +
                    "    \"workspaces\" : {\n" +
                    "      \"2\" : {\n" +
                    "        \"name\" : \"2\",\n" +
                    "        \"collectionName\" : \"1\",\n" +
                    "        \"items\" : {\n" +
                    "          \"3\" : {\n" +
                    "            \"name\" : \"3\",\n" +
                    "            \"workspaceName\" : \"POST\",\n" +
                    "            \"url\" : \"2\",\n" +
                    "            \"httpMethod\" : \"GET\",\n" +
                    "            \"headers\" : { },\n" +
                    "            \"body\" : \"133\"\n" +
                    "          }\n" +
                    "        }\n" +
                    "      },\n" +
                    "      \"3\" : {\n" +
                    "        \"name\" : \"3\",\n" +
                    "        \"collectionName\" : \"1\",\n" +
                    "        \"items\" : {\n" +
                    "          \"123\" : {\n" +
                    "            \"name\" : \"123\",\n" +
                    "            \"workspaceName\" : \"3\",\n" +
                    "            \"url\" : \"post\",\n" +
                    "            \"httpMethod\" : \"POST\",\n" +
                    "            \"headers\" : { },\n" +
                    "            \"body\" : \"12323213\"\n" +
                    "          },\n" +
                    "          \"44\" : {\n" +
                    "            \"name\" : \"44\",\n" +
                    "            \"workspaceName\" : \"3\",\n" +
                    "            \"url\" : \"55\",\n" +
                    "            \"httpMethod\" : \"POST\",\n" +
                    "            \"headers\" : { },\n" +
                    "            \"body\" : \"55\"\n" +
                    "          }\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"aa\" : {\n" +
                    "    \"name\" : \"aa\",\n" +
                    "    \"workspaces\" : {\n" +
                    "      \"bb\" : {\n" +
                    "        \"name\" : \"bb\",\n" +
                    "        \"collectionName\" : \"aa\",\n" +
                    "        \"items\" : {\n" +
                    "          \"cc\" : {\n" +
                    "            \"name\" : \"cc\",\n" +
                    "            \"workspaceName\" : \"bb\",\n" +
                    "            \"url\" : \"asf34554646\",\n" +
                    "            \"httpMethod\" : \"POST\",\n" +
                    "            \"headers\" : {\n" +
                    "              \"3\" : \"3\",\n" +
                    "              \"5\" : \"5\"\n" +
                    "            },\n" +
                    "            \"body\" : \"{\\r\\n  \\\"string\\\":\\\"value\\\"\\r\\n  \\\"add\\\": {\\r\\n  \\t\\\"asd\\\":\\\"asd\\\"\\r\\n\\t}\\r\\n}\"\n" +
                    "          }\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"x\" : {\n" +
                    "    \"name\" : \"x\",\n" +
                    "    \"workspaces\" : {\n" +
                    "      \"y\" : {\n" +
                    "        \"name\" : \"y\",\n" +
                    "        \"collectionName\" : \"x\",\n" +
                    "        \"items\" : {\n" +
                    "          \"z\" : {\n" +
                    "            \"name\" : \"z\",\n" +
                    "            \"workspaceName\" : \"y\",\n" +
                    "            \"url\" : \"URL\",\n" +
                    "            \"httpMethod\" : \"POST\",\n" +
                    "            \"headers\" : { },\n" +
                    "            \"body\" : \"BODY\"\n" +
                    "          }\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"123\" : {\n" +
                    "    \"name\" : \"123\",\n" +
                    "    \"workspaces\" : { }\n" +
                    "  },\n" +
                    "  \"324\" : {\n" +
                    "    \"name\" : \"324\",\n" +
                    "    \"workspaces\" : {\n" +
                    "      \"679679\" : {\n" +
                    "        \"name\" : 679679,\n" +
                    "        \"collectionName\" : true,\n" +
                    "        \"items\" : { }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"56765\" : {\n" +
                    "    \"name\" : \"56765\",\n" +
                    "    \"workspaces\" : { }\n" +
                    "  },\n" +
                    "  \"457457547\" : {\n" +
                    "    \"name\" : \"457457547\",\n" +
                    "    \"workspaces\" : {\n" +
                    "      \"346\" : {\n" +
                    "        \"name\" : \"346\",\n" +
                    "        \"collectionName\" : \"457457547\",\n" +
                    "        \"items\" : {\n" +
                    "          \"547547547\" : {\n" +
                    "            \"name\" : \"547547547\",\n" +
                    "            \"workspaceName\" : \"346\",\n" +
                    "            \"url\" : \"696\",\n" +
                    "            \"httpMethod\" : \"POST\",\n" +
                    "            \"headers\" : {\n" +
                    "              \"56756\" : \"55\",\n" +
                    "              \"7765\" : \"6565\"\n" +
                    "            },\n" +
                    "            \"body\" : \"679436436\"\n" +
                    "          }\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
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
