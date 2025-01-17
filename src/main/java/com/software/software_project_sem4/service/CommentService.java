package com.software.software_project_sem4.service;

import com.software.software_project_sem4.dto.*;
import com.software.software_project_sem4.exception.ResourceNotFoundException;
import com.software.software_project_sem4.model.Comment;
import com.software.software_project_sem4.model.Post;
import com.software.software_project_sem4.model.Reply;
import com.software.software_project_sem4.model.User;
import com.software.software_project_sem4.repository.CommentRepo;
import com.software.software_project_sem4.repository.PostRepo;
import com.software.software_project_sem4.repository.ReplyRepo;
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
    private final ReplyRepo replyRepo;

    public CommentService(CommentRepo commentRepo, PostRepo postRepo, UserRepo userRepo, ReplyRepo replyRepo) {
        this.commentRepo = commentRepo;
        this.postRepo = postRepo;
        this.userRepo = userRepo;
        this.replyRepo = replyRepo;
    }

    public StatusRespDto addComment(Long postId, CommentReqDto requestDto, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        Optional<User> userOpt = userRepo.findById(userId);
        Optional<Post> postOpt = postRepo.findById(postId);

        if (userOpt.isEmpty() || postOpt.isEmpty()) {
            throw new ResourceNotFoundException("User or post not found.");
        }

        User user = userOpt.get();
        Post post = postOpt.get();

        Comment comment = new Comment();
        comment.setCommentContent(requestDto.getContent());
        comment.setUser(user);
        comment.setPost(post);

        commentRepo.save(comment);

        return new StatusRespDto(true);
    }

    public StatusRespDto addReply(Long commentId, ReplyReqDto requestDto, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        Optional<User> userOpt = userRepo.findById(userId);
        Optional<Comment> commentOpt = commentRepo.findById(commentId);

        if (userOpt.isEmpty() || commentOpt.isEmpty()) {
            throw new ResourceNotFoundException("User or comment not found.");
        }

        User user = userOpt.get();
        Comment parentComment = commentOpt.get();

        // Create a new reply
        Reply reply = new Reply();
        reply.setComment(parentComment);
        reply.setUser(user);
        reply.setReplyContent(requestDto.getContent());

        // If replying to another reply, set the parent reply
        if (requestDto.getParentReplyId() != null) {
            Optional<Reply> parentReplyOpt = replyRepo.findById(requestDto.getParentReplyId());
            if (parentReplyOpt.isPresent()) {
                reply.setParentReply(parentReplyOpt.get());
            } else {
                throw new ResourceNotFoundException("Parent reply not found.");
            }
        }

        // Save the reply
        replyRepo.save(reply);

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
            user.getLikedComments().remove(comment);
            commentRepo.save(comment);
            userRepo.save(user);
            return new LikeRespDto(true, "Comment unliked", false);
        }

        comment.getLikedByUsers().add(user);
        user.getLikedComments().add(comment);
        userRepo.save(user);
        commentRepo.save(comment);

        return new LikeRespDto(true, "Comment liked", true);
    }


    public List<CommentRespDto> getCommentsForPost(Long postId, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id"); // Get user ID from session
        List<Comment> comments = commentRepo.findByPostId(postId);

        int totalCommentsCount = comments.size(); // Calculate the total number of comments

        return comments.stream().map(comment -> {
            CommentRespDto dto = new CommentRespDto();
            UserRespDto userDto = new UserRespDto();
            userDto.setId(comment.getUser().getId());
            userDto.setAvatar(comment.getUser().getAvatar());
            userDto.setUserName(comment.getUser().getUserName());
            dto.setId(comment.getId());
            dto.setUser(userDto);
            dto.setCommentContent(comment.getCommentContent());
            dto.setLikesCount(comment.getLikedByUsers().size());
            dto.setRepliesCount(comment.getReplies().size());
            dto.setCreatedAt(comment.getCreatedAt().toString());
            dto.setUpdatedAt(comment.getUpdatedAt().toString());

            // Check if the current user has liked this comment
            dto.setLikedByCurrentUser(
                    comment.getLikedByUsers().stream().anyMatch(user -> user.getId().equals(userId))
            );

            // Include the total comments count in the DTO
            dto.setCommentsCount(totalCommentsCount);

            // Map nested replies if needed
            dto.setReplies(comment.getReplies().stream().map(reply -> {
                UserRespDto userReplyDto = new UserRespDto();
                userReplyDto.setId(reply.getUser().getId());
                userReplyDto.setAvatar(reply.getUser().getAvatar());
                userReplyDto.setUserName(reply.getUser().getUserName());
                ReplyRespDto replyDto = new ReplyRespDto();
                replyDto.setId(reply.getId());
                replyDto.setReplyContent(reply.getReplyContent());
                replyDto.setLikedByUsers(reply.getLikedByUsers());
//                replyDto.setRepliesCount(reply.getChildReplies().size());
                replyDto.setPostId(reply.getComment().getPost().getId());
                replyDto.setUser(userReplyDto);
                replyDto.setCreatedAt(reply.getCreatedAt().toString());
                replyDto.setUpdatedAt(reply.getUpdatedAt().toString());

                // Check if the current user has liked this reply
                replyDto.setLikedByCurrentUser(
                        reply.getLikedByUsers().stream().anyMatch(user -> user.getId().equals(userId))
                );

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

    public StatusRespDto deleteReply(Long replyId, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        Optional<Reply> replyOpt = replyRepo.findById(replyId);

        if (replyOpt.isEmpty()) {
            throw new ResourceNotFoundException("Reply not found.");
        }

        Reply reply = replyOpt.get();

        // Check if the logged-in user is the owner of the reply
        if (!reply.getUser().getId().equals(userId)) {
            return new StatusRespDto(false);
        }

        replyRepo.delete(reply);
        return new StatusRespDto(true);
    }
}
