package com.blue.requester.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {
    String name;
    String url;
    Map<String, String> headers;
    String body;
}
