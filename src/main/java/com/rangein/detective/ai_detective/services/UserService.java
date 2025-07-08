package com.rangein.detective.ai_detective.services;

import com.rangein.detective.ai_detective.dtos.controllers.UserResponseDto;
import com.rangein.detective.ai_detective.exceptions.auth.NoUserException;
import com.rangein.detective.ai_detective.models.User;
import com.rangein.detective.ai_detective.repositories.UserRepository;
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
                user.getSex(),
                user.getAge()
        );
    }


}
