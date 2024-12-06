package com.blue.requester.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ResultDTO {
    Map<String, CollectionDTO> collections;
    List<String> headerKeys;
    List<String> headerValues;

    String body;
    String response;
    String status;
}
