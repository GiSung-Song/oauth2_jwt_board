package com.study.board.post.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResDto {

    private Long id; //게시글 번호
    private String subject; //게시글 제목
    private String content; //내용
    private LocalDateTime localDateTime; //게시글 작성 시각

}
