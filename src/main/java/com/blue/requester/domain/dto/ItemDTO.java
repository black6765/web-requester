package com.blue.requester.domain.dto;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.util.MultiValueMap;

import java.util.Set;

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
    public MultiValueMap<String, String> headers;
    public String body;
    public Set<String> selectedHeaders;
}
