-- This script populates a Cloudscape database with information that
-- models a university's registration

-- Have to drop tables in the correct order (the opposite order in
-- which they were created) so that all of the referential integrity
-- constraints are obeyed

DROP TABLE enrollment;
DROP TABLE sections;
DROP TABLE locations;
DROP TABLE professors;
DROP TABLE students;
DROP TABLE permits;

CREATE TABLE permits 
  (number INTEGER CONSTRAINT pk_permits PRIMARY KEY, 
   expiration DATE);
INSERT INTO permits VALUES
  (1621, '2002-08-30');
INSERT INTO permits VALUES
  (3681, '2002-10-31');
INSERT INTO permits VALUES
  (8528, '2003-03-31');
INSERT INTO permits VALUES
  (1792, '2002-08-30');


CREATE TABLE students 
  (ssn CHAR(11) CONSTRAINT pk_students PRIMARY KEY, 
   name VARCHAR(30), permit INTEGER, 
   CONSTRAINT fk_permit FOREIGN KEY (permit) 
     REFERENCES permits(number));
INSERT INTO students VALUES
  ('152-62-1728', 'Robert Johnson', 3681);
INSERT INTO students VALUES
  ('371-27-2872', 'Sunil Sharma', 8528);
INSERT INTO students (ssn, name) VALUES
  ('283-39-1952', 'Wen Zhao');


CREATE TABLE professors
  (id INTEGER CONSTRAINT pk_professors PRIMARY KEY, 
   name VARCHAR(30), started DATE);
INSERT INTO professors VALUES
  (324, 'Hermann Schroeder', '1974-08-15');
INSERT INTO professors VALUES
  (562, 'Eliza Goldstein', '1987-01-01');
INSERT INTO professors VALUES
  (278, 'Janet Deblasio', '1992-06-01');


CREATE TABLE locations 
  (id INTEGER CONSTRAINT pk_locations PRIMARY KEY,
   building VARCHAR(20), room INTEGER);
INSERT INTO locations VALUES
  (236, 'Science 4', 156);
INSERT INTO locations VALUES
  (245, 'Psychology', 274);
INSERT INTO locations VALUES
  (295, 'Calvin Hall', 168);


CREATE TABLE sections 
  (crn INTEGER CONSTRAINT pk_sections PRIMARY KEY, 
   professor INTEGER, location INTEGER, start TIME, 
   CONSTRAINT fk_sections FOREIGN KEY (professor) 
     REFERENCES professors(id));
INSERT INTO sections VALUES
  (4561, 562, 245, '11:45:00');
INSERT INTO sections VALUES
  (6721, 324, 236, '16:25:00');
INSERT INTO sections VALUES
  (1620, 278, 245, '9:00:00');


CREATE TABLE enrollment 
  (student CHAR(11), section INTEGER, 
   CONSTRAINT fk_enrollment FOREIGN KEY (student)
   REFERENCES students(ssn));
INSERT INTO enrollment VALUES
  ('152-62-1728', 4561);
INSERT INTO enrollment VALUES
  ('371-27-2872', 4561);
INSERT INTO enrollment VALUES
  ('152-62-1728', 1620);