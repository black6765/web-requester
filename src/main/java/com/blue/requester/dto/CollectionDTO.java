package com.blue.requester.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CollectionDTO {

    @Setter
    public String name;
    public Map<String, WorkspaceDTO> workspaces;

}
