DROP DATABASE IF EXISTS rooms_system;
CREATE DATABASE rooms_system;
USE rooms_system;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS rooms;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
  user_id INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  email VARCHAR(100) NOT NULL,
  password VARCHAR(100) NOT NULL,
  role ENUM('admin','faculty') NOT NULL,
  deleted_at TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (user_id),
  UNIQUE KEY username_UNIQUE (username),
  UNIQUE KEY email_UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE rooms (
  room_name VARCHAR(45) NOT NULL,
  room_type ENUM('Lecture','Laboratory') NOT NULL,
  working_pcs INT DEFAULT NULL,
  num_chairs INT DEFAULT NUll,
  is_occupied TINYINT DEFAULT 0,
  PRIMARY KEY (room_name),
  UNIQUE KEY room_name_UNIQUE (room_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE bookings (
  booking_id INT NOT NULL AUTO_INCREMENT,
  user_id INT NOT NULL,
  room_name VARCHAR(45) NOT NULL,
  booking_date DATE NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  full_name VARCHAR(100),
  professor_name VARCHAR(100),
  program ENUM('BSIT', 'BSCS'),
  section VARCHAR(50),
  year INT,
  PRIMARY KEY (booking_id)
);

CREATE TABLE inbox_requests (
  request_id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT,
  room_name VARCHAR(45),
  booking_date DATE,
  start_time TIME,
  end_time TIME,
  status ENUM('pending', 'cancelled', 'approved') DEFAULT 'pending',
  full_name VARCHAR(100),
  professor_name VARCHAR(100),
  program ENUM('BSIT', 'BSCS'),
  section VARCHAR(50),
  year INT
);

INSERT INTO users (username, email, password, role) VALUES
('admin1', 'admin1@pup.edu.ph', 'admin123', 'admin'),
('admin2', 'admin2@pup.edu.ph', 'admin123', 'admin'),
('admin3', 'admin3@pup.edu.ph', 'admin123', 'admin'),
('faculty1', 'faculty1@pup.edu.ph', 'faculty123', 'faculty'),
('faculty2', 'faculty2@pup.edu.ph', 'faculty123', 'faculty'),
('faculty3', 'faculty3@pup.edu.ph', 'faculty123', 'faculty');


INSERT INTO rooms (room_name, room_type, working_pcs, num_chairs, is_occupied) VALUES
('S501', 'Laboratory', 30, 55, 0),
('S502', 'Laboratory', 31, 55, 0),
('S503A', 'Laboratory', 32, 55,  0),
('S503B', 'Laboratory', 23, 55, 0),
('S504', 'Laboratory', 25, 55, 0),
('S505', 'Laboratory', 35, 55, 0),
('S508', 'Laboratory', 28, 55, 0),
('S509', 'Laboratory', 36, 55, 0),
('S510', 'Laboratory', 24, 55, 0),
('S511', 'Laboratory', 20, 55, 0),
('S512B', 'Laboratory', 35, 55, 0),
('S513', 'Laboratory', 29, 55, 0),
('S515', 'Laboratory', 28, 55, 0),
('S517', 'Laboratory', 30, 55, 0),
('S518', 'Laboratory', 32, 55, 0),
('W500', 'Lecture', NULL, 55,0),
('W501', 'Lecture', NULL, 55,0),
('W502', 'Lecture', NULL, 55,0),
('W503', 'Lecture', NULL, 55,0),
('W504', 'Lecture', NULL, 55,0),
('W505', 'Lecture', NULL, 55,0),
('W506', 'Lecture', NULL, 55,0),
('W507', 'Lecture', NULL, 55,0),
('W508', 'Lecture', NULL, 55,0),
('W509', 'Lecture', NULL, 55,0),
('W510', 'Lecture', NULL, 55,0),
('W511', 'Lecture', NULL, 55,0),
('W512', 'Lecture', NULL, 55,0),
('W513', 'Lecture', NULL, 55,0),
('W514', 'Lecture', NULL, 55,0),
('W515', 'Lecture', NULL, 55,0),
('W516', 'Lecture', NULL, 55,0),
('W517', 'Lecture', NULL, 55,0),
('W518', 'Lecture', NULL, 55,0),
('E501', 'Lecture', NULL, 55,0),
('E502', 'Lecture', NULL, 55,0),
('E503', 'Lecture', NULL, 55,0),
('E504', 'Lecture', NULL, 55,0),
('E505', 'Lecture', NULL, 55,0),
('E506', 'Lecture', NULL, 55,0),
('E507', 'Lecture', NULL, 55,0),
('E508', 'Lecture', NULL, 55,0),
('E509', 'Lecture', NULL, 55,0),
('E510', 'Lecture', NULL, 55,0),
('E511', 'Lecture', NULL, 55,0),
('E512', 'Lecture', NULL, 55,0),
('E513', 'Lecture', NULL, 55,0),
('E514', 'Lecture', NULL, 55,0),
('E515', 'Lecture', NULL, 55,0),
('E516', 'Lecture', NULL, 55,0),
('E517', 'Lecture', NULL, 55,0),
('E518', 'Lecture', NULL, 55,0),
('N501', 'Lecture', NULL, 55,0),
('N502', 'Lecture', NULL, 55,0),
('N503', 'Lecture', NULL, 55,0),
('N504', 'Lecture', NULL, 55,0),
('N505', 'Lecture', NULL, 55,0),
('N506', 'Lecture', NULL, 55,0),
('N507', 'Lecture', NULL, 55,0),
('N508', 'Lecture', NULL, 55,0),
('N509', 'Lecture', NULL, 55,0),
('N510', 'Lecture', NULL, 55,0),
('N511', 'Lecture', NULL, 55,0),
('N512', 'Lecture', NULL, 55,0),
('N513', 'Lecture', NULL, 55,0),
('N514', 'Lecture', NULL, 55,0),
('N515', 'Lecture', NULL, 55,0),
('N516', 'Lecture', NULL, 55,0),
('N517', 'Lecture', NULL, 55,0),
('N518', 'Lecture', NULL, 55,0);

ALTER TABLE users ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;