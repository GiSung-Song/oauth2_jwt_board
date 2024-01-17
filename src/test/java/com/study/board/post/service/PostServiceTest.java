package com.study.board.post.service;

import com.study.board.post.dto.PostRegDto;
import com.study.board.post.repository.PostRepository;
import com.study.board.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.parameters.P;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Nested
    @DisplayName("게시글 목록 가져오기")
    class getPostList {

        @Test
        @WithMockCustomUser
        @DisplayName("성공 테스트")
        void success() throws Exception {

            PostRegDto postRegDto = new PostRegDto();
            postRegDto.setSubject("제목");
            postRegDto.setContent("내용");

            postService.regPost(postRegDto);
        }

    }



}