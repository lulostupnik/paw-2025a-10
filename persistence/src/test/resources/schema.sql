CREATE SEQUENCE images_id_seq START WITH 3;
CREATE TABLE IF NOT EXISTS images(
    id bigint DEFAULT nextval('images_id_seq') PRIMARY KEY,
    content BLOB NOT NULL
);

CREATE SEQUENCE category_id_seq START WITH 4;
CREATE TABLE IF NOT EXISTS category(
    id bigint DEFAULT nextval('category_id_seq') PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE SEQUENCE countries_id_seq START WITH 3;
CREATE TABLE IF NOT EXISTS countries(
    id bigint DEFAULT nextval('countries_id_seq') PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(3) NOT NULL UNIQUE
);

CREATE SEQUENCE cities_id_seq START WITH 5;
CREATE TABLE IF NOT EXISTS cities(
    id bigint DEFAULT nextval('cities_id_seq') PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    country_id BIGINT NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    FOREIGN KEY(country_id) REFERENCES countries ON DELETE RESTRICT
);

CREATE SEQUENCE universities_id_seq START WITH 5;
CREATE TABLE IF NOT EXISTS universities(
    id bigint DEFAULT nextval('universities_id_seq') PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    city_id BIGINT NOT NULL,
    abbreviation VARCHAR(255) DEFAULT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    FOREIGN KEY(city_id) REFERENCES cities ON DELETE RESTRICT
);

CREATE SEQUENCE careers_id_seq START WITH 4;
CREATE TABLE IF NOT EXISTS careers(
    id bigint DEFAULT nextval('careers_id_seq') PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    deleted BOOLEAN DEFAULT FALSE NOT NULL
);

CREATE SEQUENCE users_id_seq START WITH 8;
CREATE TABLE IF NOT EXISTS users(
    id bigint DEFAULT nextval('users_id_seq') PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    university BIGINT NOT NULL,
    career_id BIGINT NOT NULL,
    profile_picture_id BIGINT NOT NULL,
    password VARCHAR(100) DEFAULT '$2b$10$KbQiA8xVuOPQkfiYJ0X0FubQbQjEJpTr6QOBD3qL6sYzFoq2nJ8fK' NOT NULL,
    language VARCHAR(2) DEFAULT 'en' NOT NULL,
    roles VARCHAR(50) DEFAULT 'user' NOT NULL,
    blocked BOOLEAN DEFAULT FALSE NOT NULL,
    validated BOOLEAN DEFAULT TRUE NOT NULL,

    FOREIGN KEY(university) REFERENCES universities ON DELETE RESTRICT,
    FOREIGN KEY(career_id) REFERENCES careers ON DELETE RESTRICT,
    FOREIGN KEY(profile_picture_id) REFERENCES images ON DELETE RESTRICT,
    CHECK (roles in ('USER', 'ADMIN'))
);

CREATE TABLE IF NOT EXISTS user_interest(
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    score INTEGER DEFAULT 0 NOT NULL,
    PRIMARY KEY(user_id, category_id),
    FOREIGN KEY(category_id) REFERENCES category ON DELETE CASCADE,
    FOREIGN KEY(user_id) REFERENCES users ON DELETE CASCADE
);

CREATE SEQUENCE journeys_id_seq START WITH 4;
CREATE TABLE IF NOT EXISTS journeys(
    id bigint DEFAULT nextval('journeys_id_seq') PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    destination_university_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    description VARCHAR(2047),
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    deleted_message VARCHAR(1000),
    FOREIGN KEY(user_id) REFERENCES users ON DELETE RESTRICT,
    FOREIGN KEY(destination_university_id) REFERENCES universities ON DELETE RESTRICT
);

CREATE SEQUENCE journey_responses_id_seq START WITH 5;
CREATE TABLE IF NOT EXISTS journey_responses(
    id bigint DEFAULT nextval('journey_responses_id_seq') PRIMARY KEY,
    user_id BIGINT NOT NULL,
    journey_id BIGINT NOT NULL,
    message VARCHAR(1023) NOT NULL,
    date_time TIMESTAMP DEFAULT CURRENT_DATE NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    deleted_message VARCHAR(1000),
    FOREIGN KEY(user_id) REFERENCES users ON DELETE CASCADE,
    FOREIGN KEY(journey_id) REFERENCES journeys ON DELETE CASCADE
);

CREATE SEQUENCE events_id_seq START WITH 6;
CREATE TABLE IF NOT EXISTS events(
    id bigint DEFAULT nextval('events_id_seq') PRIMARY KEY,
    user_id BIGINT NOT NULL,
    city_id BIGINT NOT NULL,
    event_date DATE NOT NULL,
    event_time TIME,
    description VARCHAR(2047),
    attendees_limit INTEGER,
    address VARCHAR(255),
    flyer_image_id BIGINT NOT NULL,
    attendees_count INTEGER DEFAULT 0 NOT NULL,
    title VARCHAR(255) NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    deleted_message VARCHAR(1000),
    FOREIGN KEY(user_id) REFERENCES users ON DELETE RESTRICT,
    FOREIGN KEY(city_id) REFERENCES cities ON DELETE RESTRICT,
    FOREIGN KEY(flyer_image_id) REFERENCES images ON DELETE RESTRICT
);

CREATE SEQUENCE event_responses_id_seq START WITH 4;
CREATE TABLE IF NOT EXISTS event_responses(
    id bigint DEFAULT nextval('event_responses_id_seq') PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    message VARCHAR(1023) NOT NULL,
    date_time TIMESTAMP DEFAULT CURRENT_DATE NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    deleted_message VARCHAR(1000),
    FOREIGN KEY(user_id) REFERENCES users ON DELETE CASCADE,
    FOREIGN KEY(event_id) REFERENCES events ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS event_attendances(
    user_id INTEGER NOT NULL,
    event_id INTEGER NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users ON DELETE CASCADE,
    FOREIGN KEY(event_id) REFERENCES events ON DELETE CASCADE,
    PRIMARY KEY(user_id, event_id)
);

CREATE SEQUENCE reports_id_seq START WITH 8;
CREATE TABLE IF NOT EXISTS reports (
    id bigint DEFAULT nextval('reports_id_seq') PRIMARY KEY,
    reported_user_id BIGINT NOT NULL,
    reporting_user_id BIGINT NOT NULL,
    journey_id BIGINT,
    event_id BIGINT,
    event_response_id BIGINT,
    journey_response_id BIGINT,
    description VARCHAR(1000) NOT NULL,
    reason VARCHAR(255) NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY(reported_user_id) REFERENCES users ON DELETE CASCADE,
    FOREIGN KEY(reporting_user_id) REFERENCES users ON DELETE CASCADE,
    FOREIGN KEY(journey_id) REFERENCES journeys ON DELETE CASCADE,
    FOREIGN KEY(event_id) REFERENCES events ON DELETE CASCADE,
    FOREIGN KEY(event_response_id) REFERENCES event_responses ON DELETE CASCADE,
    FOREIGN KEY(journey_response_id) REFERENCES journey_responses ON DELETE CASCADE
);

CREATE SEQUENCE ratings_id_seq START WITH 6;
CREATE TABLE IF NOT EXISTS ratings (
    id bigint DEFAULT nextval('ratings_id_seq') PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    rating DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    UNIQUE (user_id, event_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CHECK (rating >= 1 AND rating <= 5)
);

CREATE SEQUENCE tokens_id_seq START WITH 5;
CREATE TABLE IF NOT EXISTS tokens (
    id bigint DEFAULT nextval('tokens_id_seq') PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(100) UNIQUE,
    token_expiration TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
