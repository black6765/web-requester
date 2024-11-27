package com.blue.requester.domain.request;

import com.blue.requester.domain.dto.CollectionDTO;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    Map<String, CollectionDTO> collections;
    String url;
    List<String> headersKeys;
    List<String> headersValues;
    String body;
    String httpMethod;
    String contentType;
    String collectionName;
    String workspaceName;
    String itemName;
    Set<String> selectedHeaders;
}
