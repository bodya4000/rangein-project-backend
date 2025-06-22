package com.rangein.detective.ai_detective.repositories.game;


import com.rangein.detective.ai_detective.dtos.game.MessageDto;
import com.rangein.detective.ai_detective.models.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface MessageRepository extends JpaRepository<Message, String>, CustomMessageRepository {


    @Query("""
    SELECT new com.rangein.detective.ai_detective.dtos.game.MessageDto(m.messageId, m.role,m.message) FROM Message m
    WHERE m.game.gameId = :gameId
        AND m.role != com.rangein.detective.ai_detective.enums.GameRole.RULE_MASTER
        AND m.role != com.rangein.detective.ai_detective.enums.GameRole.STORYTELLER
    ORDER BY m.timestamp
    """)
    Page<MessageDto> findPaginatedMessageHistoryByGameId(String gameId, Pageable pageable);

}
