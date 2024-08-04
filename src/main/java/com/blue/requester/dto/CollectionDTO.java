package com.blue.requester.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CollectionDTO {

    public String name;
    public Map<String, WorkspaceDTO> workspaces;

}
