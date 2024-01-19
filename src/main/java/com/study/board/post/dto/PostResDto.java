package com.study.board.post.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PostResDto {

    private Long id; //게시글 번호
    private String subject; //게시글 제목
    private String content; //내용
    private LocalDateTime localDateTime; //게시글 작성 시각

}
