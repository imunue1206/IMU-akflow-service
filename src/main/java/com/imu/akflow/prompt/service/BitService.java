package com.imu.akflow.prompt.service;

import com.imu.akflow.prompt.mapper.DocMapper;
import com.imu.akflow.prompt.mapper.TagMapper;
import com.imu.akflow.prompt.model.entity.Doc;
import com.imu.akflow.prompt.model.entity.Tag;
import com.imu.akflow.common.utils.StrBitMapUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
@Service
public class BitService {

    private final DocMapper docMapper;
    private final TagMapper tagMapper;

    /**
     * 当文档新增时，进行tag位图增量操作
     */
    @CacheEvict(value = {"docs", "tags"}, allEntries = true)
    public void addDocTags(Set<Integer> tagIds, Integer docId) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return;
        }

        List<Tag> tags = tagMapper.selectBatchIds(tagIds);
        if (CollectionUtils.isEmpty(tags)) {
            return;
        }

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            CompletableFuture<?>[] futures = tags.stream()
                    .map(tag -> CompletableFuture.runAsync(() -> {
                        Set<Integer> docIds = StrBitMapUtil.bitmapToSet(tag.getDocBitmap());
                        docIds.add(docId);
                        tag.setDocBitmap(StrBitMapUtil.toBitmap(docIds));
                        tag.setDocCount(docIds.size());
                    }, executor))
                    .toArray(CompletableFuture[]::new);

            CompletableFuture.allOf(futures).join();
        }

        tagMapper.updateBatchById(tags);
    }

    /**
     * 当文档删除时，进行tag位图删除操作
     */
    @CacheEvict(value = {"docs", "tags"}, allEntries = true)
    public void deleteDocTags(Set<Integer> tagIds, Integer docId) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return;
        }

        List<Tag> tags = tagMapper.selectBatchIds(tagIds);
        if (CollectionUtils.isEmpty(tags)) {
            return;
        }

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            CompletableFuture<?>[] futures = tags.stream()
                    .map(tag -> CompletableFuture.runAsync(() -> {
                        Set<Integer> docIds = StrBitMapUtil.bitmapToSet(tag.getDocBitmap());
                        docIds.remove(docId);
                        tag.setDocBitmap(StrBitMapUtil.toBitmap(docIds));
                        tag.setDocCount(docIds.size());
                    }, executor))
                    .toArray(CompletableFuture[]::new);

            CompletableFuture.allOf(futures).join();
        }

        tagMapper.updateBatchById(tags);
    }

    /**
     * 批量删除文档时，一次性清理所有标签位图
     * @param docTagMap docId -> tagIds 映射
     */
    @CacheEvict(value = {"docs", "tags"}, allEntries = true)
    public void batchDeleteDocTags(Map<Integer, Set<Integer>> docTagMap) {
        if (MapUtils.isEmpty(docTagMap)) {
            return;
        }

        // 反转映射：tagId -> Set<docId>
        Map<Integer, Set<Integer>> tagDocMap = new HashMap<>();
        for (Map.Entry<Integer, Set<Integer>> entry : docTagMap.entrySet()) {
            Integer docId = entry.getKey();
            for (Integer tagId : entry.getValue()) {
                tagDocMap.computeIfAbsent(tagId, k -> new HashSet<>()).add(docId);
            }
        }

        // 批量查询所有涉及的标签
        List<Tag> tags = tagMapper.selectBatchIds(tagDocMap.keySet());
        if (CollectionUtils.isEmpty(tags)) {
            return;
        }

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            CompletableFuture<?>[] futures = tags.stream()
                    .map(tag -> CompletableFuture.runAsync(() -> {
                        Set<Integer> docIds = StrBitMapUtil.bitmapToSet(tag.getDocBitmap());
                        Set<Integer> toRemove = tagDocMap.get(tag.getTagId());
                        if (CollectionUtils.isNotEmpty(toRemove)) {
                            docIds.removeAll(toRemove);
                        }
                        tag.setDocBitmap(StrBitMapUtil.toBitmap(docIds));
                        tag.setDocCount(docIds.size());
                    }, executor))
                    .toArray(CompletableFuture[]::new);

            CompletableFuture.allOf(futures).join();
        }

        tagMapper.updateBatchById(tags);
    }

    /**
     * 当用户手动编辑文档设计标签时，进行tag位图维护
     */
    @CacheEvict(value = {"docs", "tags"}, allEntries = true)
    public void changeDocTags(Set<Integer> oldTagIds, Set<Integer> newTagIds, Integer docId) {
        Set<Integer> safeOld = oldTagIds != null ? oldTagIds : Set.of();
        Set<Integer> safeNew = newTagIds != null ? newTagIds : Set.of();

        Set<Integer> removedTags = new HashSet<>(safeOld);
        removedTags.removeAll(safeNew);
        if (CollectionUtils.isNotEmpty(removedTags)) {
            deleteDocTags(removedTags, docId);
        }

        Set<Integer> addedTags = new HashSet<>(safeNew);
        addedTags.removeAll(safeOld);
        if (CollectionUtils.isNotEmpty(addedTags)) {
            addDocTags(addedTags, docId);
        }
    }

    /**
     * 从文档位图中移除指定标签（用于删除标签时清理关联文档）
     */
    @CacheEvict(value = {"docs", "tags"}, allEntries = true)
    public void removeTagFromDocs(Integer tagId, Set<Integer> docIds) {
        if (CollectionUtils.isEmpty(docIds)) {
            return;
        }

        List<Doc> docs = docMapper.selectBatchIds(docIds);
        if (CollectionUtils.isEmpty(docs)) {
            return;
        }

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            CompletableFuture<?>[] futures = docs.stream()
                    .map(doc -> CompletableFuture.runAsync(() -> {
                        Set<Integer> tagIdSet = StrBitMapUtil.bitmapToSet(doc.getTagBitmap());
                        tagIdSet.remove(tagId);
                        doc.setTagBitmap(StrBitMapUtil.toBitmap(tagIdSet));
                        doc.setTagCount(tagIdSet.size());
                    }, executor))
                    .toArray(CompletableFuture[]::new);

            CompletableFuture.allOf(futures).join();
        }

        docMapper.updateBatchById(docs);
    }

    /**
     * 批量删除标签时，一次性清理所有文档位图
     * @param tagDocMap tagId -> docIds 映射
     */
    @CacheEvict(value = {"docs", "tags"}, allEntries = true)
    public void batchRemoveTagFromDocs(Map<Integer, Set<Integer>> tagDocMap) {
        if (MapUtils.isEmpty(tagDocMap)) {
            return;
        }

        // 收集所有涉及的docId
        Set<Integer> allDocIds = new HashSet<>();
        tagDocMap.values().forEach(allDocIds::addAll);

        // 批量查询所有涉及的文档
        List<Doc> docs = docMapper.selectBatchIds(allDocIds);
        if (CollectionUtils.isEmpty(docs)) {
            return;
        }

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            CompletableFuture<?>[] futures = docs.stream()
                    .map(doc -> CompletableFuture.runAsync(() -> {
                        Set<Integer> tagIdSet = StrBitMapUtil.bitmapToSet(doc.getTagBitmap());
                        // 移除所有需要删除的标签
                        for (Map.Entry<Integer, Set<Integer>> entry : tagDocMap.entrySet()) {
                            if (entry.getValue().contains(doc.getDocId())) {
                                tagIdSet.remove(entry.getKey());
                            }
                        }
                        doc.setTagBitmap(StrBitMapUtil.toBitmap(tagIdSet));
                        doc.setTagCount(tagIdSet.size());
                    }, executor))
                    .toArray(CompletableFuture[]::new);

            CompletableFuture.allOf(futures).join();
        }

        docMapper.updateBatchById(docs);
    }
}
