package com.rangein.detective.ai_detective.repositories.game;

import com.rangein.detective.ai_detective.dtos.game.MessageDto;
import com.rangein.detective.ai_detective.enums.GameRole;
import com.rangein.detective.ai_detective.models.Game;

public interface CustomMessageRepository {

    MessageDto saveMessageToGame(String message, GameRole gameRole, Game game);
}
