package com.software.software_project_sem4.controller;

import com.software.software_project_sem4.aspect.AuthGuard;
import com.software.software_project_sem4.dto.*;
import com.software.software_project_sem4.service.PostService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/posts")
public class PostController {
    private final PostService postService;
    public PostController(PostService postService) {
        this.postService = postService;
    }

    //end point for create(post): localhost:8080/api/v1/posts
    //end point for getList(get): localhost:8080/api/v1/posts
    //end point for get(get): localhost:8080/api/v1/posts/:id
    //end point for update(put): localhost:8080/api/v1/posts/:id
    //end point for delete(delete): localhost:8080/api/v1/posts/:id

    @PostMapping
    @AuthGuard
    // TODO: add validation
    public StatusRespDto create(@RequestParam("content") String content, @RequestParam("files") MultipartFile[] files, HttpSession session) throws IOException {
        return this.postService.create(content, files, session);
    }

    @GetMapping
    public List<PostRespDto> getList(){
        return this.postService.getList();
    }


    @GetMapping("{id}")
    public PostRespDto getDetail(@PathVariable Long id){
        return this.postService.getDetail(id);
    }

    @AuthGuard
    @PutMapping("{id}")
    public PostRespDto update(@PathVariable Long id, @Valid @RequestBody PostUpdateReqDto postReqDto, HttpSession session){
        return this.postService.update(id, postReqDto, session);
    }

    @AuthGuard
    @DeleteMapping("{id}")
    public StatusRespDto delete(@PathVariable Long id, HttpSession session){
        return this.postService.delete(id, session);
    }

}
