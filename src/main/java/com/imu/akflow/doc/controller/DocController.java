package com.imu.akflow.doc.controller;

import com.imu.akflow.doc.model.entity.Doc;
import com.imu.akflow.common.model.PageResult;
import com.imu.akflow.common.model.Result;
import com.imu.akflow.doc.model.param.DocContentParam;
import com.imu.akflow.doc.model.param.DocUploadParam;
import com.imu.akflow.doc.model.vo.DocVO;
import com.imu.akflow.doc.service.DocService;
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
    public Result<Void> uploadDoc(@RequestBody DocUploadParam param) throws Exception {
        docService.uploadMdFile(param);
        return Result.success();
    }

    @PostMapping("/create")
    public Result<Void> createDocByContent(@RequestBody DocContentParam param) {
        docService.createDocByContent(param);
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
