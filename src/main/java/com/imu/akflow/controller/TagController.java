package com.imu.akflow.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imu.akflow.common.PageResult;
import com.imu.akflow.common.Result;
import com.imu.akflow.model.entity.Doc;
import com.imu.akflow.model.entity.Tag;
import com.imu.akflow.model.param.TagParam;
import com.imu.akflow.model.vo.DocVO;
import com.imu.akflow.model.vo.TagSimpleVO;
import com.imu.akflow.model.vo.TagVO;
import com.imu.akflow.service.BitService;
import com.imu.akflow.service.DocService;
import com.imu.akflow.service.TagService;
import com.imu.akflow.utils.StrBitMapUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagService tagService;
    private final BitService bitService;
    private final DocService docService;

    @PostMapping
    public Result<Void> createTag(@RequestBody TagParam param) {
        tagService.addTag(param.getTagName(), param.getTagDesc());
        return Result.success();
    }

    @PutMapping("/{tagId}")
    public Result<Void> updateTag(@PathVariable Integer tagId, @RequestBody TagParam param) {
        tagService.editTagInfo(tagId, param.getTagName(), param.getTagDesc());
        return Result.success();
    }

    @DeleteMapping("/{tagId}")
    public Result<Void> deleteTag(@PathVariable Integer tagId) {
        tagService.deleteTag(tagId);
        return Result.success();
    }

    @DeleteMapping("/batch")
    public Result<Void> batchDeleteTags(@RequestBody Set<Integer> tagIds) {
        tagService.batchDeleteTags(tagIds);
        return Result.success();
    }

    @GetMapping("/{tagId}")
    public Result<TagVO> getTag(@PathVariable Integer tagId) {
        Tag tag = tagService.getById(tagId);
        if (tag == null) {
            return Result.error("标签不存在");
        }
        return Result.success(convertToVO(tag));
    }

    @GetMapping("/page")
    public Result<PageResult<TagVO>> pageTags(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        Page<Tag> pageParam = new Page<>(page, pageSize);

        var query = Wrappers.lambdaQuery(Tag.class);
        if (StringUtils.isNotBlank(keyword)) {
            query.like(Tag::getTagName, keyword);
        }
        query.orderByDesc(Tag::getCreateTime);

        Page<Tag> result = tagService.page(pageParam, query);

        List<TagVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return Result.success(PageResult.of(voList, result.getTotal()));
    }

    @GetMapping("/{tagId}/docs")
    public Result<PageResult<DocVO>> getTagDocs(
            @PathVariable Integer tagId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Tag tag = tagService.getById(tagId);
        if (tag == null) {
            return Result.error("标签不存在");
        }

        Set<Integer> docIds = StrBitMapUtil.bitmapToSet(tag.getDocBitmap());
        if (CollectionUtils.isEmpty(docIds)) {
            return Result.success(PageResult.of(Collections.emptyList(), 0L));
        }

        Page<Doc> pageParam = new Page<>(page, pageSize);
        var query = Wrappers.lambdaQuery(Doc.class)
                .in(Doc::getDocId, docIds)
                .orderByDesc(Doc::getCreateTime);

        Page<Doc> result = docService.page(pageParam, query);

        List<DocVO> voList = result.getRecords().stream()
                .map(this::convertDocToVO)
                .collect(Collectors.toList());

        return Result.success(PageResult.of(voList, result.getTotal()));
    }

    private TagVO convertToVO(Tag tag) {
        TagVO vo = new TagVO();
        vo.setTagId(tag.getTagId());
        vo.setTagName(tag.getTagName());
        vo.setTagDesc(tag.getTagDesc());
        vo.setDocCount(tag.getDocCount());
        vo.setCreateTime(tag.getCreateTime());
        vo.setUpdateTime(tag.getUpdateTime());
        return vo;
    }

    private DocVO convertDocToVO(Doc doc) {
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
            List<TagSimpleVO> tagVOs = tags.stream().map(t -> {
                TagSimpleVO tagVO = new TagSimpleVO();
                tagVO.setTagId(t.getTagId());
                tagVO.setTagName(t.getTagName());
                tagVO.setTagDesc(t.getTagDesc());
                return tagVO;
            }).collect(Collectors.toList());
            vo.setTags(tagVOs);
        } else {
            vo.setTags(Collections.emptyList());
        }

        return vo;
    }
}
