package com.rangein.detective.ai_detective.repositories.game;

import com.rangein.detective.ai_detective.dtos.repositories.MessageDto;
import com.rangein.detective.ai_detective.dtos.repositories.MessageWithIdDto;
import com.rangein.detective.ai_detective.enums.GameRole;
import com.rangein.detective.ai_detective.models.Game;
import com.rangein.detective.ai_detective.models.Message;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class CustomMessageRepositoryImpl implements CustomMessageRepository {

    private final EntityManager entityManager;

    @Override
    @Transactional
    public MessageWithIdDto saveMessageToGame(String message, String sender, Game game) {
        var messageEntity = Message.builder()
                .message(message)
                .sender(sender)
                .timestamp(Instant.now())
                .game(game)
                .build();
        entityManager.persist(messageEntity);
        entityManager.flush();
        return new MessageWithIdDto(messageEntity.getMessageId(), messageEntity.getSender(), messageEntity.getMessage());
    }
}
