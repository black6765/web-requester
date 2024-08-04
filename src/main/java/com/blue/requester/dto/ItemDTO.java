package com.blue.requester.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {
    public String name;
    public String workspaceName;
    public String url;
    public Map<String, String> headers;
    public String body;
}
