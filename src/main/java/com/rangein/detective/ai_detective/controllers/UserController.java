package com.rangein.detective.ai_detective.controllers;

import com.rangein.detective.ai_detective.dtos.controllers.user.UserResponseDto;
import com.rangein.detective.ai_detective.dtos.controllers.user.UserUpdateDto;
import com.rangein.detective.ai_detective.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/{deviceId}")
    public ResponseEntity<UserResponseDto> getUserInfo(@PathVariable String deviceId) {
        UserResponseDto userInfo = userService.userInfo(deviceId);
        return ResponseEntity.ok(userInfo);
    }

    @PatchMapping("/{deviceId}")
    public ResponseEntity<UserResponseDto> update(@PathVariable String deviceId, @RequestBody UserUpdateDto userUpdateDto) {
        UserResponseDto userInfo = userService.updateUser(deviceId, userUpdateDto);
        return ResponseEntity.ok(userInfo);
    }
}
