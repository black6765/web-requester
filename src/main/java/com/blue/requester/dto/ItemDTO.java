package com.blue.requester.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {
    public String name;
    public String workspaceName;
    public String url;
    public String httpMethod;
    public Map<String, String> headers;
    public String body;
}
