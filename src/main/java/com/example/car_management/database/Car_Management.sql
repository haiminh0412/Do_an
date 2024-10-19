use do_an_tn_v3;

create table Customer
(
    Customer_ID int not null auto_increment,
    name nvarchar(225) not null,
    phone nvarchar(225) not null,
    email nvarchar(225) not null,
    CONSTRAINT PK_Customer_ID PRIMARY KEY (Customer_ID)
);

create table CarType
(
    CarType_ID int not null auto_increment,
    name nvarchar(225) not null,
    created_at datetime not null default now(),
    updated_at datetime not null default now() on update CURRENT_TIMESTAMP,
    constraint PK_CarType_ID primary key (CarType_ID)
);

create table Car
(
    Car_ID int not null auto_increment,
    CarType_ID int not null,
    image nvarchar(225) null,
    seats int not null default 4,
    license_plate nvarchar(225) not null,
    status nvarchar(225) not null default N'Hoạt động',
    created_at datetime not null default now(),
    updated_at datetime not null default now() on update CURRENT_TIMESTAMP,
    constraint PK_Car_ID primary key (Car_ID),
    CONSTRAINT FK_Car_CarType_ID foreign key(CarType_ID) references CarType(CarType_ID) ON DELETE CASCADE
);

create table Seat
(
    Seat_ID int not null auto_increment,
    Car_ID int not null,
    Seat_number nvarchar(10) not null,
    created_at datetime not null default now(),
    updated_at datetime not null default now() on update CURRENT_TIMESTAMP,
    constraint PK_Seat_ID primary key (Seat_ID),
    constraint FK_Seat_Car_ID foreign key(Car_ID) references Car(Car_ID) ON DELETE CASCADE
);

create table Trip
(
    Trip_ID int not null auto_increment,
    departure nvarchar(225) not null,
    destination nvarchar(225) not null,
    created_at datetime not null default now(),
    updated_at datetime not null default now() on update CURRENT_TIMESTAMP,
    constraint PK_Trip_ID primary key (Trip_ID)
);

create table Trip_Detail
(
    Trip_Detail_ID int not null auto_increment,
    Trip_ID int not null,
    Car_ID int not null,
    price long not null,
    departure_time time not null,
    destination_time time not null,
    created_at datetime not null default now(),
    updated_at datetime not null default now() on update CURRENT_TIMESTAMP,
    constraint PK_Trip_Detail_ID primary key (Trip_Detail_ID),
    constraint FK_Trip_Detail_Car_ID foreign key(Car_ID) references Car(Car_ID) ON DELETE CASCADE,
    constraint FK_Trip_Detail_Trip_ID foreign key(Trip_ID) references Trip(Trip_ID) ON DELETE CASCADE
);

create table Booking
(
    Booking_ID int not null auto_increment,
    Trip_Detail_ID int not null,
    Customer_ID int not null,
    start_destination nvarchar(225) not null,
    end_destination nvarchar(225) not null,
    departure_date date not null,
    booking_at datetime not null default now(),
    update_booking_at datetime not null default now() on update CURRENT_TIMESTAMP,
    status nvarchar(225) default N'Pending',
    constraint PK_Booking_ID primary key (Booking_ID),
    constraint FK_Booking_Customer_ID foreign key (Customer_ID) references Customer(Customer_ID) ON DELETE CASCADE,
    constraint FK_Booking_Trip_Detail_ID foreign key (Trip_Detail_ID) references Trip_Detail(Trip_Detail_ID) ON DELETE CASCADE
);

create table Booking_Seat
(
    Booking_Seat_ID int not null auto_increment,
    Booking_ID int not null,
    Seat_ID int not null,
    constraint PK_Booking_Seat_ID primary key (Booking_Seat_ID),
    constraint FK_Booking_Seat_Booking_ID foreign key (Booking_ID) references Booking(Booking_ID) ON DELETE CASCADE,
    constraint FK_Booking_Seat_Seat_ID foreign key (Seat_ID) references Seat(Seat_ID) ON DELETE CASCADE
);

create table Payment
(
    Payment_ID int not null auto_increment,
    Booking_ID int not null,
    Payment_Method nvarchar(225) not null,
    payement_at datetime not null default now(),
    constraint PK_Payment_ID primary key (Payment_ID),
    constraint FK_Payment_Booking_ID foreign key (Booking_ID) references Booking(Booking_ID) ON DELETE CASCADE
);

CREATE TABLE Notification (
      Notification_ID INT NOT NULL AUTO_INCREMENT,
      Booking_ID INT NOT NULL,
      Sent_At DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
      Type VARCHAR(50) NOT NULL,
      CONSTRAINT PK_Notification PRIMARY KEY (Notification_ID),
      CONSTRAINT FK_Notification_Booking_ID FOREIGN KEY (Booking_ID) REFERENCES Booking(Booking_ID) ON DELETE CASCADE
);

CREATE TABLE Verification_Code (
       id INT PRIMARY KEY AUTO_INCREMENT,
       code VARCHAR(7) NOT NULL,
       type VARCHAR(50) NOT NULL,
       booking_id INT NOT NULL,
       sent_at DATETIME DEFAULT CURRENT_TIMESTAMP,
       CONSTRAINT FK_Verification_Code_Booking_ID FOREIGN KEY (booking_id) REFERENCES Booking(booking_id) ON DELETE CASCADE
);


CREATE TABLE promotion (
       id BIGINT AUTO_INCREMENT PRIMARY KEY,
       code VARCHAR(50) NOT NULL,
       description VARCHAR(255),
       discount_percentage DECIMAL(5, 2) NOT NULL,
       start_date DATE NOT NULL,
       end_date DATE NOT NULL,
       usage_limit INT DEFAULT 0,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE SeatHold (
      id INT AUTO_INCREMENT PRIMARY KEY,
      seat_id INT NOT NULL,
      session_id VARCHAR(255) NOT NULL,
      trip_detail_id INT NOT NULL,
      hold_start TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      departure_date DATE NOT NULL,
      CONSTRAINT FK_SeatHold_Seat FOREIGN KEY (seat_id) REFERENCES Seat(Seat_ID) ON DELETE CASCADE,
      CONSTRAINT FK_SeatHold_TripDetail FOREIGN KEY (trip_detail_id) REFERENCES Trip_Detail(Trip_Detail_ID) ON DELETE CASCADE
);

CREATE TABLE History_Booking (
         History_ID INT NOT NULL AUTO_INCREMENT,
         Booking_ID INT NOT NULL,
         total_price int not null,
         CONSTRAINT PK_History_Booking PRIMARY KEY (History_ID),
         CONSTRAINT FK_History_Booking_ID FOREIGN KEY (Booking_ID) REFERENCES Booking(Booking_ID) ON DELETE CASCADE
);
