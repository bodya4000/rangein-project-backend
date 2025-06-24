package com.rangein.detective.ai_detective.models;

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


    @Column(columnDefinition = "jsonb")
    @Convert(converter = StringListConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private List<String> options;
}
