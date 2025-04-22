CREATE TABLE IF NOT EXISTS images (
        id SERIAL PRIMARY KEY,
        content BYTEA NOT NULL
);

CREATE TABLE IF NOT EXISTS category (
        id SERIAL PRIMARY KEY,
        name varchar(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS user_interest (
        user_id INTEGER NOT NULL,
        category_id INTEGER NOT NULL,
        score INTEGER NOT NULL DEFAULT 0,

        PRIMARY KEY (user_id, category_id),
        FOREIGN KEY (category_id) REFERENCES category ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS countries (
        id SERIAL PRIMARY KEY,
        name VARCHAR(100) NOT NULL UNIQUE,
        code VARCHAR(3) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS cities (
        id SERIAL PRIMARY KEY,
        name VARCHAR(100) UNIQUE,
        country_id INTEGER NOT NULL,

        FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE RESTRICT
);


CREATE TABLE IF NOT EXISTS universities (
        id SERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL UNIQUE,
        city_id INTEGER NOT NULL,
        abbreviation VARCHAR(255) DEFAULT NULL,

        FOREIGN KEY (city_id) REFERENCES cities(id) ON DELETE RESTRICT
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
        language VARCHAR(2) NOT NULL DEFAULT 'en' CHECK (language IN ('en', 'es')),

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





CREATE TABLE IF NOT EXISTS journeys (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL UNIQUE,
    destination_university_id INTEGER NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    description VARCHAR(2047),
    FOREIGN KEY (destination_university_id) REFERENCES universities(id) ON DELETE RESTRICT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
    );


CREATE TABLE IF NOT EXISTS journey_responses (
        user_id INTEGER NOT NULL,
        journey_id INTEGER NOT NULL,
        message VARCHAR(1023) NOT NULL,
        date_time TIMESTAMP NOT NULL DEFAULT CURRENT_DATE,
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
        event_time TIME,
        attendees_limit INT,
        address VARCHAR(255),
        flyer_image_id INTEGER,
        attendees_count INTEGER DEFAULT 0,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (city_id) REFERENCES cities(id) ON DELETE RESTRICT,
    FOREIGN KEY (flyer_image_id) REFERENCES images(id) ON DELETE RESTRICT
    );

CREATE TABLE IF NOT EXISTS event_responses (
                                               user_id INTEGER NOT NULL,
                                               event_id INTEGER NOT NULL,
                                               message VARCHAR(1023) NOT NULL,
                                               date_time TIMESTAMP NOT NULL DEFAULT CURRENT_DATE,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, event_id)
    );

CREATE TABLE IF NOT EXISTS event_attendances (
        user_id INTEGER NOT NULL,
        event_id INTEGER NOT NULL,

        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
        PRIMARY KEY (user_id, event_id)
);

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS password VARCHAR(100) NOT NULL DEFAULT '$2b$10$KbQiA8xVuOPQkfiYJ0X0FubQbQjEJpTr6QOBD3qL6sYzFoq2nJ8fK';


UPDATE users
SET password = '$2b$10$KbQiA8xVuOPQkfiYJ0X0FubQbQjEJpTr6QOBD3qL6sYzFoq2nJ8fK'
WHERE password IS NULL;

ALTER TABLE users ADD COLUMN IF NOT EXISTS language VARCHAR(2) NOT NULL DEFAULT 'en' CHECK (language IN ('en', 'es'));
-- ALTER TABLE user_interest ADD COLUMN IF NOT EXISTS score INTEGER NOT NULL DEFAULT 0
ALTER TABLE event_responses ADD COLUMN IF NOT EXISTS date_time TIMESTAMP NOT NULL DEFAULT CURRENT_DATE;
ALTER TABLE journey_responses ADD COLUMN IF NOT EXISTS date_time TIMESTAMP NOT NULL DEFAULT CURRENT_DATE;

ALTER TABLE user_interest ADD COLUMN IF NOT EXISTS score INTEGER NOT NULL DEFAULT 0;

CREATE INDEX IF NOT EXISTS idx_journey_responses_journeyid_datetime
    ON journey_responses (journey_id, date_time);

-- Step 1: Add column if it doesn't exist
ALTER TABLE events
    ADD COLUMN IF NOT EXISTS title VARCHAR(255);

-- Step 2: Fill NULL titles with default value
UPDATE events
SET title = 'Event'
WHERE title IS NULL;

-- Step 3: Set column as NOT NULL (safe because we just filled NULLs)
ALTER TABLE events
    ALTER COLUMN title SET NOT NULL;



-- Solo hay que ejecutar el codigo siguiente una vez. Lo dejo comentado para evitar errores.

-- BEGIN;
-- ALTER TABLE journey_responses
-- DROP CONSTRAINT journey_responses_pkey;
--
-- ALTER TABLE journey_responses
--     ADD COLUMN id SERIAL PRIMARY KEY;
-- COMMIT;

-- BEGIN;
-- ALTER TABLE event_responses
--     DROP CONSTRAINT event_responses_pkey;
--
-- ALTER TABLE event_responses
--     ADD COLUMN id SERIAL PRIMARY KEY;
-- COMMIT;

BEGIN;
ALTER TABLE events ADD COLUMN IF NOT EXISTS event_time TIME;
ALTER TABLE events ADD COLUMN IF NOT EXISTS attendees_limit INT;
ALTER TABLE events ADD COLUMN IF NOT EXISTS address VARCHAR(255);
COMMIT;

-- Add attendees_count column to events table with a default value of 0
ALTER TABLE events ADD COLUMN IF NOT EXISTS attendees_count INTEGER NOT NULL DEFAULT 0;

--
-- UPDATE events e SET attendees_count = (
--     SELECT COUNT(*)
--     FROM event_attendances ea
--     WHERE ea.event_id = e.id
-- );