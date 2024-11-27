package com.blue.requester.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceDTO {

    @Setter
    public String name;
    @Setter
    public String collectionName;
    public Map<String, ItemDTO> items;
}
