package com.software.software_project_sem4.controller;

import com.software.software_project_sem4.dto.*;
import com.software.software_project_sem4.service.CommentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
    @RequestMapping("api/v1/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<StatusRespDto> addComment(
            @PathVariable Long postId,
            @RequestBody CommentReqDto requestDto,
            HttpSession session) {
        return ResponseEntity.ok(commentService.addComment(postId, requestDto, session));
    }

    @PostMapping("/{commentId}/reply")
    public ResponseEntity<StatusRespDto> addReply(
            @PathVariable Long commentId,
            @RequestBody ReplyReqDto requestDto,
            HttpSession session) {
        return ResponseEntity.ok(commentService.addReply(commentId, requestDto, session));
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<LikeRespDto> likeComment(
            @PathVariable Long commentId,
            HttpSession session) {
        return ResponseEntity.ok(commentService.likeComment(commentId, session));
    }

    @GetMapping
    public ResponseEntity<List<CommentRespDto>> getCommentsForPost(
            @PathVariable Long postId,
            HttpSession session) {
        return ResponseEntity.ok(commentService.getCommentsForPost(postId, session));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<StatusRespDto> deleteComment(
            @PathVariable Long commentId,
            HttpSession session) {
        return ResponseEntity.ok(commentService.deleteComment(commentId, session));
    }

    @DeleteMapping("/{commentId}/replies/{replyId}")
    public ResponseEntity<StatusRespDto> deleteReply(
            @PathVariable Long commentId,
            @PathVariable Long replyId,
            HttpSession session) {
        return ResponseEntity.ok(commentService.deleteReply(replyId, session));
    }

}
