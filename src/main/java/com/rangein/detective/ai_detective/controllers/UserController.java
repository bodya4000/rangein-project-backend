package com.rangein.detective.ai_detective.controllers;

import com.rangein.detective.ai_detective.dtos.user.UserInfoResponseDto;
import com.rangein.detective.ai_detective.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/{deviceId}")
    public ResponseEntity<UserInfoResponseDto> getUserInfo(@PathVariable String deviceId) {
        UserInfoResponseDto userInfo = userService.userInfo(deviceId);
        return ResponseEntity.ok(userInfo);
    }
}
