package com.blue.requester.domain.request;

import com.blue.requester.domain.dto.CollectionDTO;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    Map<String, CollectionDTO> collections;
    String url;
    List<String> headerKeys;
    List<String> headerValues;
    String body;
    String httpMethod;
    String contentType;
    String collectionName;
    String workspaceName;
    String itemName;
    List<Integer> selectedHeaderIndexes;
    boolean curlRequest;
}
