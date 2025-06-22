package com.rangein.detective.ai_detective.repositories.game;


import com.rangein.detective.ai_detective.dtos.game.AiMessageDto;
import com.rangein.detective.ai_detective.dtos.game.MessageDto;
import com.rangein.detective.ai_detective.models.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface MessageRepository extends JpaRepository<Message, String>, CustomMessageRepository {


    @Query("""
    SELECT new com.rangein.detective.ai_detective.dtos.game.MessageDto(m.messageId, m.role,m.message) FROM Message m
    WHERE m.game.gameId = :gameId
        AND m.role != com.rangein.detective.ai_detective.enums.GameRole.SYSTEM_BACKEND
        AND m.role != com.rangein.detective.ai_detective.enums.GameRole.STORYTELLER_AI
    ORDER BY m.timestamp
    """)
    Page<MessageDto> findPaginatedMessageHistoryByGameId(String gameId, Pageable pageable);

    @Query("""
    SELECT new com.rangein.detective.ai_detective.dtos.game.AiMessageDto(m.role,m.message) FROM Message m
    WHERE m.game.gameId = :gameId
    ORDER BY m.timestamp
    """)
    List<AiMessageDto> findAllMappedToAiMessageDto(String gameId);

}
