INSERT INTO careers(id, name, deleted) VALUES(10000, 'career 1', FALSE);
INSERT INTO careers(id, name, deleted) VALUES(20000, 'career 2', FALSE);
INSERT INTO careers(id, name, deleted) VALUES(30000, 'deleted', TRUE);

INSERT INTO countries(id, name, code) VALUES(10000, 'cuntry', 'aa');
INSERT INTO countries(id, name, code) VALUES(20000, 'cuntry2', 'bb');

INSERT INTO cities(id, name, country_id, deleted) VALUES(10000, 'city1', 10000, FALSE);
INSERT INTO cities(id, name, country_id, deleted) VALUES(20000, 'city2', 10000, FALSE);
INSERT INTO cities(id, name, country_id, deleted) VALUES(30000, 'city3', 20000, FALSE);
INSERT INTO cities(id, name, country_id, deleted) VALUES(40000, 'deleted city', 20000, TRUE);

INSERT INTO universities(id, name, abbreviation, city_id, deleted) VALUES(10000, 'Instituto de muy largo', 'ITBA', 10000, FALSE);
INSERT INTO universities(id, name, abbreviation, city_id, deleted) VALUES(20000, 'Universidad de muy largo', 'UBA', 20000, FALSE);
INSERT INTO universities(id, name, abbreviation, city_id, deleted) VALUES(30000, 'Another one', 'MAS', 20000, FALSE);
INSERT INTO universities(id, name, abbreviation, city_id, deleted) VALUES(40000, 'Deleted uni', 'DEL', 10000, TRUE);

INSERT INTO images(id, content) VALUES(10000, 'ffffffff');
INSERT INTO images(id, content) VALUES(20000, 'ffffffffffffffff');

INSERT INTO category(id, name) VALUES(10000, 'interest 1');
INSERT INTO category(id, name) VALUES(20000, 'interest 2');
INSERT INTO category(id, name) VALUES(30000, 'interest 3');

INSERT INTO users(id, email, username, firstname, lastname, password, language, university, career_id, profile_picture_id, roles, blocked) VALUES(10000, 'user1@mail.com', 'user1', 'user', 'user', 'superSecret', 'es', 10000, 10000, 10000, 'user', FALSE);
INSERT INTO users(id, email, username, firstname, lastname, password, language, university, career_id, profile_picture_id, roles, blocked) VALUES(20000, 'user2@mail.com', 'user2', 'user', 'user', 'superSecret', 'es', 10000, 10000, 10000, 'user', FALSE);
INSERT INTO users(id, email, username, firstname, lastname, password, language, university, career_id, profile_picture_id, roles, blocked, token, token_expiration) VALUES(30000, 'user3@mail.com', 'user3', 'user', 'user', 'superSecret', 'es', 10000, 10000, 10000, 'user', FALSE, 'token', CURRENT_DATE + INTERVAL '1' DAY);
INSERT INTO users(id, email, username, firstname, lastname, password, language, university, career_id, profile_picture_id, roles, blocked, validated) VALUES(40000, 'user4@mail.com', 'user4', 'user', 'user', 'superSecret', 'es', 20000, 10000, 10000, 'user', FALSE, FALSE);
INSERT INTO users(id, username, email, firstname, lastname, password, language, university, career_id, profile_picture_id, roles, blocked) VALUES(50000, '1interest', '1interest@mail.com', 'user', 'user', 'superSecret', 'en', 10000, 10000, 10000, 'user', FALSE);
INSERT INTO users(id, username, email, firstname, lastname, password, language, university, career_id, profile_picture_id, roles, blocked) VALUES(60000, '2interest', '2interest@mail.com', 'user', 'user', 'superSecret', 'en', 10000, 10000, 10000, 'user', FALSE);
INSERT INTO users(id, username, email, firstname, lastname, password, language, university, career_id, profile_picture_id, roles, blocked) VALUES(70000, '3interest', '3interest@mail.com', 'user', 'user', 'superSecret', 'en', 10000, 10000, 10000, 'user', FALSE);

INSERT INTO user_interest(user_id, category_id, score) VALUES(10000, 10000, 4);
INSERT INTO user_interest(user_id, category_id, score) VALUES(10000, 20000, 2);
INSERT INTO user_interest(user_id, category_id, score) VALUES(10000, 30000, 1);
INSERT INTO user_interest(user_id, category_id, score) VALUES(50000, 10000, 1);
INSERT INTO user_interest(user_id, category_id, score) VALUES(60000, 20000, 1);
INSERT INTO user_interest(user_id, category_id, score) VALUES(60000, 10000, 1);
INSERT INTO user_interest(user_id, category_id, score) VALUES(70000, 10000, 1);
INSERT INTO user_interest(user_id, category_id, score) VALUES(70000, 20000, 1);
INSERT INTO user_interest(user_id, category_id, score) VALUES(70000, 30000, 1);

