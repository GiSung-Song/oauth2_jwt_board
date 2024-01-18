package com.study.board.user.entity;

import com.study.board.post.entityy.Post;
import com.study.board.user.etc.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //DB에 ID값 지정 위임
    @Column(name = "user_id")
    private Long id; //primary key

    private String username; //이름
    private String email; //이메일
    private String password; //비밀번호

    @Column(unique = true)
    private String nickname; //별칭

    @Enumerated(EnumType.STRING)
    private Role role; //권한(member, manager, admin)

    private String provider; //소셜종류 ex) google, naver, kakao
    private String providerId; //소셜 ID의 고유번호(PK)

    private String refreshToken; //JWT 리프레시 토큰

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }

    //연관관계 매핑
    public void addPost(Post post) {
        posts.add(post);
        post.setUser(this);
    }
}
