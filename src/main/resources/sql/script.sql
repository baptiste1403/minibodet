CREATE TABLE IF NOT EXISTS Label(
   label_id BIGINT AUTO_INCREMENT,
   label_color CHARACTER(7) NOT NULL,
   label_text VARCHAR(20) UNIQUE NOT NULL,
   PRIMARY KEY(label_id)
);

CREATE TABLE IF NOT EXISTS Calendar(
    Id_Calendar BIGINT AUTO_INCREMENT,
    PRIMARY KEY(Id_Calendar)
);

CREATE TABLE IF NOT EXISTS Employee(
   Id_Employee BIGINT AUTO_INCREMENT,
   Id_Calendar BIGINT UNIQUE NOT NULL,
   firstname VARCHAR NOT NULL,
   lastname VARCHAR,
   PRIMARY KEY(Id_Employee),
   FOREIGN KEY(Id_Calendar) REFERENCES Calendar(Id_Calendar)
);

CREATE TABLE IF NOT EXISTS Planning(
   Id_Planning BIGINT AUTO_INCREMENT,
   PRIMARY KEY(Id_Planning)
);

CREATE TABLE IF NOT EXISTS Schedule_planning(
   Id_Schedule_planning BIGINT AUTO_INCREMENT,
   num_day TINYINT,
   total_hours TINYINT NOT NULL,
   night_hours TINYINT NOT NULL,
   Id_Planning BIGINT NOT NULL,
   Id_label BIGINT NOT NULL,
   PRIMARY KEY(Id_Schedule_planning, num_day),
   FOREIGN KEY(Id_Planning) REFERENCES Planning(Id_Planning),
   FOREIGN KEY(Id_label) REFERENCES Label(label_id)
);

ALTER TABLE Schedule_planning
ADD CONSTRAINT IF NOT EXISTS CK_Planning_num_Day_range CHECK (num_day BETWEEN 1 AND 7);

CREATE TABLE IF NOT EXISTS LabelDay(
   Id_day BIGINT UNIQUE NOT NULL AUTO_INCREMENT,
   day_date DATE NOT NULL,
   Id_Calendar BIGINT NOT NULL,
   label_id BIGINT NOT NULL,
   PRIMARY KEY(day_date, Id_Calendar),
   FOREIGN KEY(Id_Calendar) REFERENCES Calendar(Id_Calendar),
   FOREIGN KEY(label_id) REFERENCES Label(label_id)
);

CREATE TABLE IF NOT EXISTS WorkDay(
   id_work_day BIGINT,
   commentary CHARACTER LARGE OBJECT,
   illness_info CHARACTER LARGE OBJECT,
   total_hours TINYINT NOT NULL,
   night_hours TINYINT NOT NULL,
   PRIMARY KEY(id_work_day),
   FOREIGN KEY(id_work_day) REFERENCES LabelDay(Id_day)
);

CREATE TABLE IF NOT EXISTS utilise(
   Id_Employee BIGINT,
   start_sate DATE,
   end_date DATE,
   Id_Planning BIGINT NOT NULL,
   PRIMARY KEY(Id_Employee, start_sate),
   FOREIGN KEY(Id_Employee) REFERENCES Employee(Id_Employee),
   FOREIGN KEY(Id_Planning) REFERENCES Planning(Id_Planning)
);
