package com.imu.akflow.prompt.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imu.akflow.prompt.mapper.DocMapper;
import com.imu.akflow.common.model.PageResult;
import com.imu.akflow.prompt.model.entity.Doc;
import com.imu.akflow.prompt.model.entity.Tag;
import com.imu.akflow.prompt.model.param.DocUploadParam;
import com.imu.akflow.prompt.model.vo.DocVO;
import com.imu.akflow.prompt.model.vo.TagVO;
import com.imu.akflow.common.utils.FileUtil;
import com.imu.akflow.common.utils.StrBitMapUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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

    @Transactional
    @CacheEvict(value = "docs", allEntries = true)
    public void uploadMdFile(DocUploadParam param) throws Exception {
        File mdFile = FileUtil.getMdFile(param.getPath());

        Doc doc = Doc.init(mdFile, param.getTagIds());
        Doc hisDoc = docMapper.queryByDocTitle(doc.getDocTitle());
        if (hisDoc != null) {
            doc.setVersion(1 + hisDoc.getVersion());
            deleteDoc(hisDoc);
        }
        this.save(doc);
        doc = docMapper.queryByDocTitle(doc.getDocTitle());

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

        Map<Integer, Set<Integer>> docTagMap = new HashMap<>();
        for (Doc doc : docs) {
            Set<Integer> tagIds = StrBitMapUtil.bitmapToSet(doc.getTagBitmap());
            if (CollectionUtils.isNotEmpty(tagIds)) {
                docTagMap.put(doc.getDocId(), tagIds);
            }
        }

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

    @Transactional
    @CacheEvict(value = "docs", allEntries = true)
    public void updateDocContent(Integer docId, String content) {
        Doc doc = this.getById(docId);
        if (doc == null) {
            throw new IllegalArgumentException("文档不存在: " + docId);
        }
        doc.setDocContent(content);
        this.updateById(doc);
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
        Page<Doc> result = docMapper.selectPageWithoutContent(pageParam, keyword);

        return PageResult.of(
                result.getRecords().stream()
                        .map(this::convertToVOWithoutContent)
                        .collect(Collectors.toList()),
                result.getTotal()
        );
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
        Page<Doc> result = docMapper.selectPageByDocIds(pageParam, new ArrayList<>(docIds));

        return PageResult.of(
                result.getRecords().stream()
                        .map(this::convertToVOWithoutContent)
                        .collect(Collectors.toList()),
                result.getTotal()
        );
    }

    @Cacheable(value = "docs", key = "'allWithoutContent'")
    public List<Doc> listAllWithoutContent() {
        return docMapper.listAllWithoutContent();
    }

    public DocVO convertToVO(Doc doc) {
        return buildDocVO(doc, true);
    }

    public DocVO convertToVOWithoutContent(Doc doc) {
        return buildDocVO(doc, false);
    }

    private DocVO buildDocVO(Doc doc, boolean includeContent) {
        DocVO vo = new DocVO();
        vo.setDocId(doc.getDocId());
        vo.setDocTitle(doc.getDocTitle());
        vo.setDocContent(includeContent ? doc.getDocContent() : null);
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
            List<TagVO> tagVOs = tagIds.stream()
                    .map(tagMap::get)
                    .filter(t -> t != null)
                    .map(TagVO::from)
                    .collect(Collectors.toList());
            vo.setTags(tagVOs);
        } else {
            vo.setTags(Collections.emptyList());
        }

        return vo;
    }
}
