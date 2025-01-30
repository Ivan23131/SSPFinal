-- Добавляем пользователей
INSERT INTO t_user (username, password_hash, c_balance, c_credit) VALUES
('ivan', '{noop}1', 1000, 0),
('dima', '{noop}2', 2000, 1000),
('liza', '{noop}3', 0, 0);

-- Добавляем роли
INSERT INTO t_authority (c_authority) VALUES
('client'),
('superclient'),
('organizer');

-- Связываем пользователей с ролями
INSERT INTO t_user_authority (id_user, id_authority) VALUES
(1, 1),
(2, 1),
(2, 2),
(3, 3);

-- Добавляем события
INSERT INTO t_events (c_title, c_place, c_type, c_date, c_organizer_id) VALUES
('Концерт', 'Москва, Кремль', 'Музыка', '2025-12-25 19:00:00', 3),
('Выставка', 'Санкт-Петербург, Эрмитаж', 'Искусство', '2025-11-15 10:00:00', 3),
('Кинофестиваль', 'Казань, Кремль', 'Кино', '2025-10-10 18:00:00', 3);

-- Добавляем билеты
INSERT INTO t_tickets (event_id, c_status, c_row, c_place, c_price, c_user) VALUES
(1, 'продается', 1, 10, 1000, NULL),
(1, 'продается', 1, 11, 1000, NULL),
(1, 'продается', 1, 12, 1000, NULL),
(1, 'продается', 1, 13, 1000, NULL),
(1, 'продается', 1, 14, 1000, NULL),
(1, 'продан', 2, 5, 1500, 1),
(2, 'забронирован', 3, 7, 500, 2),
(3, 'забронирован', 1, 1, 2000, 2);