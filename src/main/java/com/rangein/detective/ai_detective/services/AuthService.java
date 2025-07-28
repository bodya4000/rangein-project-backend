package com.rangein.detective.ai_detective.services;

import com.rangein.detective.ai_detective.contants.Colors;
import com.rangein.detective.ai_detective.dtos.controllers.user.UserResponseDto;
import com.rangein.detective.ai_detective.dtos.controllers.auth.RegisterDto;
import com.rangein.detective.ai_detective.exceptions.auth.NoUserException;
import com.rangein.detective.ai_detective.models.User;
import com.rangein.detective.ai_detective.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public UserResponseDto register(RegisterDto registerDto) {
        var avatarColorsList = new ArrayList<>(Colors.gradientUserAvatars.values());
        var avatarColors = avatarColorsList.get(new Random().nextInt(avatarColorsList.size()));
        User user = User.builder()
                .userId(registerDto.deviceId())
                .username(registerDto.username())
                .age(registerDto.age())
                .sex(registerDto.sex())
                .avatarColors(avatarColors)
                .build();
        user = userRepository.save(user);
        return new UserResponseDto(
                user.getUserId(),
                null,
                user.getUsername(),
                user.getSex().getCode(),
                user.getAge(),
                user.getAvatarColors()
        );
    }

    public UserResponseDto login(String deviceId) {
        var user = userRepository.findAllByIdWithGames(deviceId).orElseThrow(NoUserException::new);
        return new UserResponseDto(
                user.getUserId(),
                (user.getGame() == null || user.getGame().isEmpty()) ? null : user.getGame().getFirst().getGameId(),
                user.getUsername(),
                user.getSex().getCode(),
                user.getAge(),
                user.getAvatarColors()
        );
    }
}
