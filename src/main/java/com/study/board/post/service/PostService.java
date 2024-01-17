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

        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userEmail = principal.getUsername();

        log.info("userEmail : {}", userEmail);

        User userEntity = userRepository.findByEmail(userEmail).orElseThrow(() -> new Exception("회원의 정보가 없습니다."));

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

        return postList.stream().map(post -> toEntity(post)).collect(Collectors.toList());
    }

    public PostResDto toEntity(Post post) {
        return PostResDto.builder()
                .id(post.getId())
                .subject(post.getSubject())
                .localDateTime(post.getCreateTime())
                .build();
    }
}
