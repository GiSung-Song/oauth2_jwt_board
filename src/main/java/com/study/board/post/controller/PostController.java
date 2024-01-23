package com.study.board.post.controller;

import com.study.board.post.dto.PostRegDto;
import com.study.board.post.dto.PostResDto;
import com.study.board.post.service.PostService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    @GetMapping("/post")
    public List<PostResDto> getAllPost() {
        return postService.getAllPost();
    }

//    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER', 'MANAGER')")
    @PostMapping("/post")
    public String regPost(@RequestBody PostRegDto postRegDto) {
        try {
            Long postId = postService.regPost(postRegDto);

            return postId + "번으로 등록되었습니다";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

//    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER', 'MANAGER')")
    @GetMapping("/post/{id}")
    public PostResDto getPost(@PathVariable("id") Long postId, HttpServletResponse response) throws IOException {
        try {
            PostResDto post = postService.getPost(postId);

            return post;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(e.getMessage());

            throw new RuntimeException(e);
        }

    }

//    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER', 'MANAGER')")
    @PatchMapping("/post/{id}")
    public void editPost(@PathVariable("id") Long postId, @RequestBody PostRegDto postRegDto) throws Exception {
        postService.modifyPost(postId, postRegDto);
    }

//    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER', 'MANAGER')")
    @DeleteMapping("/post/{id}")
    public void deletePost(@PathVariable("id") Long postId) throws Exception {
        postService.deletePost(postId);
    }
}
