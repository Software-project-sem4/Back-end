package com.software.software_project_sem4.service;

import com.software.software_project_sem4.dto.*;
import com.software.software_project_sem4.exception.ResourceNotFoundException;
import com.software.software_project_sem4.model.File;
import com.software.software_project_sem4.model.Post;
import com.software.software_project_sem4.model.User;
import com.software.software_project_sem4.repository.FileRepo;
import com.software.software_project_sem4.repository.PostRepo;
import com.software.software_project_sem4.repository.UserRepo;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {
    private final PostRepo postRepo;
    private final UserRepo userRepo;
    private final FileRepo fileRepo;

    public PostService(PostRepo postRepo, UserRepo userRepo, FileRepo fileRepo) {
        this.postRepo = postRepo;
        this.userRepo = userRepo;
        this.fileRepo = fileRepo;
    }

    public StatusRespDto create(String content, MultipartFile[] files, HttpSession session) throws IOException {
        Long userId = (Long) session.getAttribute("user_id");
        Optional<User> user = userRepo.findById(userId);

        Post post = new Post();
        post.setUser(user.get());
        post.setContent(content);

        for (MultipartFile multipartFile : files) {
            File file = new File();
            file.setPost(post);
            file.setFileName(multipartFile.getOriginalFilename());
            file.setFileData(multipartFile.getBytes());
            post.getFiles().add(file);
        }


        postRepo.saveAndFlush(post);

        StatusRespDto postRespDto = new StatusRespDto();
        postRespDto.setSuccess(true);

        return postRespDto;
    }

    public List<PostRespDto> getList(HttpSession session, Optional<Boolean> savedByUser) {
        Long userId = (Long) session.getAttribute("user_id");
        List<Post> posts;
        if (savedByUser.isEmpty()) {
            posts = postRepo.findAll();
        } else {
            Optional<User> curUser = userRepo.findById(userId);
            posts = curUser.get().getSavedPosts().stream().toList();
        }
        return posts.stream().map(post -> {
            int total = post.getComments().stream()
                    .mapToInt(comment -> 1 + comment.getReplies().size())
                    .sum();
            UserRespDto user = new UserRespDto();
            user.setId(post.getUser().getId());
            user.setUserName(post.getUser().getUserName());
            user.setEmail(post.getUser().getEmail());
            user.setAvatar(post.getUser().getAvatar());

//            const total = comments.reduce(
//                    (count, comment) =>
//            count + 1 + (comment.replies ? comment.replies.length : 0),
//                    0
//             );


            PostRespDto postRespDto = new PostRespDto();
            postRespDto.setId(post.getId());
            postRespDto.setContent(post.getContent());
            postRespDto.setUser(user);
            postRespDto.setTotalLikes(post.getTotalLikes());
            postRespDto.setTotalSaves(post.getTotalSaves());
            postRespDto.setTotalCommentsReplies(total);

            if(userId != null) {
                Optional<User> curUser = userRepo.findById(userId);
                postRespDto.setLikedByCurrentUser(post.getLikedByCurUser(curUser.get()));
                postRespDto.setSavedByCurrentUser(post.getSavedByCurUser(curUser.get()));
            }

            Set<PostFileRespDto> files = post.getFiles().stream().map(file -> {
                PostFileRespDto postFileRespDto = new PostFileRespDto();
                postFileRespDto.setFileName(file.getFileName());
                postFileRespDto.setId(file.getId());
                return postFileRespDto;
            }).collect(Collectors.toSet());

            postRespDto.setFiles(files);

            postRespDto.setCreatedAt(post.getCreatedAt().toString());
            postRespDto.setUpdatedAt(post.getUpdatedAt().toString());
            return postRespDto;
        }).collect(Collectors.toList());
    }

    public PostRespDto getDetail(Long id) {
          Optional<Post> post = postRepo.findById(id);
          PostRespDto postRespDto = new PostRespDto();
          if(post.isEmpty()) {
             return postRespDto;
          }
          postRespDto.setId(post.get().getId());
          postRespDto.setContent(post.get().getContent());

        Set<PostFileRespDto> files = post.get().getFiles().stream().map(file -> {
            PostFileRespDto postFileRespDto = new PostFileRespDto();
            postFileRespDto.setFileName(file.getFileName());
            return postFileRespDto;
        }).collect(Collectors.toSet());

        postRespDto.setFiles(files);

          postRespDto.setCreatedAt(post.get().getCreatedAt().toString());
          postRespDto.setUpdatedAt(post.get().getUpdatedAt().toString());
          return postRespDto;
    }

    public PostRespDto update(Long postId, PostUpdateReqDto postReqDto, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");

        Optional<Post> post = postRepo.findByPostIdAndUserId(postId, userId);

        PostRespDto postRespDto = new PostRespDto();
        if(post.isEmpty()) {
            return postRespDto;
        }

        post.get().setContent(postReqDto.getContent());
        postRepo.saveAndFlush(post.get());
        postRespDto.setId(post.get().getId());
        postRespDto.setContent(post.get().getContent());

        Set<PostFileRespDto> files = post.get().getFiles().stream().map(file -> {
            PostFileRespDto postFileRespDto = new PostFileRespDto();
            postFileRespDto.setFileName(file.getFileName());
            return postFileRespDto;
        }).collect(Collectors.toSet());

        postRespDto.setFiles(files);

        postRespDto.setCreatedAt(post.get().getCreatedAt().toString());
        postRespDto.setUpdatedAt(post.get().getUpdatedAt().toString());
        return postRespDto;
    }

    public StatusRespDto delete(Long postId, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");

        int rows = postRepo.deleteByPostIdAndUserIdRaw(postId, userId);

        StatusRespDto statusRespDto = new StatusRespDto();
        statusRespDto.setSuccess(true);

        if(rows < 1){
            statusRespDto.setSuccess(false);
        }

        return statusRespDto;
    }

    @Transactional
    public LikeRespDto like(Long postId, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        Optional<User> userOpt = userRepo.findById(userId);
        Optional<Post> postOpt = postRepo.findById(postId);

        LikeRespDto likeRespDto = new LikeRespDto();

        if (userOpt.isEmpty() || postOpt.isEmpty()) {
            likeRespDto.setSuccess(false);
            likeRespDto.setMessage("User or post not found.");
            likeRespDto.setIsLiked(false); // Default value
            return likeRespDto;
        }

        User user = userOpt.get();
        Post post = postOpt.get();

        // Check if the post is already liked by the user
        if (user.getLikedPosts().contains(post)) {
            // Unlike the post
            user.getLikedPosts().remove(post);
            post.getLikedByUsers().remove(user);

            // Save the updated entities
            userRepo.save(user);
            postRepo.save(post);

            likeRespDto.setSuccess(true);
            likeRespDto.setMessage("Post unliked successfully.");
            likeRespDto.setIsLiked(false); // Post is no longer liked
            return likeRespDto;
        }

        // Like the post
        user.getLikedPosts().add(post);
        post.getLikedByUsers().add(user);

        // Save the updated entities
        userRepo.save(user);
        postRepo.save(post);

        likeRespDto.setSuccess(true);
        likeRespDto.setMessage("Post liked successfully.");
        likeRespDto.setIsLiked(true); // Post is now liked
        return likeRespDto;
    }



    public LikesCountRespDto getLikesCount(Long postId) {
        Optional<Post> postOpt = postRepo.findById(postId);
        LikesCountRespDto likesCountRespDto = new LikesCountRespDto();

        if (postOpt.isEmpty()) {
            likesCountRespDto.setSuccess(false);
            likesCountRespDto.setLikesCount(0);
            likesCountRespDto.setMessage("Post not found.");
            return likesCountRespDto;
        }

        Post post = postOpt.get();
        int likesCount = post.getLikedByUsers().size();

        likesCountRespDto.setSuccess(true);
        likesCountRespDto.setLikesCount(likesCount);
        likesCountRespDto.setMessage("Likes count retrieved successfully.");

        return likesCountRespDto;
    }


    public SaveRespDto save(Long postId, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        Optional<User> userOpt = userRepo.findById(userId);
        Optional<Post> postOpt = postRepo.findById(postId);

        SaveRespDto saveRespDto = new SaveRespDto();

        if (userOpt.isEmpty() || postOpt.isEmpty()) {
            saveRespDto.setSuccess(false);
            saveRespDto.setMessage("User or post not found.");
            saveRespDto.setIsSaved(false);
            return saveRespDto;
        }

        User user = userOpt.get();
        Post post = postOpt.get();

        // Check if the post is already saved by the user
        if (user.getSavedPosts().contains(post)) {
            // Unsave the post
            user.getSavedPosts().remove(post);
            userRepo.save(user);

            saveRespDto.setSuccess(true);
            saveRespDto.setMessage("Post unsaved successfully.");
            saveRespDto.setIsSaved(false);
            return saveRespDto;
        }

        // Save the post
        user.getSavedPosts().add(post);
        userRepo.save(user);

        saveRespDto.setSuccess(true);
        saveRespDto.setMessage("Post saved successfully.");
        saveRespDto.setIsSaved(true);
        return saveRespDto;
    }

    public SaveCountRespDto getSaveCount(Long postId) {
        Optional<Post> postOpt = postRepo.findById(postId);
        SaveCountRespDto saveCountRespDto = new SaveCountRespDto();

        if (postOpt.isEmpty()) {
            saveCountRespDto.setSuccess(false);
            saveCountRespDto.setSaveCount(0);
            saveCountRespDto.setMessage("Post not found.");
            return saveCountRespDto;
        }

        Post post = postOpt.get();
        int saveCount = post.getSavedByUsers().size();

        saveCountRespDto.setSuccess(true);
        saveCountRespDto.setSaveCount(saveCount);
        saveCountRespDto.setMessage("Save count retrieved successfully.");
        return saveCountRespDto;
    }


    @Transactional
    public StatusRespDto uploadFiles(Long postId, MultipartFile[] files, HttpSession session) throws IOException {
        Long userId = (Long) session.getAttribute("user_id");

        // Retrieve the post and validate ownership
        Optional<Post> postOpt = postRepo.findByPostIdAndUserId(postId, userId);
        StatusRespDto statusRespDto = new StatusRespDto();

        if (postOpt.isEmpty()) {
            statusRespDto.setSuccess(false);
            return statusRespDto;
        }

        Post post = postOpt.get();

        // Process each file and associate it with the post
        for (MultipartFile multipartFile : files) {
            File file = new File();
            file.setPost(post);
            file.setFileName(multipartFile.getOriginalFilename());
            file.setFileData(multipartFile.getBytes());
            post.getFiles().add(file); // Add file to the post
        }

        postRepo.save(post); // Save changes to the database

        statusRespDto.setSuccess(true);
        return statusRespDto;
    }

    public ResponseEntity<byte[]> downloadFile(Long postId, Long fileId, HttpSession session) {
        // Validate that the post exists
        Optional<Post> postOpt = postRepo.findById(postId);
        if (postOpt.isEmpty()) {
            throw new ResourceNotFoundException("Post not found.");
        }

        Post post = postOpt.get();

        // Find the requested file in the post
        Optional<File> fileOpt = post.getFiles().stream()
                .filter(file -> file.getId().equals(fileId))
                .findFirst();

        if (fileOpt.isEmpty()) {
            throw new ResourceNotFoundException("File not found.");
        }

        File file = fileOpt.get();

        // Build the response
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file.getFileData());
    }




    @Transactional
    public StatusRespDto deleteFile(Long postId, Long fileId, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");

        // Validate ownership
        Optional<Post> postOpt = postRepo.findByPostIdAndUserId(postId, userId);
        StatusRespDto statusRespDto = new StatusRespDto();

        if (postOpt.isEmpty()) {
            statusRespDto.setSuccess(false);
            return statusRespDto;
        }

        Post post = postOpt.get();

        // Find the file in the post
        Optional<File> fileOpt = post.getFiles().stream()
                .filter(file -> file.getId().equals(fileId))
                .findFirst();

        if (fileOpt.isEmpty()) {
            statusRespDto.setSuccess(false);
            return statusRespDto;
        }

        File file = fileOpt.get();

        // Remove the file from the collection and delete it
        post.getFiles().remove(file);
        fileRepo.delete(file);

        statusRespDto.setSuccess(true);
        return statusRespDto;
    }

}
