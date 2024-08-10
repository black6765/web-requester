package com.blue.requester.dto;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class ItemDTO {

    public ItemDTO(ItemDTO itemDTO) {
        this.name = itemDTO.getName();
        this.collectionName = itemDTO.getCollectionName();
        this.workspaceName = itemDTO.getWorkspaceName();
        this.url = itemDTO.getUrl();
        this.httpMethod = itemDTO.getHttpMethod();
        this.contentType = itemDTO.getContentType();
        this.headers = itemDTO.getHeaders();
        this.body = itemDTO.getBody();
        this.selectedHeaders = itemDTO.getSelectedHeaders();
    }

    @Setter
    public String name;
    @Setter
    public String collectionName;
    @Setter
    public String workspaceName;
    public String url;
    public String httpMethod;
    public String contentType;
    public Map<String, String> headers;
    public String body;
    public List<String> selectedHeaders;
}
