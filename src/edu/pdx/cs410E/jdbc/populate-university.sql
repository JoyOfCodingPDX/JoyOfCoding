-- This script populates a Cloudscape database with information that
-- models a university's registration

DROP TABLE permits;
DROP TABLE students;
DROP TABLE professors;
DROP TABLE locations;
DROP TABLE sections;
DROP TABLE enrollment;

CREATE TABLE permits 
  (number INTEGER CONSTRAINT pk_permits PRIMARY KEY, 
   expiration DATE);



CREATE TABLE students 
  (ssn CHAR(11) CONSTRAINT pk_students PRIMARY KEY, 
   name VARCHAR(30), permit INTEGER, 
   CONSTRAINT fk_permit FOREIGN KEY (permit) 
     REFERENCES permits(number));
INSERT INTO students VALUES
  ('153-62-1728', 'Robert Johnson', 3681);
INSERT INTO students VALUES
  ('371-27-2872', 'Sunil Sharma', 1792);
INSERT INTO students (ssn, name) VALUES
  ('283-39-1952', 'Wen Zhao');


CREATE TABLE professors
  (id INTEGER CONSTRAINT pk_professors PRIMARY KEY, 
   name VARCHAR(30), started DATE);
INSERT INTO professors VALUES
  (324, 'Hermann Schroeder', 'August 15, 1974');
INSERT INTO professors VALUES
  (562, 'Eliza Goldstein', 'January 1, 1987');
INSERT INTO professors VALUES
  (278, 'Janet Deblasio', 'June 1, 1992');


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
  (4561, 562, 245, '11:45 AM');
INSERT INTO sections VALUES
  (6721, 324, 236, '4:25 PM');
INSERT INTO sections VALUES
  (1620, 278, 245, '9:00 AM');


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