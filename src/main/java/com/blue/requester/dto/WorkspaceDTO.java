package com.blue.requester.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceDTO {

    public String name;
    public String collectionName;
    public Map<String, ItemDTO> items;
}
