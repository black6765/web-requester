package com.blue.requester.service;

import com.blue.requester.domain.dto.CollectionDTO;
import com.blue.requester.domain.dto.ItemDTO;
import com.blue.requester.domain.dto.WorkspaceDTO;
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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
        TypeReference<TreeMap<String, CollectionDTO>> typeReference = new TypeReference<>() {
        };
        Map<String, CollectionDTO> store = objectMapper.readValue(json, typeReference);
        store.putAll(store);
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
        collectionRepository.save(collectionName, new CollectionDTO(collectionName, new TreeMap<>()));
        return "redirect:/collection/createForm";
    }

    public String createWorkspace(final String collectionName, final String workspaceName) {
        collectionRepository.getCollectionsStore().get(collectionName).getWorkspaces().put(workspaceName, new WorkspaceDTO(workspaceName, collectionName, new TreeMap<>()));
        return "redirect:/collection/createForm";
    }

    public void createItem(final String collectionName, final String workspaceName, final String itemName,
                           final String url, final String httpMethod, final String body,
                           final List<String> headersKeys, List<String> headersValues, final String contentType, final List<Integer> selectedHeaderIndexes) {

        ItemDTO itemDTO = new ItemDTO(itemName, collectionName, workspaceName, url, httpMethod, contentType, headersKeys, headersValues, body, selectedHeaderIndexes);
        collectionRepository.getCollectionsStore().get(collectionName).getWorkspaces().get(workspaceName).getItems().put(itemName, itemDTO);
    }

    public void renameCollection(final String collectionName, final String workspaceName, final String itemName, final String targetName) {
        Map<String, CollectionDTO> store = collectionRepository.getCollectionsStore();

        if (ObjectUtils.isEmpty(workspaceName)) {
            CollectionDTO collection = store.get(collectionName);

            Map<String, WorkspaceDTO> workspaces = store.get(collectionName).getWorkspaces();


            for (WorkspaceDTO workspace : workspaces.values()) {

                Map<String, ItemDTO> items = workspace.getItems();

                for (ItemDTO item : items.values()) {
                    item.setCollectionName(targetName);
                }

                workspace.setCollectionName(targetName);
            }

            collection.setName(targetName);
            store.put(targetName, collection);
            store.remove(collectionName);
            log.info("Collection [{}] renamed to [{}]", collectionName, targetName);
        } else if (ObjectUtils.isEmpty(itemName)) {
            WorkspaceDTO workspace = store.get(collectionName).getWorkspaces().get(workspaceName);

            Map<String, ItemDTO> items = workspace.getItems();

            for (ItemDTO item : items.values()) {
                item.setWorkspaceName(targetName);
            }

            workspace.setName(targetName);
            store.get(collectionName).getWorkspaces().put(targetName, workspace);
            store.get(collectionName).getWorkspaces().remove(workspaceName);
            log.info("Workspace [{}] <in [{}] Collection> renamed to [{}]", workspaceName, collectionName, targetName);
        } else {
            ItemDTO item = store.get(collectionName).getWorkspaces().get(workspaceName).getItems().get(itemName);
            store.get(collectionName).getWorkspaces().get(workspaceName).getItems().put(targetName, item);
            store.get(collectionName).getWorkspaces().get(workspaceName).getItems().remove(itemName);
            item.setName(targetName);
            log.info("Item [{}] <in [{}] Workspace in [{}] Collection> renamed to [{}]", itemName, workspaceName, collectionName, targetName);
        }
    }

    public void copyCollection(final String collectionName, final String workspaceName, final String itemName, final String targetName) {
        Map<String, CollectionDTO> store = collectionRepository.getCollectionsStore();

        if (ObjectUtils.isEmpty(workspaceName)) {
            CollectionDTO copiedCollection = new CollectionDTO(targetName, new TreeMap<>());

            Map<String, WorkspaceDTO> workspaces = store.get(collectionName).getWorkspaces();

            for (WorkspaceDTO workspace : workspaces.values()) {
                WorkspaceDTO copiedWorkspace = new WorkspaceDTO(workspace.getName(), targetName, new TreeMap<>());
                copiedCollection.getWorkspaces().put(workspace.getName(), copiedWorkspace);

                Map<String, ItemDTO> items = workspace.getItems();

                for (ItemDTO item : items.values()) {
                    ItemDTO copiedItem = new ItemDTO(item);
                    copiedItem.setCollectionName(targetName);
                    copiedWorkspace.getItems().put(copiedItem.getName(), copiedItem);
                }
            }

            store.put(targetName, copiedCollection);
            log.info("Collection [{}] copy to [{}]", collectionName, targetName);
        } else if (ObjectUtils.isEmpty(itemName)) {
            WorkspaceDTO workspace = store.get(collectionName).getWorkspaces().get(workspaceName);
            WorkspaceDTO copiedWorkspace = new WorkspaceDTO(targetName, collectionName, new TreeMap<>());

            Map<String, ItemDTO> items = workspace.getItems();

            for (ItemDTO item : items.values()) {
                ItemDTO copiedItem = new ItemDTO(item);
                copiedItem.setWorkspaceName(targetName);
                copiedWorkspace.getItems().put(copiedItem.getName(), copiedItem);
            }

            store.get(collectionName).getWorkspaces().put(targetName, copiedWorkspace);
            log.info("Workspace [{}] <in [{}] Collection> copy to [{}]", workspaceName, collectionName, targetName);
        } else {
            ItemDTO item = store.get(collectionName).getWorkspaces().get(workspaceName).getItems().get(itemName);
            ItemDTO copiedItem = new ItemDTO(item);
            copiedItem.setName(targetName);
            store.get(collectionName).getWorkspaces().get(workspaceName).getItems().put(targetName, copiedItem);
            log.info("Item [{}] <in [{}] Workspace in [{}] Collection> copy to [{}]", itemName, workspaceName, collectionName, targetName);
        }
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
