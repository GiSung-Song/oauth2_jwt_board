package com.study.board.post.controller;

import com.study.board.post.dto.PostRegDto;
import com.study.board.post.dto.PostResDto;
import com.study.board.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/post")
    public String regPost(@RequestBody PostRegDto postRegDto) {
        try {
            postService.regPost(postRegDto);
        } catch (Exception e) {
            return e.getMessage();
        }

        return "등록되었습니다";
    }
}
