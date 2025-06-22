package com.rangein.detective.ai_detective.repositories;

import com.rangein.detective.ai_detective.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

}
