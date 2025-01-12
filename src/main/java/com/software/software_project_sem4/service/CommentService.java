package com.software.software_project_sem4.service;

import com.software.software_project_sem4.dto.CommentRespDto;
import com.software.software_project_sem4.dto.LikeRespDto;
import com.software.software_project_sem4.dto.StatusRespDto;
import com.software.software_project_sem4.exception.ResourceNotFoundException;
import com.software.software_project_sem4.model.Comment;
import com.software.software_project_sem4.model.Post;
import com.software.software_project_sem4.model.User;
import com.software.software_project_sem4.repository.CommentRepo;
import com.software.software_project_sem4.repository.PostRepo;
import com.software.software_project_sem4.repository.UserRepo;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentService {

    private final CommentRepo commentRepo;
    private final PostRepo postRepo;
    private final UserRepo userRepo;

    public CommentService(CommentRepo commentRepo, PostRepo postRepo, UserRepo userRepo) {
        this.commentRepo = commentRepo;
        this.postRepo = postRepo;
        this.userRepo = userRepo;
    }

    public StatusRespDto addComment(Long postId, String content, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        Optional<User> userOpt = userRepo.findById(userId);
        Optional<Post> postOpt = postRepo.findById(postId);

        if (userOpt.isEmpty() || postOpt.isEmpty()) {
            throw new ResourceNotFoundException("User or post not found.");
        }

        User user = userOpt.get();
        Post post = postOpt.get();

        Comment comment = new Comment();
        comment.setCommentContent(content);
        comment.setUser(user);
        comment.setPost(post);

        commentRepo.save(comment);

        return new StatusRespDto(true);
    }

    public StatusRespDto addReply(Long commentId, String content, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        Optional<User> userOpt = userRepo.findById(userId);
        Optional<Comment> commentOpt = commentRepo.findById(commentId);

        if (userOpt.isEmpty() || commentOpt.isEmpty()) {
            throw new ResourceNotFoundException("User or comment not found.");
        }

        User user = userOpt.get();
        Comment parentComment = commentOpt.get();

        Comment reply = new Comment();
        reply.setCommentContent(content);
        reply.setUser(user);
        reply.setPost(parentComment.getPost());
        reply.setReplies(parentComment.getReplies());

        commentRepo.save(reply);

        return new StatusRespDto(true);
    }

    public LikeRespDto likeComment(Long commentId, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        Optional<User> userOpt = userRepo.findById(userId);
        Optional<Comment> commentOpt = commentRepo.findById(commentId);

        if (userOpt.isEmpty() || commentOpt.isEmpty()) {
            throw new ResourceNotFoundException("User or comment not found.");
        }

        User user = userOpt.get();
        Comment comment = commentOpt.get();

        if (comment.getLikedByUsers().contains(user)) {
            comment.getLikedByUsers().remove(user);
            commentRepo.save(comment);
            return new LikeRespDto(true, "Comment unliked", false);
        }

        comment.getLikedByUsers().add(user);
        commentRepo.save(comment);

        return new LikeRespDto(true, "Comment liked", true);
    }


    public List<CommentRespDto> getCommentsForPost(Long postId) {
        List<Comment> comments = commentRepo.findByPostId(postId);

        return comments.stream().map(comment -> {
            CommentRespDto dto = new CommentRespDto();
            dto.setId(comment.getId());
            dto.setCommentContent(comment.getCommentContent());
            dto.setLikesCount(comment.getLikedByUsers().size());
            dto.setRepliesCount(comment.getReplies().size());
            dto.setCreatedAt(comment.getCreatedAt());
            dto.setUpdatedAt(comment.getUpdatedAt());

            // Map nested replies if needed
            dto.setReplies(comment.getReplies().stream().map(reply -> {
                CommentRespDto replyDto = new CommentRespDto();
                replyDto.setId(reply.getId());
                replyDto.setCommentContent(reply.getComment().getCommentContent());
                replyDto.setLikesCount(reply.getLikedByUsers().size());
                replyDto.setRepliesCount(reply.getReplies().size());
                replyDto.setCreatedAt(reply.getCreatedAt());
                replyDto.setUpdatedAt(reply.getUpdatedAt());
                return replyDto;
            }).collect(Collectors.toList()));

            return dto;
        }).collect(Collectors.toList());
    }


    public StatusRespDto deleteComment(Long commentId, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        Optional<Comment> commentOpt = commentRepo.findById(commentId);

        if (commentOpt.isEmpty()) {
            throw new ResourceNotFoundException("Comment not found.");
        }

        Comment comment = commentOpt.get();

        if (!comment.getUser().getId().equals(userId)) {
            return new StatusRespDto(false);
        }

        commentRepo.delete(comment);
        return new StatusRespDto(true);
    }
}
