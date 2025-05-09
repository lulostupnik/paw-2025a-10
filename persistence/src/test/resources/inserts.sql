INSERT INTO careers(name, deleted) VALUES('career 1', FALSE);
INSERT INTO careers(name, deleted) VALUES('career 2', FALSE);
INSERT INTO careers(name, deleted) VALUES('deleted', TRUE);

INSERT INTO countries(name, code) VALUES('cuntry', 'aa');
INSERT INTO countries(name, code) VALUES('cuntry2', 'bb');

INSERT INTO cities(name, country_id, deleted) VALUES('city1', (SELECT id FROM countries WHERE code = 'aa'), FALSE);
INSERT INTO cities(name, country_id, deleted) VALUES('city2', (SELECT id FROM countries WHERE code = 'aa'), FALSE);
INSERT INTO cities(name, country_id, deleted) VALUES('city3', (SELECT id FROM countries WHERE code = 'bb'), FALSE);
INSERT INTO cities(name, country_id, deleted) VALUES('deleted city', (SELECT id FROM countries WHERE code = 'bb'), TRUE);

INSERT INTO universities(name, abbreviation, city_id, deleted) VALUES('Instituto de muy largo', 'ITBA', (SELECT id FROM cities WHERE name = 'city1'), FALSE);
INSERT INTO universities(name, abbreviation, city_id, deleted) VALUES('Universidad de muy largo', 'UBA', (SELECT id FROM cities WHERE name = 'city2'), FALSE);
INSERT INTO universities(name, abbreviation, city_id, deleted) VALUES('Another one', 'MAS', (SELECT id FROM cities WHERE name = 'city2'), FALSE);
INSERT INTO universities(name, abbreviation, city_id, deleted) VALUES('Deleted uni', 'DEL', (SELECT id FROM cities WHERE name = 'city1'), TRUE);

INSERT INTO images(content) VALUES('ffffffff');
INSERT INTO images(content) VALUES('ffffffffffffffff');

INSERT INTO category(name) VALUES('interest 1');
INSERT INTO category(name) VALUES('interest 2');
INSERT INTO category(name) VALUES('interest 3');

INSERT INTO users(email, username, firstname, lastname, password, language, university, career_id, profile_picture_id, roles, blocked) VALUES('user1@mail.com', 'user1', 'user', 'user', 'superSecret', 'es', (SELECT id FROM universities WHERE abbreviation = 'ITBA'), (SELECT id FROM careers WHERE name = 'career 1'), (SELECT id FROM images LIMIT 1), 'user', FALSE);
INSERT INTO users(email, username, firstname, lastname, password, language, university, career_id, profile_picture_id, roles, blocked) VALUES('user2@mail.com', 'user2', 'user', 'user', 'superSecret', 'es', (SELECT id FROM universities WHERE abbreviation = 'ITBA'), (SELECT id FROM careers WHERE name = 'career 1'), (SELECT id FROM images LIMIT 1), 'user', FALSE);
INSERT INTO users(email, username, firstname, lastname, password, language, university, career_id, profile_picture_id, roles, blocked) VALUES('user3@mail.com', 'user3', 'user', 'user', 'superSecret', 'es', (SELECT id FROM universities WHERE abbreviation = 'ITBA'), (SELECT id FROM careers WHERE name = 'career 1'), (SELECT id FROM images LIMIT 1), 'user', FALSE);
INSERT INTO users(email, username, firstname, lastname, password, language, university, career_id, profile_picture_id, roles, blocked) VALUES('user4@mail.com', 'user4', 'user', 'user', 'superSecret', 'es', (SELECT id FROM universities WHERE abbreviation = 'UBA'), (SELECT id FROM careers WHERE name = 'career 1'), (SELECT id FROM images LIMIT 1), 'user', FALSE);
INSERT INTO users(username, email, firstname, lastname, password, language, university, career_id, profile_picture_id, roles, blocked) VALUES('1interest', '1interest@mail.com', 'user', 'name', 'superSecret', 'en', (SELECT id FROM universities WHERE abbreviation = 'ITBA'), (SELECT id FROM careers LIMIT 1), (SELECT id FROM images LIMIT 1), 'user', FALSE);
INSERT INTO users(username, email, firstname, lastname, password, language, university, career_id, profile_picture_id, roles, blocked) VALUES('2interest', '2interest@mail.com', 'user', 'name', 'superSecret', 'en', (SELECT id FROM universities WHERE abbreviation = 'ITBA'), (SELECT id FROM careers LIMIT 1), (SELECT id FROM images LIMIT 1), 'user', FALSE);
INSERT INTO users(username, email, firstname, lastname, password, language, university, career_id, profile_picture_id, roles, blocked) VALUES('3interest', '3interest@mail.com', 'user', 'name', 'superSecret', 'en', (SELECT id FROM universities WHERE abbreviation = 'ITBA'), (SELECT id FROM careers LIMIT 1), (SELECT id FROM images LIMIT 1), 'user', FALSE);

INSERT INTO user_interest(user_id, category_id, score) VALUES((SELECT id FROM users WHERE username = 'user1'), (SELECT id FROM category WHERE name = 'interest 1'), 4);
INSERT INTO user_interest(user_id, category_id, score) VALUES((SELECT id FROM users WHERE username = 'user1'), (SELECT id FROM category WHERE name = 'interest 2'), 2);
INSERT INTO user_interest(user_id, category_id, score) VALUES((SELECT id FROM users WHERE username = 'user1'), (SELECT id FROM category WHERE name = 'interest 3'), 1);
INSERT INTO user_interest(user_id, category_id, score) VALUES((SELECT id FROM users WHERE username = '1interest'), (SELECT id FROM category WHERE name = 'interest 1'), 1);
INSERT INTO user_interest(user_id, category_id, score) VALUES((SELECT id FROM users WHERE username = '2interest'), (SELECT id FROM category WHERE name = 'interest 2'), 1);
INSERT INTO user_interest(user_id, category_id, score) VALUES((SELECT id FROM users WHERE username = '2interest'), (SELECT id FROM category WHERE name = 'interest 1'), 1);
INSERT INTO user_interest(user_id, category_id, score) VALUES((SELECT id FROM users WHERE username = '3interest'), (SELECT id FROM category WHERE name = 'interest 1'), 1);
INSERT INTO user_interest(user_id, category_id, score) VALUES((SELECT id FROM users WHERE username = '3interest'), (SELECT id FROM category WHERE name = 'interest 2'), 1);
INSERT INTO user_interest(user_id, category_id, score) VALUES((SELECT id FROM users WHERE username = '3interest'), (SELECT id FROM category WHERE name = 'interest 3'), 1);

INSERT INTO journeys(user_id, destination_university_id, start_date, end_date, description, deleted) VALUES((SELECT id FROM users WHERE username = 'user4'), (SELECT id FROM universities WHERE abbreviation = 'ITBA'), CURRENT_DATE, CURRENT_DATE, 'deleted', TRUE);
INSERT INTO journeys(user_id, destination_university_id, start_date, end_date, description, deleted) VALUES((SELECT id FROM users WHERE username = 'user1'), (SELECT id FROM universities WHERE abbreviation = 'UBA'), CURRENT_DATE + INTERVAL '7' DAY, CURRENT_DATE + INTERVAL '7' DAY + INTERVAL '1' MONTH, 'Cool journey', FALSE);