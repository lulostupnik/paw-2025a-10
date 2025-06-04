INSERT INTO images(id, content) VALUES(1, 'ffffffff');
INSERT INTO images(id, content) VALUES(2, 'ffffffffffffffff');

INSERT INTO category(id, name) VALUES(1, 'interest 1');
INSERT INTO category(id, name) VALUES(2, 'interest 2');
INSERT INTO category(id, name) VALUES(3, 'interest 3');

INSERT INTO countries(id, name, code) VALUES(1, 'cuntry',  'aa');
INSERT INTO countries(id, name, code) VALUES(2, 'cuntry2', 'bb');

INSERT INTO cities(id, name, country_id, deleted) VALUES(1, 'city1',        1, FALSE);
INSERT INTO cities(id, name, country_id, deleted) VALUES(2, 'city2',        1, FALSE);
INSERT INTO cities(id, name, country_id, deleted) VALUES(3, 'city3',        2, FALSE);
INSERT INTO cities(id, name, country_id, deleted) VALUES(4, 'deleted city', 2, TRUE);

INSERT INTO universities(id, name, abbreviation, city_id, deleted) VALUES(1, 'Instituto de muy largo', 'ITBA',  1, FALSE);
INSERT INTO universities(id, name, abbreviation, city_id, deleted) VALUES(2, 'Universidad de muy largo', 'UBA', 2, FALSE);
INSERT INTO universities(id, name, abbreviation, city_id, deleted) VALUES(3, 'Another one', 'MAS',              2, FALSE);
INSERT INTO universities(id, name, abbreviation, city_id, deleted) VALUES(4, 'Deleted uni', 'DEL',              1, TRUE);

INSERT INTO careers(id, name, deleted) VALUES(1, 'career 1', FALSE);
INSERT INTO careers(id, name, deleted) VALUES(2, 'career 2', FALSE);
INSERT INTO careers(id, name, deleted) VALUES(3, 'deleted', TRUE);

INSERT INTO users(id, email, username, firstname, lastname, password, language, university, career_id, profile_picture_id, roles, blocked)            VALUES(1, 'user1@mail.com', 'user1',              'user', 'user', 'superSecret', 'es', 1, 1, 1, 'USER', FALSE);
INSERT INTO users(id, email, username, firstname, lastname, password, language, university, career_id, profile_picture_id, roles, blocked)            VALUES(2, 'user2@mail.com', 'user2',              'user', 'user', 'superSecret', 'es', 1, 1, 1, 'USER', FALSE);
INSERT INTO users(id, email, username, firstname, lastname, password, language, university, career_id, profile_picture_id, roles, blocked)            VALUES(3, 'user3@mail.com', 'user3',              'user', 'user', 'superSecret', 'es', 1, 1, 1, 'USER', FALSE);
INSERT INTO users(id, email, username, firstname, lastname, password, language, university, career_id, profile_picture_id, roles, blocked, validated) VALUES(4, 'user4@mail.com', 'user4',              'user', 'user', 'superSecret', 'es', 2, 1, 1, 'USER', FALSE, FALSE);
INSERT INTO users(id, username, email, firstname, lastname, password, language, university, career_id, profile_picture_id, roles, blocked)            VALUES(5, '1interest',      '1interest@mail.com', 'user', 'user', 'superSecret', 'en', 1, 1, 1, 'USER', FALSE);
INSERT INTO users(id, username, email, firstname, lastname, password, language, university, career_id, profile_picture_id, roles, blocked)            VALUES(6, '2interest',      '2interest@mail.com', 'user', 'user', 'superSecret', 'en', 1, 1, 1, 'USER', FALSE);
INSERT INTO users(id, username, email, firstname, lastname, password, language, university, career_id, profile_picture_id, roles, blocked)            VALUES(7, '3interest',      '3interest@mail.com', 'user', 'user', 'superSecret', 'en', 1, 1, 1, 'USER', FALSE);

