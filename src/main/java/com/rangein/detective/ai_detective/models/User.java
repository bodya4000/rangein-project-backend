package com.rangein.detective.ai_detective.models;

import com.rangein.detective.ai_detective.enums.Sex;
import com.rangein.detective.ai_detective.utils.StringListConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;

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

    @Column(columnDefinition = "jsonb")
    @Convert(converter = StringListConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private List<String> avatarColors;
}

