package com.imu.akflow.ai.controller;

import com.imu.akflow.common.model.PageResult;
import com.imu.akflow.common.model.Result;
import com.imu.akflow.ai.model.param.ConversationParam;
import com.imu.akflow.ai.model.vo.ConversationVO;
import com.imu.akflow.ai.model.vo.MessageVO;
import com.imu.akflow.ai.service.ConversationService;
import com.imu.akflow.ai.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/ai/conversations")
public class ConversationController {

    private final ConversationService conversationService;
    private final MessageService messageService;

    @PostMapping
    public Result<ConversationVO> createConversation(@RequestBody ConversationParam param) {
        return Result.success(conversationService.createConversation(param));
    }

    @GetMapping("/{conversationId}")
    public Result<ConversationVO> getConversation(@PathVariable Integer conversationId) {
        return Result.success(conversationService.getConversationById(conversationId));
    }

    @GetMapping("/page")
    public Result<PageResult<ConversationVO>> pageConversations(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer modelId,
            @RequestParam(required = false) String status) {
        return Result.success(conversationService.pageConversations(page, pageSize, modelId, status));
    }

    @PutMapping("/{conversationId}/archive")
    public Result<Void> archiveConversation(@PathVariable Integer conversationId) {
        conversationService.archiveConversation(conversationId);
        return Result.success();
    }

    @DeleteMapping("/{conversationId}")
    public Result<Void> deleteConversation(@PathVariable Integer conversationId) {
        conversationService.deleteConversation(conversationId);
        return Result.success();
    }

    @PostMapping("/{conversationId}/messages")
    public Result<MessageVO> sendMessage(@PathVariable Integer conversationId, @RequestBody String content) {
        return Result.success(messageService.addUserMessage(conversationId, content));
    }

    @GetMapping("/{conversationId}/messages")
    public Result<PageResult<MessageVO>> pageMessages(
            @PathVariable Integer conversationId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(messageService.pageMessages(conversationId, page, pageSize));
    }
}
