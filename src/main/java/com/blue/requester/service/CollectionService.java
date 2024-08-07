package com.blue.requester.service;

import com.blue.requester.dto.CollectionDTO;
import com.blue.requester.dto.ItemDTO;
import com.blue.requester.dto.WorkspaceDTO;
import com.blue.requester.repository.CollectionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final ObjectMapper objectMapper;

    public String convertCurrentCollectionsToJson() throws JsonProcessingException {
        String collections = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(collectionRepository.getCollectionsStore());
        log.info(collections);
        return collections;
    }

    public Map<String, CollectionDTO> convertJsonToCollectionsMapAndNewStore(final String json) throws IOException {
        Map<String, CollectionDTO> store = objectMapper.readValue(json, new TypeReference<>(){});
        collectionRepository.newStore(store);
        return store;
    }

    public String collectionForm(Model model) {
        Map<String, CollectionDTO> store = collectionRepository.getCollectionsStore();
        List<String> collectionNameList = new ArrayList<>(store.keySet());
        model.addAttribute("collectionNameList", collectionNameList);
        model.addAttribute("collections", store);

        return "createCollectionForm";
    }

    public String createCollection(String collectionName) {
        collectionRepository.save(collectionName, new CollectionDTO(collectionName, new LinkedHashMap<>()));
        return "redirect:/collection/createForm";
    }

    public String createWorkspace(final String collectionName, final String workspaceName) {
        collectionRepository.getCollectionsStore().get(collectionName).getWorkspaces().put(workspaceName, new WorkspaceDTO(workspaceName, collectionName, new LinkedHashMap<>()));
        return "redirect:/collection/createForm";
    }

    public String createItem(final String collectionName, final String workspaceName, final String itemName, final String url, final String httpMethod, final String body, final List<String> headersKeys, List<String> headersValues) {
        Map<String, String> headers = new LinkedHashMap<>();

        if (headersKeys != null && headersValues != null && !headersKeys.isEmpty() && !headersValues.isEmpty()) {
            for (int i = 0; i < headersKeys.size(); i++) {
                if (!ObjectUtils.isEmpty(headersKeys.get(i)) && !ObjectUtils.isEmpty(headersValues.get(i))) { // 키와 값이 비어있지 않다면
                    headers.put(headersKeys.get(i), headersValues.get(i));
                }
            }
        }

        System.out.println(itemName);

        ItemDTO itemDTO = new ItemDTO(itemName, collectionName, workspaceName, url, httpMethod, headers, body);
        collectionRepository.getCollectionsStore().get(collectionName).getWorkspaces().get(workspaceName).getItems().put(itemName, itemDTO);

        return "redirect:/collection/createForm";
    }

    public String deleteCollection(final String collectionName, final String workspaceName, final String itemName) {
        Map<String, CollectionDTO> store = collectionRepository.getCollectionsStore();

        if (ObjectUtils.isEmpty(workspaceName)) {
            store.remove(collectionName);
            log.info("Collection [{}] removed", collectionName);
        } else if (ObjectUtils.isEmpty(itemName)) {
            store.get(collectionName).getWorkspaces().remove(workspaceName);
            log.info("Workspace [{}] <in [{}] Collection> removed", workspaceName, collectionName);
        } else {
            store.get(collectionName).getWorkspaces().get(workspaceName).getItems().remove(itemName);
            log.info("Item [{}] <in [{}] Workspace in [{}] Collection> removed", itemName, workspaceName, collectionName);
        }

        return "deleteCollectionForm";
    }

    public List<String> getCollectionNameList() {
        Map<String, CollectionDTO> collections = collectionRepository.getCollectionsStore();
        return new ArrayList<>(collections.keySet());
    }

    public List<String> getWorkspaceNameList(String collectionName) {
        CollectionDTO collectionMap = collectionRepository.getCollectionsStore().get(collectionName);
        return new ArrayList<>(collectionMap.getWorkspaces().keySet());
    }

    public List<String> getItemNameList(String collectionName, String workspaceName) {
        WorkspaceDTO workspaceMap = collectionRepository.getCollectionsStore().get(collectionName).getWorkspaces().get(workspaceName);
        return new ArrayList<>(workspaceMap.getItems().keySet());
    }
}
