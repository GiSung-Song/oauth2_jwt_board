package com.study.board.user.controller;

import com.study.board.user.dto.UserSignUpDto;
import com.study.board.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public String signUp(@RequestBody UserSignUpDto userSignUpDto){
        try {
            userService.signUp(userSignUpDto);
        } catch (Exception e) {
            log.info("회원가입 실패");
            e.printStackTrace();
        }

        return "회원가입";
    }
}
