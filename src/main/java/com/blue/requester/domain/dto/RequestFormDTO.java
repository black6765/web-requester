package com.blue.requester.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;


@Getter
@Setter
@AllArgsConstructor
public class RequestFormDTO {
    ItemDTO itemDTO;
    Map<String, CollectionDTO> collections;
}
