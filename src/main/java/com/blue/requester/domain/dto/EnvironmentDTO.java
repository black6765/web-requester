package com.blue.requester.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
public class EnvironmentDTO {
    Map<String, CollectionDTO> collections;
    List<String> envNames;
    String exceptEnvName;
    String currentEnvName;
    Map<String, String> variables;
}
