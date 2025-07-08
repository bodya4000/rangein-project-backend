package com.rangein.detective.ai_detective.controllers;

import com.rangein.detective.ai_detective.dtos.controllers.user.UserResponseDto;
import com.rangein.detective.ai_detective.dtos.controllers.auth.RegisterDto;
import com.rangein.detective.ai_detective.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> getUserInfo(@RequestBody RegisterDto registerDto) {
        UserResponseDto userInfo = authService.register(registerDto);
        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/login/{deviceId}")
    public ResponseEntity<UserResponseDto> getUserInfo(@PathVariable String deviceId) {
        UserResponseDto userInfo = authService.login(deviceId);
        return ResponseEntity.ok(userInfo);
    }
}
