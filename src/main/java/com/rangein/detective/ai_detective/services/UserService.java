package com.rangein.detective.ai_detective.services;

import com.rangein.detective.ai_detective.dtos.controllers.UserResponseDto;
import com.rangein.detective.ai_detective.models.User;
import com.rangein.detective.ai_detective.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public UserResponseDto userInfo(String deviceId) {
        var user = userRepository.findAllByIdWithGames(deviceId).orElse(null);
        if (user == null) {
            user = User.builder().userId(deviceId).build();
            user = userRepository.save(user); // create a new user if not found by now
        }

        return new UserResponseDto(
                user.getUserId(),
                (user.getGame() == null || user.getGame().isEmpty()) ? null : user.getGame().getFirst().getGameId(),
                user.getUsername(),
                user.getSex(),
                user.getAge()
        );
    }


}
