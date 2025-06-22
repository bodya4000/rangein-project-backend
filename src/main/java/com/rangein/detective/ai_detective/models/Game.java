package com.rangein.detective.ai_detective.models;

import com.rangein.detective.ai_detective.dtos.game.MessageDto;
import com.rangein.detective.ai_detective.enums.GameRole;
import com.rangein.detective.ai_detective.utils.StringListConverter;
import org.hibernate.annotations.ColumnTransformer;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;


import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.Type;


@Entity
@Table(name = "games")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String gameId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new java.util.ArrayList<>();

    private String title;

    @Column(columnDefinition = "jsonb")
    @Convert(converter = StringListConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private List<String> story;

    public String addMessageToHistory(String messageBody, GameRole role) {
        var messageObj = Message.builder()
                .message(messageBody)
                .role(role)
                .game(this)
                .build();
        messages.add(messageObj);
        return messageObj.getMessageId();
    }

    public List<MessageDto> getMessageContext() {
        return messages.stream()
                .filter(message -> message.getRole() != GameRole.RULE_MASTER && message.getRole() != GameRole.STORYTELLER)
                .map(message -> new MessageDto(message.getMessageId(), message.getRole(), message.getMessage()))
                .toList();
    }
}
