package com.rangein.detective.ai_detective.repositories;

import com.rangein.detective.ai_detective.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, String> {


    @Query("""
    SELECT g FROM Game g
    WHERE g.user.userId = ?1
    """)
    List<Game> findGamesByUserId(String userId);


    @Query("""
    SELECT g FROM Game g
    LEFT JOIN FETCH g.messages
    WHERE g.gameId = ?1
    """)
    Optional<Game> findByIdWithMessageHistory(String gameId);

}
