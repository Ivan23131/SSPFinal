-- Удаляем таблицы, если они существуют
DROP TABLE IF EXISTS t_user_authority CASCADE;
DROP TABLE IF EXISTS t_tickets CASCADE;
DROP TABLE IF EXISTS t_events CASCADE;
DROP TABLE IF EXISTS t_authority CASCADE;
DROP TABLE IF EXISTS t_user CASCADE;

-- Создаем таблицу пользователей
CREATE TABLE t_user (
    id BIGSERIAL PRIMARY KEY, -- BIGSERIAL для автоматической генерации ID
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    c_balance INT NOT NULL,
    c_credit INT
);

-- Создаем таблицу ролей (authorities)
CREATE TABLE t_authority (
    id SERIAL PRIMARY KEY, -- SERIAL для автоматической генерации ID
    c_authority VARCHAR(50) NOT NULL
);

-- Создаем таблицу связи пользователей и ролей
CREATE TABLE t_user_authority (
    id_user BIGINT NOT NULL,
    id_authority INT NOT NULL,
    PRIMARY KEY (id_user, id_authority),
    FOREIGN KEY (id_user) REFERENCES t_user(id) ON DELETE CASCADE,
    FOREIGN KEY (id_authority) REFERENCES t_authority(id) ON DELETE CASCADE
);

-- Создаем таблицу событий
CREATE TABLE t_events (
    id SERIAL PRIMARY KEY, -- SERIAL для автоматической генерации ID
    c_title VARCHAR(50) NOT NULL,
    c_place VARCHAR(1000) NOT NULL,
    c_type VARCHAR(100) NOT NULL,
    c_date TIMESTAMP NOT NULL, -- TIMESTAMP вместо DATETIME
    c_organizer_id BIGINT NOT NULL,
    FOREIGN KEY (c_organizer_id) REFERENCES t_user(id) ON DELETE CASCADE
);

-- Создаем таблицу билетов
CREATE TABLE t_tickets (
    id SERIAL PRIMARY KEY, -- SERIAL для автоматической генерации ID
    event_id INT NOT NULL,
    c_status VARCHAR(50),
    c_row INT,
    c_place INT,
    c_price INT,
    c_user BIGINT,
    FOREIGN KEY (event_id) REFERENCES t_events(id) ON DELETE CASCADE,
    FOREIGN KEY (c_user) REFERENCES t_user(id) ON DELETE CASCADE
);