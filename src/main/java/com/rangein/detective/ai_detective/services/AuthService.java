package com.rangein.detective.ai_detective.services;

import com.rangein.detective.ai_detective.dtos.controllers.UserResponseDto;
import com.rangein.detective.ai_detective.dtos.controllers.auth.RegisterDto;
import com.rangein.detective.ai_detective.enums.Sex;
import com.rangein.detective.ai_detective.exceptions.auth.NoUserException;
import com.rangein.detective.ai_detective.models.User;
import com.rangein.detective.ai_detective.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public UserResponseDto register(RegisterDto registerDto) {
        User user = User.builder()
                .userId(registerDto.deviceId())
                .username(registerDto.username())
                .age(registerDto.age())
                .sex(registerDto.sex())
                .build();
        user = userRepository.save(user);
        return new UserResponseDto(
                user.getUserId(),
                null,
                user.getUsername(),
                user.getSex(),
                user.getAge()
        );
    }

    public UserResponseDto login(String deviceId) {
        var user = userRepository.findAllByIdWithGames(deviceId).orElseThrow(NoUserException::new);
        return new UserResponseDto(
                user.getUserId(),
                (user.getGame() == null || user.getGame().isEmpty()) ? null : user.getGame().getFirst().getGameId(),
                user.getUsername(),
                user.getSex(),
                user.getAge()
        );
    }
}
