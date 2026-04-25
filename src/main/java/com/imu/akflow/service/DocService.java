package com.imu.akflow.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imu.akflow.mapper.DocMapper;
import com.imu.akflow.model.common.PageResult;
import com.imu.akflow.model.entity.Doc;
import com.imu.akflow.model.entity.Tag;
import com.imu.akflow.model.param.UploadParam;
import com.imu.akflow.model.vo.DocVO;
import com.imu.akflow.model.vo.TagSimpleVO;
import com.imu.akflow.utils.FileUtil;
import com.imu.akflow.utils.StrBitMapUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class DocService extends ServiceImpl<DocMapper, Doc> {

    private final DocMapper docMapper;
    private final BitService bitService;
    private final TagService tagService;

    /**
     * 根据本地上传文件进行文档存档
     */
    @Transactional
    @CacheEvict(value = "docs", allEntries = true)
    public void uploadMdFile(UploadParam param) throws Exception {
        // 读取文件
        File mdFile = FileUtil.getMdFile(param.getPath());

        Doc doc = Doc.init(mdFile, param.getTagIds());
        Doc hisDoc = docMapper.queryByDocTitle(doc.getDocTitle());
        if (hisDoc != null) {
            doc.setVersion(1 + hisDoc.getVersion());
            deleteDoc(hisDoc);
        }
        this.save(doc);
        doc = docMapper.queryByDocTitle(doc.getDocTitle());

        // 同时更新doc和tag的位图信息
        bitService.addDocTags(param.getTagIds(), doc.getDocId());
    }

    @Transactional
    @CacheEvict(value = "docs", allEntries = true)
    public void deleteDoc(Doc doc) {
        this.removeById(doc);
        Set<Integer> tagIds = StrBitMapUtil.bitmapToSet(doc.getTagBitmap());
        bitService.deleteDocTags(tagIds, doc.getDocId());
    }

    @Transactional
    @CacheEvict(value = "docs", allEntries = true)
    public void deleteDocById(Integer docId) {
        Doc doc = this.getById(docId);
        if (doc == null) {
            throw new IllegalArgumentException("文档不存在: " + docId);
        }
        deleteDoc(doc);
    }

    @Transactional
    @CacheEvict(value = "docs", allEntries = true)
    public void batchDeleteDocs(Set<Integer> docIds) {
        if (CollectionUtils.isEmpty(docIds)) {
            return;
        }

        List<Doc> docs = this.listByIds(docIds);
        if (CollectionUtils.isEmpty(docs)) {
            return;
        }

        // 构建 docId -> tagIds 映射
        Map<Integer, Set<Integer>> docTagMap = new HashMap<>();
        for (Doc doc : docs) {
            Set<Integer> tagIds = StrBitMapUtil.bitmapToSet(doc.getTagBitmap());
            if (CollectionUtils.isNotEmpty(tagIds)) {
                docTagMap.put(doc.getDocId(), tagIds);
            }
        }

        // 一次性批量清理所有标签位图
        if (MapUtils.isNotEmpty(docTagMap)) {
            bitService.batchDeleteDocTags(docTagMap);
        }

        this.removeByIds(docIds);
    }

    @Transactional
    @CacheEvict(value = "docs", allEntries = true)
    public void updateDocTags(Integer docId, Set<Integer> newTagIds) {
        Doc doc = this.getById(docId);
        if (doc == null) {
            throw new IllegalArgumentException("文档不存在: " + docId);
        }

        Set<Integer> oldTagIds = StrBitMapUtil.bitmapToSet(doc.getTagBitmap());
        doc.setTagCount(newTagIds != null ? newTagIds.size() : 0);
        doc.setTagBitmap(StrBitMapUtil.toBitmap(newTagIds));
        this.updateById(doc);

        bitService.changeDocTags(oldTagIds, newTagIds, docId);
    }

    public DocVO convertToVO(Doc doc) {
        DocVO vo = new DocVO();
        vo.setDocId(doc.getDocId());
        vo.setDocTitle(doc.getDocTitle());
        vo.setDocContent(doc.getDocContent());
        vo.setUploadPath(doc.getUploadPath());
        vo.setUploadPathType(doc.getUploadPathType() != null ? doc.getUploadPathType().name() : null);
        vo.setTagCount(doc.getTagCount());
        vo.setCreateTime(doc.getCreateTime());
        vo.setUpdateTime(doc.getUpdateTime());

        Set<Integer> tagIds = StrBitMapUtil.bitmapToSet(doc.getTagBitmap());
        if (CollectionUtils.isNotEmpty(tagIds)) {
            List<Tag> allTags = tagService.listAllTags();
            Map<Integer, Tag> tagMap = allTags.stream()
                    .filter(t -> tagIds.contains(t.getTagId()))
                    .collect(Collectors.toMap(Tag::getTagId, t -> t));
            List<TagSimpleVO> tagVOs = tagIds.stream()
                    .map(tagMap::get)
                    .filter(t -> t != null)
                    .map(TagSimpleVO::from)
                    .collect(Collectors.toList());
            vo.setTags(tagVOs);
        } else {
            vo.setTags(Collections.emptyList());
        }

        return vo;
    }

    public DocVO convertToVOWithoutContent(Doc doc) {
        DocVO vo = convertToVO(doc);
        vo.setDocContent(null);
        return vo;
    }

    public DocVO getDocById(Integer docId) {
        Doc doc = this.getById(docId);
        if (doc == null) {
            throw new IllegalArgumentException("文档不存在: " + docId);
        }
        return convertToVO(doc);
    }

    public PageResult<DocVO> pageDocs(Integer page, Integer pageSize, String keyword) {
        Page<Doc> pageParam = new Page<>(page, pageSize);

        var query = Wrappers.lambdaQuery(Doc.class);
        if (StringUtils.isNotBlank(keyword)) {
            query.like(Doc::getDocTitle, keyword);
        }
        query.orderByDesc(Doc::getCreateTime);

        Page<Doc> result = this.page(pageParam, query);

        return PageResult.of(
                result.getRecords().stream()
                        .map(this::convertToVO)
                        .collect(Collectors.toList()),
                result.getTotal()
        );
    }

    public PageResult<DocVO> pageDocsByTagId(Integer tagId, Integer page, Integer pageSize) {
        Tag tag = tagService.getById(tagId);
        if (tag == null) {
            throw new IllegalArgumentException("标签不存在: " + tagId);
        }

        Set<Integer> docIds = StrBitMapUtil.bitmapToSet(tag.getDocBitmap());
        if (CollectionUtils.isEmpty(docIds)) {
            return PageResult.of(Collections.emptyList(), 0L);
        }

        Page<Doc> pageParam = new Page<>(page, pageSize);
        var query = Wrappers.lambdaQuery(Doc.class)
                .in(Doc::getDocId, docIds)
                .orderByDesc(Doc::getCreateTime);

        Page<Doc> result = this.page(pageParam, query);

        return PageResult.of(
                result.getRecords().stream()
                        .map(this::convertToVO)
                        .collect(Collectors.toList()),
                result.getTotal()
        );
    }

    @Cacheable(value = "docs", key = "'allWithoutContent'")
    public List<Doc> listAllWithoutContent() {
        return this.list(Wrappers.lambdaQuery(Doc.class)
                .select(Doc::getDocId, Doc::getDocTitle, Doc::getUploadPath, Doc::getUploadPathType,
                        Doc::getTagBitmap, Doc::getTagCount, Doc::getCreateTime, Doc::getUpdateTime));
    }

    public PageResult<DocVO> searchByTags(Set<Integer> selectedTagIds, Integer page, Integer pageSize) {
        if (CollectionUtils.isEmpty(selectedTagIds)) {
            return PageResult.of(Collections.emptyList(), 0L);
        }

        List<Doc> docs = listAllWithoutContent();
        List<DocVO> matchedDocs = new ArrayList<>();

        for (Doc doc : docs) {
            Set<Integer> docTagIds = StrBitMapUtil.bitmapToSet(doc.getTagBitmap());
            if (CollectionUtils.isEmpty(docTagIds)) {
                continue;
            }

            Set<Integer> intersection = new HashSet<>(docTagIds);
            intersection.retainAll(selectedTagIds);

            if (intersection.isEmpty()) {
                continue;
            }

            int matchCount = intersection.size();
            double relevanceScore = (double) matchCount / selectedTagIds.size();
            boolean isExactMatch = docTagIds.equals(selectedTagIds);

            DocVO vo = convertToVOWithoutContent(doc);
            vo.setMatchCount(matchCount);
            vo.setRelevanceScore(relevanceScore);
            vo.setIsExactMatch(isExactMatch);
            matchedDocs.add(vo);
        }

        matchedDocs.sort((a, b) -> {
            if (!a.getIsExactMatch().equals(b.getIsExactMatch())) {
                return b.getIsExactMatch() ? 1 : -1;
            }
            if (!a.getMatchCount().equals(b.getMatchCount())) {
                return b.getMatchCount() - a.getMatchCount();
            }
            return Double.compare(b.getRelevanceScore(), a.getRelevanceScore());
        });

        long total = matchedDocs.size();
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, matchedDocs.size());

        List<DocVO> pageList = fromIndex < matchedDocs.size()
                ? matchedDocs.subList(fromIndex, toIndex)
                : Collections.emptyList();

        return PageResult.of(pageList, total);
    }
}
