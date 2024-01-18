package com.study.board.post.service;

import com.study.board.post.dto.PostRegDto;
import com.study.board.post.dto.PostResDto;
import com.study.board.post.entityy.Post;
import com.study.board.post.repository.PostRepository;
import com.study.board.user.etc.Role;
import com.study.board.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    //Authentication 설정
    @BeforeEach
    void setUp() {
        com.study.board.user.entity.User user = com.study.board.user.entity.User.builder()
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
    @DisplayName("저장 케이스")
    void save() throws Exception {

        //given
        Post post = Post.builder()
                .subject("제목")
                .content("내용")
                .build();

        PostRegDto postRegDto = new PostRegDto();
        postRegDto.setSubject("제목");
        postRegDto.setContent("내용");

        given(postRepository.save(any())).willReturn(post);

        //when
        postService.regPost(postRegDto);

        //then
        verify(postRepository, Mockito.times(1)).save(any());
    }

    @Test
    @DisplayName("리스트 가져오기")
    void getAll() {
        List<Post> post = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            post.add(new Post(0L, "test", null, "test", null));
        }

        doReturn(post).when(postRepository)
                .findAll();

        //when
        final List<PostResDto> postList = postService.getAllPost();

        Assertions.assertThat(postList.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("수정 케이스")
    void editPost() throws Exception {
        Post post = new Post(0L, "수정전", null, "수정전", null);
        PostRegDto postRegDto = new PostRegDto();
        postRegDto.setContent("수정후");
        postRegDto.setSubject("수정후");

        doReturn(post).when(postRepository)
                .save(post);

        postService.modifyPost(0L, postRegDto);

        doReturn(post).when(postRepository)
                        .findById(0L);

        Assertions.assertThat(post.getSubject()).isEqualTo("수정후");
    }

}