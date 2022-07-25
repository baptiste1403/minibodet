CREATE TABLE IF NOT EXISTS Calendar(
                         Id_Calendar INT AUTO_INCREMENT,
                         PRIMARY KEY(Id_Calendar)
);

CREATE TABLE IF NOT EXISTS  Label(
                      Id_Label INT AUTO_INCREMENT,
                      text VARCHAR(50)  NOT NULL,
                      color CHAR(7)  NOT NULL,
                      PRIMARY KEY(Id_Label)
);

CREATE TABLE IF NOT EXISTS  Employee(
                         Id_Employee INT AUTO_INCREMENT,
                         lastname VARCHAR(255)  NOT NULL,
                         firstname VARCHAR(255)  NOT NULL,
                         archived BOOLEAN NOT NULL DEFAULT FALSE,
                         Id_Calendar INT NOT NULL,
                         PRIMARY KEY(Id_Employee),
                         UNIQUE(Id_Calendar),
                         FOREIGN KEY(Id_Calendar) REFERENCES Calendar(Id_Calendar)
);

CREATE TABLE IF NOT EXISTS  LabelDay(
                         Id_LabelDay INT AUTO_INCREMENT,
                         day_date DATE NOT NULL,
                         Id_Label INT NOT NULL,
                         Id_Calendar INT NOT NULL,
                         PRIMARY KEY(Id_LabelDay),
                         FOREIGN KEY(Id_Label) REFERENCES Label(Id_Label),
                         FOREIGN KEY(Id_Calendar) REFERENCES Calendar(Id_Calendar) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS  WorkDay(
                        Id_LabelDay INT,
                        comment VARCHAR(255)  NOT NULL,
                        total_hours DOUBLE NOT NULL,
                        night_hours DOUBLE NOT NULL,
                        PRIMARY KEY(Id_LabelDay),
                        FOREIGN KEY(Id_LabelDay) REFERENCES LabelDay(Id_LabelDay) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS   Schedule(
                         Id_Schedule INT AUTO_INCREMENT,
                         total_hours DOUBLE NOT NULL,
                         night_hours DOUBLE NOT NULL,
                         num_day TINYINT NOT NULL,
                         Id_Label INT NOT NULL,
                         Id_Employee INT NOT NULL,
                         PRIMARY KEY(Id_Schedule),
                         FOREIGN KEY(Id_Label) REFERENCES Label(Id_Label),
                         FOREIGN KEY(Id_Employee) REFERENCES Employee(Id_Employee) ON DELETE CASCADE
);
