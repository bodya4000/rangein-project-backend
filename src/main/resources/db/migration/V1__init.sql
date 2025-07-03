-- Таблиця users
CREATE TABLE IF NOT EXISTS users (
                                     user_id VARCHAR(255) NOT NULL,
                                     CONSTRAINT pk_users PRIMARY KEY (user_id)
);

-- Таблиця games
CREATE TABLE IF NOT EXISTS games (
                                     game_id        VARCHAR(255) NOT NULL,
                                     user_id        VARCHAR(255),
                                     title          VARCHAR(255),
                                     story          JSONB,
                                     options        JSONB,
                                     game_state     JSONB,
                                     finished       bool,
                                     score          int,
                                     CONSTRAINT pk_games PRIMARY KEY (game_id)
);

-- Таблиця messages
CREATE TABLE IF NOT EXISTS messages (
                                    message_id VARCHAR(255) NOT NULL,
                                    role       SMALLINT,
                                    message    TEXT,
                                    timestamp  TIMESTAMP WITHOUT TIME ZONE,
                                    game_id    VARCHAR(255),
                                    CONSTRAINT pk_messages PRIMARY KEY (message_id)
);

-- Foreign key для games.user_id → users.user_id
DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1
            FROM information_schema.table_constraints
            WHERE constraint_name = 'fk_games_on_user'
        ) THEN
            ALTER TABLE games
                ADD CONSTRAINT fk_games_on_user FOREIGN KEY (user_id) REFERENCES users (user_id);
        END IF;
    END $$;

-- Foreign key для messages.game_id → games.game_id
DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1
            FROM information_schema.table_constraints
            WHERE constraint_name = 'fk_messages_on_game'
        ) THEN
            ALTER TABLE messages
                ADD CONSTRAINT fk_messages_on_game FOREIGN KEY (game_id) REFERENCES games (game_id);
        END IF;
    END $$;
