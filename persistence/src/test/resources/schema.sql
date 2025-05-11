    CREATE TABLE IF NOT EXISTS images(
        id IDENTITY PRIMARY KEY,
        content BLOB NOT NULL
    );

    CREATE TABLE IF NOT EXISTS category(
        id IDENTITY PRIMARY KEY,
        name VARCHAR(100) NOT NULL UNIQUE,
    );

    CREATE TABLE IF NOT EXISTS countries(
        id IDENTITY PRIMARY KEY,
        name VARCHAR(100) NOT NULL UNIQUE,
        code VARCHAR(3) NOT NULL UNIQUE
    );

    CREATE TABLE IF NOT EXISTS cities(
        id IDENTITY PRIMARY KEY,
        name VARCHAR(255) NOT NULL UNIQUE,
        country_id INTEGER NOT NULL,
        deleted BOOLEAN DEFAULT FALSE NOT NULL,
        FOREIGN KEY(country_id) REFERENCES countries ON DELETE RESTRICT
    );

    CREATE TABLE IF NOT EXISTS universities(
        id IDENTITY PRIMARY KEY,
        name VARCHAR(255) NOT NULL UNIQUE,
        city_id INTEGER NOT NULL,
        abbreviation VARCHAR(255) DEFAULT NULL,
        deleted BOOLEAN DEFAULT FALSE NOT NULL,
        FOREIGN KEY(city_id) REFERENCES cities ON DELETE RESTRICT
    );

    CREATE TABLE IF NOT EXISTS careers(
        id IDENTITY PRIMARY KEY,
        name VARCHAR(255) NOT NULL UNIQUE,
        deleted BOOLEAN DEFAULT FALSE NOT NULL
    );

    CREATE TABLE IF NOT EXISTS users(
        id IDENTITY PRIMARY KEY,
        email VARCHAR(100) NOT NULL UNIQUE,
        firstname VARCHAR(100) NOT NULL,
        lastname VARCHAR(100) NOT NULL,
        username VARCHAR(50) NOT NULL UNIQUE,
        university INTEGER NOT NULL,
        career_id INTEGER NOT NULL,
        profile_picture_id INTEGER NOT NULL,
        password VARCHAR(100) DEFAULT '$2b$10$KbQiA8xVuOPQkfiYJ0X0FubQbQjEJpTr6QOBD3qL6sYzFoq2nJ8fK' NOT NULL,
        language VARCHAR(2) DEFAULT 'en' NOT NULL,
        roles VARCHAR(50) DEFAULT 'user' NOT NULL,
        blocked BOOLEAN DEFAULT FALSE NOT NULL,
        token VARCHAR(100) DEFAULT NULL,
        token_expiration Date DEFAULT NULL,
        validated BOOLEAN DEFAULT TRUE NOT NULL,

        FOREIGN KEY(university) REFERENCES universities ON DELETE RESTRICT,
        FOREIGN KEY(career_id) REFERENCES careers,
        FOREIGN KEY(profile_picture_id) REFERENCES images,
        CHECK (language IN ('en', 'es')),
        CHECK (roles in ('user', 'admin'))
    );

    CREATE TABLE IF NOT EXISTS user_interest(
        user_id INTEGER NOT NULL,
        category_id INTEGER NOT NULL,
        score INTEGER DEFAULT 0 NOT NULL,
        PRIMARY KEY(user_id, category_id),
        FOREIGN KEY(category_id) REFERENCES category ON DELETE CASCADE,
        FOREIGN KEY(user_id) REFERENCES users ON DELETE CASCADE
    );

    CREATE TABLE IF NOT EXISTS journeys(
        id IDENTITY PRIMARY KEY,
        user_id INTEGER NOT NULL UNIQUE,
        destination_university_id INTEGER NOT NULL,
        start_date DATE NOT NULL,
        end_date DATE NOT NULL,
        description VARCHAR(2047),
        deleted BOOLEAN DEFAULT FALSE NOT NULL,
        deleted_message VARCHAR(1000),
        FOREIGN KEY(user_id) REFERENCES users ON DELETE RESTRICT,
        FOREIGN KEY(destination_university_id) REFERENCES universities ON DELETE RESTRICT
    );

    CREATE TABLE IF NOT EXISTS journey_responses(
        id IDENTITY PRIMARY KEY,
        user_id INTEGER NOT NULL,
        journey_id INTEGER NOT NULL,
        message VARCHAR(1023) NOT NULL,
        date_time TIMESTAMP DEFAULT CURRENT_DATE NOT NULL,
        deleted BOOLEAN DEFAULT FALSE NOT NULL,
        deleted_message VARCHAR(1000),
        FOREIGN KEY(user_id) REFERENCES users ON DELETE CASCADE,
        FOREIGN KEY(journey_id) REFERENCES journeys ON DELETE CASCADE,
    )

    CREATE TABLE IF NOT EXISTS events(
        id IDENTITY PRIMARY KEY,
        user_id INTEGER NOT NULL,
        city_id INTEGER NOT NULL,
        event_date DATE NOT NULL,
        event_time TIME,
        description VARCHAR(2047),
        attendees_limit INTEGER,
        address VARCHAR(255),
        flyer_image_id INTEGER,
        attendees_count INTEGER DEFAULT 0,
        title VARCHAR(255) NOT NULL,
        deleted BOOLEAN DEFAULT FALSE NOT NULL,
        deleted_message VARCHAR(1000),
        FOREIGN KEY(user_id) REFERENCES users ON DELETE RESTRICT,
        FOREIGN KEY(city_id) REFERENCES cities ON DELETE RESTRICT,
        FOREIGN KEY(flyer_image_id) REFERENCES images ON DELETE RESTRICT
    );

    CREATE TABLE IF NOT EXISTS event_responses(
        id IDENTITY PRIMARY KEY,
        user_id INTEGER NOT NULL,
        event_id INTEGER NOT NULL,
        message VARCHAR(1023) NOT NULL,
        date_time TIMESTAMP DEFAULT CURRENT_DATE NOT NULL,
        deleted BOOLEAN DEFAULT FALSE NOT NULL,
        deleted_message VARCHAR(1000),
        FOREIGN KEY(user_id) REFERENCES users ON DELETE CASCADE,
        FOREIGN KEY(event_id) REFERENCES events ON DELETE CASCADE,
    );

    CREATE TABLE IF NOT EXISTS event_attendances(
        user_id INTEGER NOT NULL,
        event_id INTEGER NOT NULL,
        FOREIGN KEY(user_id) REFERENCES users ON DELETE CASCADE,
        FOREIGN KEY(event_id) REFERENCES EVENTS ON DELETE CASCADE,
        PRIMARY KEY(user_id, event_id)
    );

