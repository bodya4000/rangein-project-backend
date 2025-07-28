package com.rangein.detective.ai_detective.services;

import com.rangein.detective.ai_detective.dtos.controllers.user.UserResponseDto;
import com.rangein.detective.ai_detective.dtos.controllers.user.UserUpdateDto;
import com.rangein.detective.ai_detective.enums.Sex;
import com.rangein.detective.ai_detective.exceptions.auth.NoUserException;
import com.rangein.detective.ai_detective.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public UserResponseDto userInfo(String deviceId) {
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

    @Transactional
    public UserResponseDto updateUser(String deviceId, UserUpdateDto userUpdateDto) {
        var user = userRepository.findAllByIdWithGames(deviceId).orElseThrow(NoUserException::new);

        System.out.println(userUpdateDto);
        user.setUsername(userUpdateDto.username());
        user.setAge(userUpdateDto.age());
        user.setSex(Sex.fromCode(userUpdateDto.sex()));
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
