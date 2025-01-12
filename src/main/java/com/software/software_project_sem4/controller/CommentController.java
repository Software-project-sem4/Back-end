package com.software.software_project_sem4.controller;

import com.software.software_project_sem4.dto.CommentRespDto;
import com.software.software_project_sem4.dto.LikeRespDto;
import com.software.software_project_sem4.dto.StatusRespDto;
import com.software.software_project_sem4.service.CommentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{postId}")
    public ResponseEntity<StatusRespDto> addComment(
            @PathVariable Long postId,
            @RequestParam String content,
            HttpSession session) {
        return ResponseEntity.ok(commentService.addComment(postId, content, session));
    }

    @PostMapping("/{commentId}/reply")
    public ResponseEntity<StatusRespDto> addReply(
            @PathVariable Long commentId,
            @RequestParam String content,
            HttpSession session) {
        return ResponseEntity.ok(commentService.addReply(commentId, content, session));
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<LikeRespDto> likeComment(
            @PathVariable Long commentId,
            HttpSession session) {
        return ResponseEntity.ok(commentService.likeComment(commentId, session));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentRespDto>> getCommentsForPost(
            @PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsForPost(postId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<StatusRespDto> deleteComment(
            @PathVariable Long commentId,
            HttpSession session) {
        return ResponseEntity.ok(commentService.deleteComment(commentId, session));
    }
}
