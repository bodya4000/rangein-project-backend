package com.rangein.detective.ai_detective.repositories;

import com.rangein.detective.ai_detective.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, String> {

    @Query("""
    SELECT g FROM Game g
    LEFT JOIN FETCH g.user
    WHERE g.gameId = ?1
""")
    Optional<Game> findByIdWithUser(String gameId);



}
