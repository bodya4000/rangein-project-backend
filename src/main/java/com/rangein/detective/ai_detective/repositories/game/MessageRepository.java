package com.rangein.detective.ai_detective.repositories.game;


import com.rangein.detective.ai_detective.dtos.repositories.MessageWithIdDto;
import com.rangein.detective.ai_detective.dtos.repositories.MessageDto;
import com.rangein.detective.ai_detective.models.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface MessageRepository extends JpaRepository<Message, String>, CustomMessageRepository {


    @Query("""
    SELECT new com.rangein.detective.ai_detective.dtos.repositories.MessageWithIdDto(m.messageId, m.role,m.message) FROM Message m
    WHERE m.game.gameId = :gameId
        AND m.role != com.rangein.detective.ai_detective.enums.GameRole.SYSTEM_BACKEND
        AND m.role != com.rangein.detective.ai_detective.enums.GameRole.STORYTELLER_AI
    ORDER BY m.timestamp DESC
    """)
    Page<MessageWithIdDto> findPaginatedMessageHistoryByGameId(String gameId, Pageable pageable);

    @Query("""
    SELECT new com.rangein.detective.ai_detective.dtos.repositories.MessageDto(m.role,m.message) FROM Message m
    WHERE m.game.gameId = :gameId
    ORDER BY m.timestamp
    """)
    List<MessageDto> findAllMessages(String gameId);

}
