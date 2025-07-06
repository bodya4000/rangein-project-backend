package com.rangein.detective.ai_detective.models;

import com.rangein.detective.ai_detective.enums.GameRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;

@Entity
@Table(name = "messages")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"game"})
public class Message {

    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Id
    private String messageId;

    @Column(nullable = false)
    private String sender;

    private GameRole role;

    @Column(columnDefinition = "TEXT")
    private String message;

    private Instant timestamp = Instant.now();

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

}
