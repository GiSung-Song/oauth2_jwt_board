package com.study.board.post.entityy;

import com.study.board.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //생성자를 통해 값 변경 목적으로 접근하는 것을 차단
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 ID / PK

    private String subject; //제목

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; //글쓴이

    private String content; //내용

    @CreationTimestamp
    private LocalDateTime createTime; //글 등록 시간

    //연관관계 매핑
    public void setUser(User user) {
        this.user = user;
    }
}