INSERT INTO user_interest(user_id, category_id, score) VALUES(1, 1, 4);
INSERT INTO user_interest(user_id, category_id, score) VALUES(1, 2, 2);
INSERT INTO user_interest(user_id, category_id, score) VALUES(1, 3, 1);
INSERT INTO user_interest(user_id, category_id, score) VALUES(5, 1, 1);
INSERT INTO user_interest(user_id, category_id, score) VALUES(6, 2, 1);
INSERT INTO user_interest(user_id, category_id, score) VALUES(6, 1, 1);
INSERT INTO user_interest(user_id, category_id, score) VALUES(7, 1, 1);
INSERT INTO user_interest(user_id, category_id, score) VALUES(7, 2, 1);
INSERT INTO user_interest(user_id, category_id, score) VALUES(7, 3, 1);

INSERT INTO journeys(id, user_id, destination_university_id, start_date, end_date, description, deleted) VALUES(1, 1, 2, CURRENT_DATE + INTERVAL '7' DAY, CURRENT_DATE + INTERVAL '7' DAY + INTERVAL '1' MONTH, 'Cool journey', FALSE);
INSERT INTO journeys(id, user_id, destination_university_id, start_date, end_date, description, deleted) VALUES(2, 2, 2, CURRENT_DATE + INTERVAL '7' DAY, CURRENT_DATE + INTERVAL '7' DAY + INTERVAL '1' MONTH, 'Cool journey', FALSE);
INSERT INTO journeys(id, user_id, destination_university_id, start_date, end_date, description, deleted) VALUES(3, 4, 1, CURRENT_DATE,                    CURRENT_DATE,                                         'deleted',      TRUE);

INSERT INTO journey_responses(id, user_id, journey_id, message, date_time, deleted) VALUES(1, 1, 1, 'COOL!', CURRENT_DATE + INTERVAL '0' HOUR, FALSE);
INSERT INTO journey_responses(id, user_id, journey_id, message, date_time, deleted) VALUES(2, 2, 1, 'COOL!', CURRENT_DATE + INTERVAL '1' HOUR, FALSE);
INSERT INTO journey_responses(id, user_id, journey_id, message, date_time, deleted) VALUES(3, 3, 1, 'COOL!', CURRENT_DATE + INTERVAL '2' HOUR, FALSE);
INSERT INTO journey_responses(id, user_id, journey_id, message, date_time, deleted) VALUES(4, 3, 1, 'COOL!', CURRENT_DATE + INTERVAL '0' HOUR, TRUE);

INSERT INTO events(id, user_id, city_id, event_date, event_time, address, attendees_limit, attendees_count, description, title, flyer_image_id, deleted) VALUES(1, 1, 1, CURRENT_DATE + INTERVAL '7' DAY,   TIME '00:00:00', 'cool place', 30,   3, 'cool event', 'warm event',     1, FALSE);
INSERT INTO events(id, user_id, city_id, event_date, event_time, address, attendees_limit, attendees_count, description, title, flyer_image_id, deleted) VALUES(2, 2, 1, CURRENT_DATE + INTERVAL '7' DAY,   TIME '00:00:00', 'cool place', NULL, 1, 'cool event', 'another event',  1, FALSE);
INSERT INTO events(id, user_id, city_id, event_date, event_time, address, attendees_limit, attendees_count, description, title, flyer_image_id, deleted) VALUES(3, 2, 1, CURRENT_DATE + INTERVAL '7' DAY,   TIME '00:00:00', 'cool place', 30,   0, 'cool event', 'one more event', 1, FALSE);
INSERT INTO events(id, user_id, city_id, event_date, event_time, address, attendees_limit, attendees_count, description, title, flyer_image_id, deleted) VALUES(4, 1, 1, CURRENT_DATE - INTERVAL '100' DAY, TIME '00:00:00', 'cool place', 30,   0, 'cool event', 'older event',    1, FALSE);
INSERT INTO events(id, user_id, city_id, event_date, event_time, address, attendees_limit, attendees_count, description, title, flyer_image_id, deleted) VALUES(5, 2, 1, CURRENT_DATE + INTERVAL '7' DAY,   NULL,            'cool place', 30,   0, 'cool event', 'deleted',        1, TRUE);

INSERT INTO event_attendances(user_id, event_id) VALUES(1, 1);
INSERT INTO event_attendances(user_id, event_id) VALUES(2, 1);
INSERT INTO event_attendances(user_id, event_id) VALUES(3, 1);
INSERT INTO event_attendances(user_id, event_id) VALUES(1, 2);
INSERT INTO event_attendances(user_id, event_id) VALUES(2, 4);


