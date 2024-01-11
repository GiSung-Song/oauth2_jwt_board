package com.study.board.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSignUpDto {

    private String email; //이메일
    private String password; //비밀번호
    private String nickname; //별칭
}
