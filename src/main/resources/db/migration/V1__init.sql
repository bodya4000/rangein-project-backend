CREATE TABLE games
(
    game_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255),
    title VARCHAR(255),
    story JSONB,
    CONSTRAINT pk_games PRIMARY KEY (game_id)
);

CREATE TABLE messages
(
    message_id VARCHAR(255) NOT NULL,
    role       SMALLINT,
    message    TEXT,
    timestamp  TIMESTAMP WITHOUT TIME ZONE,
    game_id    VARCHAR(255),
    CONSTRAINT pk_messages PRIMARY KEY (message_id)
);

CREATE TABLE users
(
    user_id VARCHAR(255) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (user_id)
);

ALTER TABLE games
    ADD CONSTRAINT FK_GAMES_ON_USER FOREIGN KEY (user_id) REFERENCES users (user_id);

ALTER TABLE messages
    ADD CONSTRAINT FK_MESSAGES_ON_GAME FOREIGN KEY (game_id) REFERENCES games (game_id);