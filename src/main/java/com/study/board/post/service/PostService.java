package com.study.board.post.service;

import com.study.board.post.dto.PostRegDto;
import com.study.board.post.dto.PostResDto;
import com.study.board.post.entityy.Post;
import com.study.board.post.repository.PostRepository;
import com.study.board.user.entity.User;
import com.study.board.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long regPost(PostRegDto dto) throws Exception {

        //제목 미 입력 시
        if (dto.getSubject() == null || dto.getSubject().isEmpty()) {
            throw new Exception("제목은 필수 입력 값 입니다.");
        }

        //내용 미 입력 시
        if (dto.getContent() == null || dto.getContent().isEmpty()) {
            throw new Exception("내용은 필수 입력 값 입니다.");
        }


        User userEntity = getAuthenticationUser();

        Post post = Post.builder()
                .subject(dto.getSubject())
                .content(dto.getContent())
                .user(userEntity)
                .build();

        userEntity.addPost(post);

        return postRepository.save(post).getId();
    }

    @Transactional(readOnly = true)
    public List<PostResDto> getAllPost() {
        List<Post> postList = postRepository.findAll();

        return postList.stream().map(post -> toDto(post)).collect(Collectors.toList());
    }

    @Transactional
    public void modifyPost(Long postId, PostRegDto postRegDto) throws Exception {
        //해당 id의 게시글 존재하는지 판단
        Post post = postRepository.findById(postId).orElseThrow(() -> new Exception("해당 게시글이 존재하지 않습니다."));

        // 해당 게시글이 사용자의 게시글이 맞는지 판단
        // 1. 인증된 회원 Entity
        User userEntity = getAuthenticationUser();

        // 2. 회원과 게시글 작성자의 ID 비교하여 다르면 throw
        if (userEntity.getId() != post.getUser().getId()) {
            throw new Exception("해당 게시글의 작성자가 아닙니다.");
        }

        // 3. 제목과 내용 변경
        post.editPost(postRegDto);
    }

    public PostResDto toDto(Post post) {
        return PostResDto.builder()
                .id(post.getId())
                .subject(post.getSubject())
                .localDateTime(post.getCreateTime())
                .build();
    }

    private User getAuthenticationUser() throws Exception {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userEmail = principal.getUsername();

        User userEntity = userRepository.findByEmail(userEmail).orElseThrow(() -> new Exception("회원의 정보가 없습니다."));

        return userEntity;
    }
}
