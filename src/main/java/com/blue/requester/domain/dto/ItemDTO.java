package com.blue.requester.domain.dto;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

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
        this.headerKeys = itemDTO.getHeaderKeys();
        this.headerValues = itemDTO.getHeaderValues();
        this.body = itemDTO.getBody();
        this.selectedHeaderIndexes = itemDTO.getSelectedHeaderIndexes();
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
    public List<String> headerKeys;
    public List<String> headerValues;
    public String body;
    public List<Integer> selectedHeaderIndexes;
}
