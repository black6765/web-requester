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
    @Setter
    public String name;
    @Setter
    public String CollectionName;
    @Setter
    public String workspaceName;
    public String url;
    public String httpMethod;
    public String contentType;
    public Map<String, String> headers;
    public String body;
}
