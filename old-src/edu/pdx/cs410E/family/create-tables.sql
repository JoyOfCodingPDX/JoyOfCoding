-- SQL Script that creates the relational database tables to store the
-- family tree data

-- The people table

DROP TABLE marriages;
DROP TABLE people;

CREATE TABLE people
  (id INTEGER CONSTRAINT pk_people PRIMARY KEY,
   gender INTEGER CONSTRAINT valid_gender CHECK (gender IN(1, 2)),
   first_name VARCHAR(20),
   middle_name VARCHAR(20),
   last_name VARCHAR(20),
   father INTEGER CONSTRAINT fk_father REFERENCES people (id),
   mother INTEGER CONSTRAINT fk_mother REFERENCES people (id),
   dob DATE, dod DATE);

INSERT INTO people (id, gender, first_name) 
  VALUES (1, 2, 'Dave');

INSERT INTO people (id, gender, first_name) 
  VALUES (2, 2, 'Stan');

INSERT INTO people (id, gender, first_name) 
  VALUES (3, 1, 'Stan');

UPDATE people SET father = 2 WHERE id = 1;

CREATE TABLE marriages
  (husband INTEGER CONSTRAINT pk_husband REFERENCES people (id),
   wife INTEGER CONSTRAINT pk_wife REFERENCES people (id),
   anniversary DATE, location VARCHAR(30));

INSERT INTO marriages (husband, wife, anniversary, location)
  VALUES (2, 3, '1969-07-12', 'Durham, NH');