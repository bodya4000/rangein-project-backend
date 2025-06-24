package com.rangein.detective.ai_detective.repositories.game;

import com.rangein.detective.ai_detective.dtos.repositories.MessageWithIdDto;
import com.rangein.detective.ai_detective.enums.GameRole;
import com.rangein.detective.ai_detective.models.Game;

public interface CustomMessageRepository {

    MessageWithIdDto saveMessageToGame(String message, GameRole gameRole, Game game);
}