INSERT INTO journeys(id, user_id, destination_university_id, start_date, end_date, description, deleted) VALUES(10000, 40000, 10000, CURRENT_DATE, CURRENT_DATE, 'deleted', TRUE);
INSERT INTO journeys(id, user_id, destination_university_id, start_date, end_date, description, deleted) VALUES(20000, 10000, 20000, CURRENT_DATE + INTERVAL '7' DAY, CURRENT_DATE + INTERVAL '7' DAY + INTERVAL '1' MONTH, 'Cool journey', FALSE);
INSERT INTO journeys(id, user_id, destination_university_id, start_date, end_date, description, deleted) VALUES(30000, 20000, 20000, CURRENT_DATE + INTERVAL '7' DAY, CURRENT_DATE + INTERVAL '7' DAY + INTERVAL '1' MONTH, 'Cool journey', FALSE);

INSERT INTO journey_responses(id, user_id, journey_id, message, date_time, deleted) VALUES(10000, 10000, 20000, 'COOL!', CURRENT_DATE + INTERVAL '0' HOUR, FALSE);
INSERT INTO journey_responses(id, user_id, journey_id, message, date_time, deleted) VALUES(20000, 20000, 20000, 'COOL!', CURRENT_DATE + INTERVAL '1' HOUR, FALSE);
INSERT INTO journey_responses(id, user_id, journey_id, message, date_time, deleted) VALUES(30000, 30000, 20000, 'COOL!', CURRENT_DATE + INTERVAL '2' HOUR, FALSE);
INSERT INTO journey_responses(id, user_id, journey_id, message, date_time, deleted) VALUES(40000, 30000, 20000, 'COOL!', CURRENT_DATE + INTERVAL '0' HOUR, TRUE);

INSERT INTO events(id, user_id, city_id, event_date, event_time, address, attendees_limit, attendees_count, description, title, flyer_image_id, deleted) VALUES(10000, 10000, 10000, CURRENT_DATE + INTERVAL '7' DAY, TIME '00:00:00', 'cool place', 30, 3, 'cool event', 'warm event', 10000, FALSE);
INSERT INTO events(id, user_id, city_id, event_date, event_time, address, attendees_limit, attendees_count, description, title, flyer_image_id, deleted) VALUES(20000, 20000, 10000, CURRENT_DATE + INTERVAL '7' DAY, TIME '00:00:00', 'cool place', NULL, 1, 'cool event', 'another event', 10000, FALSE);
INSERT INTO events(id, user_id, city_id, event_date, event_time, address, attendees_limit, attendees_count, description, title, flyer_image_id, deleted) VALUES(30000, 20000, 10000, CURRENT_DATE + INTERVAL '7' DAY, TIME '00:00:00', 'cool place', 30, 0, 'cool event', 'one more event', 10000, FALSE);
INSERT INTO events(id, user_id, city_id, event_date, event_time, address, attendees_limit, attendees_count, description, title, flyer_image_id, deleted) VALUES(40000, 10000, 10000, CURRENT_DATE - INTERVAL '100' DAY, TIME '00:00:00', 'cool place', 30, 0, 'cool event', 'older event', 10000, FALSE);
INSERT INTO events(id, user_id, city_id, event_date, event_time, address, attendees_limit, attendees_count, description, title, flyer_image_id, deleted) VALUES(50000, 20000, 10000, CURRENT_DATE + INTERVAL '7' DAY, TIME '00:00:00', 'cool place', 30, 0, 'cool event', 'deleted', 10000, TRUE);

INSERT INTO event_attendances(user_id, event_id) VALUES(10000, 10000);
INSERT INTO event_attendances(user_id, event_id) VALUES(20000, 10000);
INSERT INTO event_attendances(user_id, event_id) VALUES(30000, 10000);
INSERT INTO event_attendances(user_id, event_id) VALUES(10000, 20000);

INSERT INTO event_responses(id, user_id, event_id, message, date_time, deleted) VALUES(10000, 10000, 10000, 'COOL!', CURRENT_DATE + INTERVAL '0' HOUR, FALSE);
INSERT INTO event_responses(id, user_id, event_id, message, date_time, deleted) VALUES(20000, 10000, 10000, 'COOL!', CURRENT_DATE + INTERVAL '0' HOUR, FALSE);
INSERT INTO event_responses(id, user_id, event_id, message, date_time, deleted) VALUES(30000, 10000, 10000, 'COOL!', CURRENT_DATE + INTERVAL '0' HOUR, FALSE);
INSERT INTO event_responses(id, user_id, event_id, message, date_time, deleted) VALUES(40000, 10000, 10000, 'COOL!', CURRENT_DATE + INTERVAL '0' HOUR, TRUE);
