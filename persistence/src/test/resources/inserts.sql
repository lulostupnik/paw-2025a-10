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