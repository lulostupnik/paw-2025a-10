CREATE TABLE IF NOT EXISTS images (
        id BIGSERIAL PRIMARY KEY,
        content BYTEA NOT NULL
);

CREATE TABLE IF NOT EXISTS category (
        id BIGSERIAL PRIMARY KEY,
        name varchar(100) NOT NULL UNIQUE
);


CREATE TABLE IF NOT EXISTS countries (
        id BIGSERIAL PRIMARY KEY,
        name VARCHAR(100) NOT NULL UNIQUE,
        code VARCHAR(3) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS cities (
        id BIGSERIAL PRIMARY KEY,
        name VARCHAR(100) NOT NULL UNIQUE,
        country_id BIGINT NOT NULL,
        deleted BOOLEAN NOT NULL DEFAULT FALSE,

        FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE RESTRICT
);


CREATE TABLE IF NOT EXISTS universities (
        id BIGSERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL UNIQUE,
        city_id BIGINT NOT NULL,
        abbreviation VARCHAR(255) DEFAULT NULL,
        deleted BOOLEAN NOT NULL DEFAULT FALSE,

        FOREIGN KEY (city_id) REFERENCES cities(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS careers (
        id BIGSERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL UNIQUE,
        deleted BOOLEAN NOT NULL DEFAULT FALSE
);


CREATE TABLE IF NOT EXISTS users (
        id BIGSERIAL PRIMARY KEY,
        email VARCHAR(100) NOT NULL UNIQUE,
        firstname VARCHAR(100) NOT NULL,
        lastname VARCHAR(100) NOT NULL,
        username VARCHAR(50) NOT NULL UNIQUE,
        university BIGINT NOT NULL,
        career_id BIGINT NOT NULL,
        profile_picture_id BIGINT NOT NULL,
        password VARCHAR(100) NOT NULL DEFAULT '$2b$10$KbQiA8xVuOPQkfiYJ0X0FubQbQjEJpTr6QOBD3qL6sYzFoq2nJ8fK',
        roles VARCHAR(50) DEFAULT 'user' CHECK (roles IN ('user', 'admin')),
        language VARCHAR(2) NOT NULL DEFAULT 'en',
        blocked BOOLEAN NOT NULL DEFAULT FALSE,
        validated BOOLEAN NOT NULL DEFAULT TRUE,

    FOREIGN KEY (university) REFERENCES universities(id) ON DELETE RESTRICT,
    FOREIGN KEY (career_id) REFERENCES careers(id) ON DELETE RESTRICT,
    FOREIGN KEY(profile_picture_id) REFERENCES images ON DELETE RESTRICT
    );

    CREATE TABLE IF NOT EXISTS user_interest (
                                             user_id BIGINT NOT NULL,
                                             category_id BIGINT NOT NULL,
                                             score INTEGER NOT NULL DEFAULT 0,

                                             PRIMARY KEY (user_id, category_id),
    FOREIGN KEY (category_id) REFERENCES category ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );



CREATE TABLE IF NOT EXISTS journeys (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    destination_university_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    description VARCHAR(2047),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_message VARCHAR(1000),
    FOREIGN KEY (destination_university_id) REFERENCES universities(id) ON DELETE RESTRICT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
    );


CREATE TABLE IF NOT EXISTS journey_responses (
        id BIGSERIAL PRIMARY KEY,
        user_id BIGINT NOT NULL,
        journey_id BIGINT NOT NULL,
        message VARCHAR(1023) NOT NULL,
        date_time TIMESTAMP NOT NULL DEFAULT CURRENT_DATE,
        deleted BOOLEAN NOT NULL DEFAULT FALSE,
        deleted_message VARCHAR(1000),

        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        FOREIGN KEY (journey_id) REFERENCES journeys(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS events (
        id BIGSERIAL PRIMARY KEY,
        user_id BIGINT NOT NULL,
        city_id BIGINT NOT NULL,
        event_date DATE NOT NULL,
        description VARCHAR(2047),
        event_time TIME,
        attendees_limit INT,
        address VARCHAR(255),
        flyer_image_id BIGINT NOT NULL,
        attendees_count INTEGER DEFAULT 0 NOT NULL,
        title VARCHAR(255) NOT NULL,
        deleted BOOLEAN NOT NULL DEFAULT FALSE,
        deleted_message VARCHAR(1000),

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (city_id) REFERENCES cities(id) ON DELETE RESTRICT,
    FOREIGN KEY (flyer_image_id) REFERENCES images(id) ON DELETE RESTRICT
    );

CREATE TABLE IF NOT EXISTS event_responses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    message VARCHAR(1023) NOT NULL,
    date_time TIMESTAMP NOT NULL DEFAULT CURRENT_DATE,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_message VARCHAR(1000),

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS event_attendances (
        user_id BIGINT NOT NULL,
        event_id BIGINT NOT NULL,

        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
        PRIMARY KEY (user_id, event_id)
);

CREATE TABLE IF NOT EXISTS reports (
                         id BIGSERIAL PRIMARY KEY,
                         reported_user_id BIGINT NOT NULL,
                         reporting_user_id BIGINT NOT NULL,
                         journey_id BIGINT,
                         event_id BIGINT,
                         event_response_id BIGINT,
                         journey_response_id BIGINT,
                         description VARCHAR(1000) NOT NULL,
                         reason VARCHAR(255) NOT NULL,
                         deleted BOOLEAN NOT NULL DEFAULT FALSE,
                         status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (reported_user_id) REFERENCES users(id) ON DELETE CASCADE,
                         FOREIGN KEY (reporting_user_id) REFERENCES users(id) ON DELETE CASCADE,
                         FOREIGN KEY (journey_id) REFERENCES journeys(id) ON DELETE CASCADE,
                         FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
                         FOREIGN KEY (event_response_id) REFERENCES event_responses(id) ON DELETE CASCADE,
                         FOREIGN KEY (journey_response_id) REFERENCES journey_responses(id) ON DELETE CASCADE
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

DROP INDEX IF EXISTS idx_journey_responses_journeyid_datetime;

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


-- BEGIN;
-- ALTER TABLE events ADD COLUMN IF NOT EXISTS attendees_count INTEGER NOT NULL DEFAULT 0;
--
-- UPDATE events e SET attendees_count = (
--     SELECT COUNT(*)
--     FROM event_attendances ea
--     WHERE ea.event_id = e.id
-- );
-- COMMIT;


-- INSERT INTO event_attendance (user_id, event_id)
-- SELECT e.user_id, e.id
-- FROM events e
-- WHERE NOT EXISTS (
--     SELECT 1
--     FROM event_attendance ea
--     WHERE ea.event_id = e.id AND ea.user_id = e.user_id
-- );

BEGIN;
ALTER TABLE events ADD COLUMN IF NOT EXISTS deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE journeys ADD COLUMN IF NOT EXISTS deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE journey_responses ADD COLUMN IF NOT EXISTS deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE event_responses ADD COLUMN IF NOT EXISTS deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE events ADD COLUMN IF NOT EXISTS deleted_message VARCHAR(1000);
ALTER TABLE journeys ADD COLUMN IF NOT EXISTS deleted_message VARCHAR(1000);
ALTER TABLE journey_responses ADD COLUMN IF NOT EXISTS deleted_message VARCHAR(1000);
ALTER TABLE event_responses ADD COLUMN IF NOT EXISTS deleted_message VARCHAR(1000);
ALTER TABLE cities ADD COLUMN IF NOT EXISTS deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE universities ADD COLUMN IF NOT EXISTS deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE careers ADD COLUMN IF NOT EXISTS deleted BOOLEAN NOT NULL DEFAULT FALSE;
COMMIT;

ALTER TABLE users ADD COLUMN IF NOT EXISTS roles VARCHAR(50) DEFAULT 'user' CHECK (roles IN ('user', 'admin'));
ALTER TABLE users ADD COLUMN IF NOT EXISTS token VARCHAR(100) UNIQUE DEFAULT NULL;
Alter TABLE users ADD COLUMN IF NOT EXISTS token_expiration Date DEFAULT NULL;
ALTER TABLE users ADD COLUMN IF NOT EXISTS validated BOOLEAN NOT NULL DEFAULT TRUE;


ALTER TABLE users add column IF NOT EXISTS blocked BOOLEAN NOT NULL DEFAULT FALSE;


ALTER TABLE users DROP CONSTRAINT IF EXISTS users_language_check;


BEGIN;
CREATE TABLE IF NOT EXISTS tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(100) UNIQUE,
    token_expiration TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
INSERT INTO tokens (user_id, token, token_expiration)
SELECT id, token, token_expiration FROM users;

ALTER TABLE users
DROP COLUMN if exists token,
    DROP COLUMN IF EXISTS token_expiration;

ALTER TABLE users
DROP COLUMN IF EXISTS validate_token,
    DROP COLUMN IF EXISTS validate_token_expiration_date;

COMMIT;




-- BEGIN;
-- -- IDs: SERIAL → BIGINT
-- ALTER TABLE images ALTER COLUMN id TYPE BIGINT;
-- ALTER TABLE category ALTER COLUMN id TYPE BIGINT;
-- ALTER TABLE countries ALTER COLUMN id TYPE BIGINT;
-- ALTER TABLE cities ALTER COLUMN id TYPE BIGINT;
-- ALTER TABLE universities ALTER COLUMN id TYPE BIGINT;
-- ALTER TABLE careers ALTER COLUMN id TYPE BIGINT;
-- ALTER TABLE users ALTER COLUMN id TYPE BIGINT;
-- ALTER TABLE journeys ALTER COLUMN id TYPE BIGINT;
-- ALTER TABLE journey_responses ALTER COLUMN id TYPE BIGINT;
-- ALTER TABLE events ALTER COLUMN id TYPE BIGINT;
-- ALTER TABLE event_responses ALTER COLUMN id TYPE BIGINT;
-- -- ALTER TABLE tokens ALTER COLUMN id TYPE BIGINT;
--
-- -- Foreign keys: INTEGER → BIGINT
-- ALTER TABLE cities ALTER COLUMN country_id TYPE BIGINT;
-- ALTER TABLE universities ALTER COLUMN city_id TYPE BIGINT;
-- ALTER TABLE users ALTER COLUMN university TYPE BIGINT;
-- ALTER TABLE users ALTER COLUMN career_id TYPE BIGINT;
-- ALTER TABLE users ALTER COLUMN profile_picture_id TYPE BIGINT;
-- ALTER TABLE user_interest ALTER COLUMN user_id TYPE BIGINT;
-- ALTER TABLE user_interest ALTER COLUMN category_id TYPE BIGINT;
-- ALTER TABLE journeys ALTER COLUMN user_id TYPE BIGINT;
-- ALTER TABLE journeys ALTER COLUMN destination_university_id TYPE BIGINT;
-- ALTER TABLE journey_responses ALTER COLUMN user_id TYPE BIGINT;
-- ALTER TABLE journey_responses ALTER COLUMN journey_id TYPE BIGINT;
-- ALTER TABLE events ALTER COLUMN user_id TYPE BIGINT;
-- ALTER TABLE events ALTER COLUMN city_id TYPE BIGINT;
-- ALTER TABLE events ALTER COLUMN flyer_image_id TYPE BIGINT;
-- ALTER TABLE event_responses ALTER COLUMN user_id TYPE BIGINT;
-- ALTER TABLE event_responses ALTER COLUMN event_id TYPE BIGINT;
-- ALTER TABLE event_attendances ALTER COLUMN user_id TYPE BIGINT;
-- ALTER TABLE event_attendances ALTER COLUMN event_id TYPE BIGINT;
-- -- ALTER TABLE tokens ALTER COLUMN user_id TYPE BIGINT;
-- COMMIT;

ALTER TABLE events
    ALTER COLUMN flyer_image_id SET NOT NULL;


-- ALTER TABLE user_interest DROP COLUMN IF EXISTS interest_id; En local estaba de mas esta columna. Checkear en produccion antes de hacerlo.

BEGIN;

ALTER TABLE users
    DROP CONSTRAINT IF EXISTS users_roles_check;

UPDATE users SET roles = 'USER' WHERE roles = 'user';
UPDATE users SET roles = 'ADMIN' WHERE roles = 'admin';

ALTER TABLE users
    ADD CONSTRAINT users_roles_check
        CHECK (roles IN ('USER', 'ADMIN'));


COMMIT;
