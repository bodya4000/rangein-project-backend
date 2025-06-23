package com.rangein.detective.ai_detective.models;

import com.rangein.detective.ai_detective.enums.Sex;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @Column(name = "user_id")
    private String userId; // deviceId by now

    private String username;

    @Enumerated(EnumType.ORDINAL)
    private Sex sex;

    private Integer age;

    @OneToMany(mappedBy = "user")
    private List<Game> game;
}

