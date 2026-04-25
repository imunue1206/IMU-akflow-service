package com.imu.akflow.prompt.controller;

import com.imu.akflow.common.model.PageResult;
import com.imu.akflow.common.model.Result;
import com.imu.akflow.prompt.model.param.TagParam;
import com.imu.akflow.prompt.model.vo.DocVO;
import com.imu.akflow.prompt.model.vo.TagVO;
import com.imu.akflow.prompt.service.DocService;
import com.imu.akflow.prompt.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagService tagService;
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
        return Result.success(tagService.getTagById(tagId));
    }

    @GetMapping("/page")
    public Result<PageResult<TagVO>> pageTags(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(tagService.pageTags(page, pageSize, keyword));
    }

    @GetMapping("/{tagId}/docs")
    public Result<PageResult<DocVO>> getTagDocs(
            @PathVariable Integer tagId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(docService.pageDocsByTagId(tagId, page, pageSize));
    }
}
