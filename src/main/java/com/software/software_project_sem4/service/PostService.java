package com.software.software_project_sem4.service;

import com.software.software_project_sem4.dto.*;
import com.software.software_project_sem4.model.File;
import com.software.software_project_sem4.model.Post;
import com.software.software_project_sem4.model.User;
import com.software.software_project_sem4.repository.FileRepo;
import com.software.software_project_sem4.repository.PostRepo;
import com.software.software_project_sem4.repository.UserRepo;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
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

    public List<PostRespDto> getList() {
        List<Post> posts = postRepo.findAll();
        return posts.stream().map(post -> {
            UserRespDto user = new UserRespDto();
            user.setId(post.getUser().getId());
            user.setUserName(post.getUser().getUserName());
            user.setEmail(post.getUser().getEmail());

            PostRespDto postRespDto = new PostRespDto();
            postRespDto.setId(post.getId());
            postRespDto.setContent(post.getContent());
            postRespDto.setUser(user);

            postRespDto.setFiles(post.getFiles());
            postRespDto.setCreatedAt(post.getCreatedAt());
            postRespDto.setUpdatedAt(post.getUpdatedAt());
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
          postRespDto.setFiles(post.get().getFiles());
          postRespDto.setCreatedAt(post.get().getCreatedAt());
          postRespDto.setUpdatedAt(post.get().getUpdatedAt());
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
        postRespDto.setFiles(post.get().getFiles());
        postRespDto.setCreatedAt(post.get().getCreatedAt());
        postRespDto.setUpdatedAt(post.get().getUpdatedAt());
        return postRespDto;
    }

    public StatusRespDto delete(Long postId, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");

        int rows = postRepo.deleteByPostIdAndUserId(postId, userId);

        StatusRespDto statusRespDto = new StatusRespDto();
        statusRespDto.setSuccess(true);

        if(rows < 1){
            statusRespDto.setSuccess(false);
        }

        return statusRespDto;
    }

    @Transactional
    public StatusRespDto like(Long postId, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        Optional<User> userOpt = userRepo.findById(userId);
        Optional<Post> postOpt = postRepo.findById(postId);

        StatusRespDto statusRespDto = new StatusRespDto();

        if (userOpt.isEmpty() || postOpt.isEmpty()) {
            statusRespDto.setSuccess(false);
            return statusRespDto;
        }

        User user = userOpt.get();
        Post post = postOpt.get();

        // Add post to user's likedPosts
        user.getLikedPosts().add(post);

        // Add user to post's likedByUsers
        post.getLikedByUsers().add(user);

        // Save the updated entities
        userRepo.save(user);
        postRepo.save(post);

        statusRespDto.setSuccess(true);
        return statusRespDto;
    }



    public StatusRespDto save(Long postId, HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        Optional<User> user = userRepo.findById(userId);
        Optional<Post> post = postRepo.findById(postId);

        StatusRespDto statusRespDto = new StatusRespDto();

        if (user.isEmpty() || post.isEmpty()) {
            statusRespDto.setSuccess(false);
            return statusRespDto;
        }

        user.get().getSavedPosts().add(post.get());
        userRepo.save(user.get());

        statusRespDto.setSuccess(true);
        return statusRespDto;
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

        // Remove the file from the post and explicitly delete it
        File file = fileOpt.get();
//        post.getFiles().remove(file); // Remove from the collection
        fileRepo.deleteById(file.getId()); // Explicitly delete the file entity

        statusRespDto.setSuccess(true);
        return statusRespDto;
    }

}
