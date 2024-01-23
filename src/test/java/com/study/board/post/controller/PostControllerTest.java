package com.study.board.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.board.post.dto.PostRegDto;
import com.study.board.post.dto.PostResDto;
import com.study.board.post.service.PostService;
import com.study.board.user.etc.Role;
import com.study.board.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostService postService;

    @Mock
    private UserRepository userRepository;

    private com.study.board.user.entity.User user;

    //Authentication 설정
    @BeforeEach
    void setUp() {
        user = com.study.board.user.entity.User.builder()
                .email("test@test.com")
                .password("password")
                .role(Role.MEMBER)
                .username("test")
                .id(1L)
                .nickname("nickname")
                .build();

        lenient().when(userRepository.save(any(com.study.board.user.entity.User.class))).thenReturn(user);
        lenient().when(userRepository.findByEmail(any())).thenReturn(Optional.ofNullable(user));

        UserDetails userDetails = User.builder()
                .username("test@test.com")
                .password("password")
                .roles(Role.MEMBER.name())
                .build();

        Authentication authentication = Mockito.mock(Authentication.class);
        lenient().when(authentication.getPrincipal()).thenReturn(userDetails);
        lenient().when(authentication.isAuthenticated()).thenReturn(true);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("게시글 목록 조회 테스트")
    void getAllPost() throws Exception {

        List<PostResDto> list = new ArrayList<>();
        list.add(new PostResDto(0L, "제목1", "내용1", null));
        list.add(new PostResDto(0L, "제목1", "내용1", null));

        given(postService.getAllPost()).willReturn(list);

        mockMvc.perform(MockMvcRequestBuilders.get("/post").secure(true).with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 등록 테스트")
    void regPost() throws Exception {
        PostRegDto dto = new PostRegDto();
        dto.setSubject("제목");
        dto.setContent("내용");

        given(postService.regPost(dto)).willReturn(0L);

        mockMvc.perform(MockMvcRequestBuilders.post("/post")
                        .secure(true)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(content().string("0번으로 등록되었습니다"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 조회 테스트")
    void getPost() throws Exception {
        PostResDto postResDto = new PostResDto();
        postResDto.setId(0L);
        postResDto.setContent("내용");
        postResDto.setSubject("제목");

        given(postService.getPost(0L)).willReturn(postResDto);

        String result = new ObjectMapper().writeValueAsString(postResDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/post/0")
                        .secure(true)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(result));
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    void editPost() throws Exception {
        PostRegDto dto2 = new PostRegDto();
        dto2.setSubject("제목2");
        dto2.setContent("내용2");

//        doNothing().when(postService).modifyPost(0L, dto2);

        mockMvc.perform(MockMvcRequestBuilders.patch("/post/0")
                        .secure(true)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto2)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    void deletePost() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/post/{id}", 0L)
                        .secure(true)
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}