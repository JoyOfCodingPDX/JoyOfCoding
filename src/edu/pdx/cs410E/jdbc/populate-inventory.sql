-- This SQL script populates a Cloudscape database with data for the
-- refactored cars example

DROP TABLE models;
DROP TABLE inventory;

CREATE TABLE models (vid INTEGER not null, model_year INTEGER, 
  maker VARCHAR(15), model VARCHAR(20), PRIMARY KEY (vid));

INSERT INTO models VALUES (1, 1999, 'Ford', 'Explorer');
INSERT INTO models VALUES (2, 1998, 'Infiniti', 'Q40');
INSERT INTO models VALUES (3, 2002, 'Ford', 'Mustang');
INSERT INTO models VALUES (4, 1999, 'Volkswagon', 'Jetta');

CREATE TABLE inventory (vid INTEGER not null, 
  color VARCHAR(10), miles INTEGER, price FLOAT,
  FOREIGN KEY (vid) REFERENCES models (vid));

INSERT INTO inventory VALUES (1, 'Black', 60152, 35184.17);
INSERT INTO inventory VALUES (2, 'Silver', 55278, 47135.84);
INSERT INTO inventory VALUES (3, 'Red', 12749, 32271.51);
INSERT INTO inventory VALUES (4, 'Green', 49023, 23317.38);
INSERT INTO inventory VALUES (4, 'Blue', 76173, 21729.05);