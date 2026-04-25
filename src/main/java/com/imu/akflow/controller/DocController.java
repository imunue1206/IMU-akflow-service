package com.imu.akflow.controller;

import com.imu.akflow.model.common.PageResult;
import com.imu.akflow.model.common.Result;
import com.imu.akflow.model.param.UploadParam;
import com.imu.akflow.model.vo.DocVO;
import com.imu.akflow.service.DocService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/docs")
public class DocController {

    private final DocService docService;

    @PostMapping("/upload")
    public Result<Void> uploadDoc(@RequestBody UploadParam param) throws Exception {
        docService.uploadMdFile(param);
        return Result.success();
    }

    @PutMapping("/tags/{docId}")
    public Result<Void> updateDocTags(@PathVariable Integer docId, @RequestBody Set<Integer> tagIds) {
        docService.updateDocTags(docId, tagIds);
        return Result.success();
    }

    @DeleteMapping("/{docId}")
    public Result<Void> deleteDoc(@PathVariable Integer docId) {
        docService.deleteDocById(docId);
        return Result.success();
    }

    @DeleteMapping("/batch")
    public Result<Void> batchDeleteDocs(@RequestBody Set<Integer> docIds) {
        docService.batchDeleteDocs(docIds);
        return Result.success();
    }

    @GetMapping("/{docId}")
    public Result<DocVO> getDoc(@PathVariable Integer docId) {
        return Result.success(docService.getDocById(docId));
    }

    @GetMapping("/page")
    public Result<PageResult<DocVO>> pageDocs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(docService.pageDocs(page, pageSize, keyword));
    }

    @GetMapping("/search-by-tags")
    public Result<PageResult<DocVO>> searchByTags(
            @RequestParam Set<Integer> tagIds,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(docService.searchByTags(tagIds, page, pageSize));
    }
}
