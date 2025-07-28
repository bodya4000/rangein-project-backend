package com.rangein.detective.ai_detective.repositories;

import com.rangein.detective.ai_detective.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    @Query(
            """
            select u from User u
            left join fetch u.game g
            where u.userId =:id
            """
    )
    Optional<User> findAllByIdWithGames(String id);
}
