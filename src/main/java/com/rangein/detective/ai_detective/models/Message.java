package com.rangein.detective.ai_detective.models;

import com.rangein.detective.ai_detective.dtos.game.AiMessageDto;
import com.rangein.detective.ai_detective.dtos.game.MessageDto;
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
public class Message {

    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Id
    private String messageId;

    private GameRole role;

    @Column(columnDefinition = "TEXT")
    private String message;

    private Instant timestamp = Instant.now();

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;


    public MessageDto toMessageDto() {
        return new MessageDto(messageId, role, message);
    }

    public AiMessageDto toAiMessageDto() {
        return new AiMessageDto(role, message);
    }

}
