package com.blue.requester.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ResultDTO {
    Map<String, CollectionDTO> collections;
    Map<String, String> headers;
    String body;
    String response;
}
