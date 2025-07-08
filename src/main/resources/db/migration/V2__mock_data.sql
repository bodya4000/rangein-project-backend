ALTER TABLE users
    ADD COLUMN username VARCHAR(255),
    ADD COLUMN sex SMALLINT,
    ADD COLUMN age INT,
    ADD COLUMN avatar_colors JSONB;