-- Creates and populates the "cars" table discussed in class

DROP TABLE cars;

CREATE TABLE cars
  (model_year INTEGER, maker VARCHAR(15), model VARCHAR(20), 
  color VARCHAR(10), miles INTEGER);

INSERT INTO cars VALUES
  (2001, 'Honda', 'Accord', 'White', 32628);

INSERT INTO cars VALUES
  (1999, 'Ford', 'Explorer', 'Black', 60152);

INSERT INTO cars VALUES
  (1998, 'Infiniti', 'Q40', 'Silver', 55278);

INSERT INTO cars VALUES
  (2002, 'Ford', 'Mustang', 'Red', 12749);

INSERT INTO cars VALUES
  (1999, 'Volkswagon', 'Jetta', 'Green', 49023);

INSERT INTO cars VALUES
  (2001, 'Honda', 'Civic', 'Red', 27743);

INSERT INTO cars VALUES
  (1993, 'Oldsmobile', 'Cutlass Cierra', 'Green', 64782);

INSERT INTO cars VALUES
  (1999, 'Ford', 'Taurus', 'White', 65320);

INSERT INTO cars VALUES
  (1998, 'Dodge', 'Dakota', 'Black', 73357);

INSERT INTO cars VALUES
  (1999, 'Volkswagon', 'Jetta', 'Blue', 76173);