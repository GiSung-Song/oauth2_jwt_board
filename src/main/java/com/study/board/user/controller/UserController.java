package com.study.board.user.controller;

import com.study.board.user.dto.UserSignUpDto;
import com.study.board.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
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

            return e.getMessage();
        }

        return "회원가입 성공!";
    }

    @GetMapping("/member/list")
    public String member() {
        log.info("member");
        return "member";
    }

    @GetMapping("/manager/list")
    public String manager() {
        log.info("manager");
        return "manager";
    }

    @GetMapping("/admin/list")
    public String admin() {
        log.info("admin");
        return "admin";
    }
}
