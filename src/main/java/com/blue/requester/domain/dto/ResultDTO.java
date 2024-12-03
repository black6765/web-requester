package com.blue.requester.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ResultDTO {
    Map<String, CollectionDTO> collections;
    MultiValueMap<String, String> headers;
    String body;
    String response;
}
