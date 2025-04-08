CREATE TABLE IF NOT EXISTS images (
        id SERIAL PRIMARY KEY,
        content BYTEA NOT NULL
);

CREATE TABLE IF NOT EXISTS category (
        id SERIAL PRIMARY KEY,
        en_name varchar(100) NOT NULL UNIQUE,
        es_name varchar(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS user_interest (
        user_id INTEGER NOT NULL,
        category_id INTEGER NOT NULL,
        description VARCHAR(200),
        PRIMARY KEY (user_id, category_id),
        FOREIGN KEY (category_id) REFERENCES category ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS universities (
        id SERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL UNIQUE,
        abbreviation VARCHAR(255) DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS careers (
        id SERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL UNIQUE
);

-- CREATE TABLE IF NOT EXISTS career_in_university (
--         id SERIAL PRIMARY KEY,
--         career_id INTEGER NOT NULL,
--         university_id INTEGER NOT NULL,
--         FOREIGN KEY (career_id) REFERENCES career(id) ON DELETE CASCADE,
--         FOREIGN KEY (university_id) REFERENCES university(id) ON DELETE CASCADE,
--         UNIQUE (career_id, university_id)
-- );


CREATE TABLE IF NOT EXISTS users (
        id SERIAL PRIMARY KEY,
        email VARCHAR(100) NOT NULL UNIQUE,
        firstname VARCHAR(100) NOT NULL,
        lastname VARCHAR(100) NOT NULL,
        username VARCHAR(50) NOT NULL UNIQUE,
        university INTEGER NOT NULL,
        career_id INTEGER NOT NULL,
        profile_picture_id INTEGER NOT NULL,

        FOREIGN KEY (university) REFERENCES universities(id) ON DELETE RESTRICT,
        FOREIGN KEY (career_id) REFERENCES careers(id) ON DELETE RESTRICT
);

-- CREATE TABLE IF NOT EXISTS area_of_study (
--         id SERIAL PRIMARY KEY,
--         name VARCHAR(255) NOT NULL UNIQUE
-- );

-- CREATE TABLE IF NOT EXISTS career_area (
--         id SERIAL PRIMARY KEY,
--         career_id INTEGER NOT NULL,
--         area_of_study_id INTEGER NOT NULL,
--         UNIQUE(career_id, area_of_study_id),
--         FOREIGN KEY (career_id) REFERENCES career(id) ON DELETE CASCADE,
--         FOREIGN KEY (area_of_study_id) REFERENCES area_of_study(id) ON DELETE CASCADE
-- );

CREATE TABLE IF NOT EXISTS countries (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(3) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS cities (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    country_id INTEGER NOT NULL,

    FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE RESTRICT
);




CREATE TABLE IF NOT EXISTS journeys (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL UNIQUE,
    destination_university_id INTEGER NOT NULL,
    city_id INTEGER NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    description VARCHAR(2047),
    FOREIGN KEY (city_id) REFERENCES cities(id) ON DELETE RESTRICT,
    FOREIGN KEY (destination_university_id) REFERENCES universities(id) ON DELETE RESTRICT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
    );


CREATE TABLE IF NOT EXISTS journey_responses (
        user_id INTEGER NOT NULL,
        journey_id INTEGER NOT NULL,
        message VARCHAR(1023) NOT NULL,
        -- agregar created_at -> ¿Se puede autogenerar con el motor de la base de datos?

        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        FOREIGN KEY (journey_id) REFERENCES journeys(id) ON DELETE CASCADE,
        PRIMARY KEY (user_id, journey_id)
);


CREATE TABLE IF NOT EXISTS events (
                                     id SERIAL PRIMARY KEY,
                                     user_id INTEGER NOT NULL,
                                     city_id INTEGER NOT NULL,
                                     event_date DATE NOT NULL,
                                     description VARCHAR(2047),
    flyer_image_id INTEGER,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (city_id) REFERENCES cities(id) ON DELETE RESTRICT,
    FOREIGN KEY (flyer_image_id) REFERENCES images(id) ON DELETE RESTRICT
    );

CREATE TABLE IF NOT EXISTS event_responses (
                                               user_id INTEGER NOT NULL,
                                               event_id INTEGER NOT NULL,
                                               message VARCHAR(1023) NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, event_id)
    );
