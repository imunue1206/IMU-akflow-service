package com.imu.akflow.controller;

import com.imu.akflow.model.entity.Doc;
import com.imu.akflow.model.common.PageResult;
import com.imu.akflow.model.common.Result;
import com.imu.akflow.model.param.UploadParam;
import com.imu.akflow.model.vo.DocVO;
import com.imu.akflow.service.DocService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
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

    @PutMapping("/{docId}/content")
    public Result<Void> updateDocContent(@PathVariable Integer docId, @RequestBody String content) {
        docService.updateDocContent(docId, content);
        return Result.success();
    }

    @GetMapping("/{docId}/export")
    public ResponseEntity<byte[]> exportDoc(@PathVariable Integer docId) {
        Doc doc = docService.getById(docId);
        if (doc == null) {
            throw new IllegalArgumentException("文档不存在: " + docId);
        }

        String fileName = doc.getDocTitle() + ".md";
        byte[] content = doc.getDocContent().getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("text/markdown"))
                .contentLength(content.length)
                .body(content);
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
