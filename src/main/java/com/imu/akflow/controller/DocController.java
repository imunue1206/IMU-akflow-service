package com.imu.akflow.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imu.akflow.common.PageResult;
import com.imu.akflow.common.Result;
import com.imu.akflow.model.entity.Doc;
import com.imu.akflow.model.entity.Tag;
import com.imu.akflow.model.param.DocParam;
import com.imu.akflow.model.vo.DocVO;
import com.imu.akflow.model.vo.TagSimpleVO;
import com.imu.akflow.service.BitService;
import com.imu.akflow.service.DocService;
import com.imu.akflow.service.TagService;
import com.imu.akflow.utils.StrBitMapUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/docs")
public class DocController {

    private final DocService docService;
    private final BitService bitService;
    private final TagService tagService;

    @PostMapping
    public Result<Void> createDoc(@RequestBody DocParam param) {
        try {
            Doc doc = new Doc();
            doc.setDocTitle(param.getDocTitle());
            doc.setDocContent(param.getDocContent());
            doc.setUploadPath(param.getUploadPath());
            doc.setUploadPathType(com.imu.akflow.enums.UploadPathTypeEnum.valueOf(param.getUploadPathType()));
            doc.setTagCount(CollectionUtils.isEmpty(param.getTagIds()) ? 0 : param.getTagIds().size());
            doc.setTagBitmap(StrBitMapUtil.toBitmap(param.getTagIds()));
            docService.save(doc);

            if (CollectionUtils.isNotEmpty(param.getTagIds())) {
                bitService.addDocTags(param.getTagIds(), doc.getDocId());
            }
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{docId}")
    public Result<Void> updateDoc(@PathVariable Integer docId, @RequestBody DocParam param) {
        Doc doc = docService.getById(docId);
        if (doc == null) {
            return Result.error("文档不存在");
        }

        Set<Integer> oldTagIds = StrBitMapUtil.bitmapToSet(doc.getTagBitmap());

        if (StringUtils.isNotBlank(param.getDocTitle())) {
            doc.setDocTitle(param.getDocTitle());
        }
        if (param.getDocContent() != null) {
            doc.setDocContent(param.getDocContent());
        }
        if (param.getTagIds() != null) {
            doc.setTagCount(param.getTagIds().size());
            doc.setTagBitmap(StrBitMapUtil.toBitmap(param.getTagIds()));
        }
        docService.updateById(doc);

        if (param.getTagIds() != null) {
            bitService.changeDocTags(oldTagIds, param.getTagIds(), docId);
        }
        return Result.success();
    }

    @DeleteMapping("/{docId}")
    public Result<Void> deleteDoc(@PathVariable Integer docId) {
        Doc doc = docService.getById(docId);
        if (doc == null) {
            return Result.error("文档不存在");
        }
        docService.deleteDoc(doc);
        return Result.success();
    }

    @DeleteMapping("/batch")
    public Result<Void> batchDeleteDocs(@RequestBody Set<Integer> docIds) {
        docService.batchDeleteDocs(docIds);
        return Result.success();
    }

    @GetMapping("/{docId}")
    public Result<DocVO> getDoc(@PathVariable Integer docId) {
        Doc doc = docService.getById(docId);
        if (doc == null) {
            return Result.error("文档不存在");
        }
        return Result.success(convertToVO(doc));
    }

    @GetMapping("/page")
    public Result<PageResult<DocVO>> pageDocs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        Page<Doc> pageParam = new Page<>(page, pageSize);

        var query = Wrappers.lambdaQuery(Doc.class);
        if (StringUtils.isNotBlank(keyword)) {
            query.like(Doc::getDocTitle, keyword);
        }
        query.orderByDesc(Doc::getCreateTime);

        Page<Doc> result = docService.page(pageParam, query);

        List<DocVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(PageResult.of(voList, result.getTotal()));
    }

    private DocVO convertToVO(Doc doc) {
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
            List<Tag> tags = tagService.listByIds(tagIds);
            List<TagSimpleVO> tagVOs = tags.stream().map(tag -> {
                TagSimpleVO tagVO = new TagSimpleVO();
                tagVO.setTagId(tag.getTagId());
                tagVO.setTagName(tag.getTagName());
                tagVO.setTagDesc(tag.getTagDesc());
                return tagVO;
            }).collect(Collectors.toList());
            vo.setTags(tagVOs);
        } else {
            vo.setTags(Collections.emptyList());
        }

        return vo;
    }
}