INSERT INTO event_responses(id, user_id, event_id, message, date_time, deleted) VALUES(1, 1, 1, 'COOL!', CURRENT_DATE + INTERVAL '0' HOUR, FALSE);
INSERT INTO event_responses(id, user_id, event_id, message, date_time, deleted) VALUES(2, 1, 1, 'COOL!', CURRENT_DATE + INTERVAL '0' HOUR, FALSE);
INSERT INTO event_responses(id, user_id, event_id, message, date_time, deleted) VALUES(3, 1, 1, 'COOL!', CURRENT_DATE + INTERVAL '0' HOUR, FALSE);
INSERT INTO event_responses(id, user_id, event_id, message, date_time, deleted) VALUES(4, 1, 1, 'COOL!', CURRENT_DATE + INTERVAL '0' HOUR, TRUE);

INSERT INTO ratings(id, user_id, event_id, rating, created_at, updated_at) VALUES(1, 1, 1, 5, CURRENT_DATE, CURRENT_DATE);
INSERT INTO ratings(id, user_id, event_id, rating, created_at, updated_at) VALUES(2, 2, 1, 4, CURRENT_DATE, CURRENT_DATE);
INSERT INTO ratings(id, user_id, event_id, rating, created_at, updated_at) VALUES(3, 1, 2, 3, CURRENT_DATE, CURRENT_DATE);
INSERT INTO ratings(id, user_id, event_id, rating, created_at, updated_at) VALUES(4, 2, 2, 2, CURRENT_DATE, CURRENT_DATE);
INSERT INTO ratings(id, user_id, event_id, rating, created_at, updated_at) VALUES(5, 2, 3, 1, CURRENT_DATE, CURRENT_DATE);

INSERT INTO tokens(id, user_id, token, token_expiration) VALUES(1, 1, 'asdf', CURRENT_TIMESTAMP + INTERVAL '1' DAY);
INSERT INTO tokens(id, user_id, token, token_expiration) VALUES(2, 2, 'zxcv', CURRENT_TIMESTAMP + INTERVAL '1' DAY);
INSERT INTO tokens(id, user_id, token, token_expiration) VALUES(3, 3, 'qwer', CURRENT_TIMESTAMP + INTERVAL '1' DAY);
INSERT INTO tokens(id, user_id, token, token_expiration) VALUES(4, 4, 'tyui', CURRENT_TIMESTAMP - INTERVAL '7' DAY);

INSERT INTO reports(id, reported_user_id, reporting_user_id, journey_id, event_id, event_response_id, journey_response_id, description, reason, deleted)         VALUES(1, 3, 1, null, null, null, null, 'illegal',        'HARASSMENT', FALSE);
INSERT INTO reports(id, reported_user_id, reporting_user_id, journey_id, event_id, event_response_id, journey_response_id, description, reason, deleted)         VALUES(2, 4, 1, 3,    null, null, null, 'illegaljourney', 'HARASSMENT', FALSE);
INSERT INTO reports(id, reported_user_id, reporting_user_id, journey_id, event_id, event_response_id, journey_response_id, description, reason, deleted)         VALUES(3, 2, 1, null, 3,    null, null, 'illegalevent',   'HARASSMENT', FALSE);
INSERT INTO reports(id, reported_user_id, reporting_user_id, journey_id, event_id, event_response_id, journey_response_id, description, reason, deleted, status) VALUES(4, 3, 1, null, null, null, null, 'illegal',        'HARASSMENT', FALSE, 'UNDER_REVIEW');
INSERT INTO reports(id, reported_user_id, reporting_user_id, journey_id, event_id, event_response_id, journey_response_id, description, reason, deleted, status) VALUES(5, 3, 1, null, null, null, null, 'illegal',        'HARASSMENT', FALSE, 'RESOLVED');
INSERT INTO reports(id, reported_user_id, reporting_user_id, journey_id, event_id, event_response_id, journey_response_id, description, reason, deleted, status) VALUES(6, 3, 1, null, null, null, null, 'illegal',        'HARASSMENT', FALSE, 'DISMISSED');
INSERT INTO reports(id, reported_user_id, reporting_user_id, journey_id, event_id, event_response_id, journey_response_id, description, reason, deleted, status) VALUES(7, 3, 1, null, null, null, null, 'illegal',        'HARASSMENT', TRUE, 'DISMISSED');
