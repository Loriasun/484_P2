CREATE TABLE users (
    user_id number not null,
    first_name varchar2(100) not null,
    last_name varchar2(100) not null,
    year_of_birth integer,
    month_of_birth integer,
    day_of_birth INTEGER,
    gender varchar2(100),
    primary key (user_id) 
);

CREATE TABLE friends (
    user1_id number not null,
    user2_id number not null,
    foreign key (user1_id) references users(user_id),
    foreign key (user2_id) references users(user_id),
    primary key (user1_id, user2_id)
);

CREATE TRIGGER order_friends_pairs
     BEFORE INSERT ON FRIENDS
     FOR EACH ROW
            DECLARE temp NUMBER;
            BEGIN
                IF: NEW.USER1_ID > :NEW.USER2_ID THEN
                      temp := :NEW.USER2_ID;
                      :NEW.USER2_ID := :NEW.USER1_ID;
                      :NEW.USER1_ID := temp;
                END IF ;
        END;
/

CREATE TABLE  cities (
    city_id integer not null,
    city_name varchar2(100) not null,
    state_name varchar2(100) not null,
    country_name varchar2(100) not null,
    unique(city_name,state_name,country_name),
    PRIMARY KEY (city_id)
);


CREATE TABLE user_current_cities(
    user_id number not null,
    current_city_id integer not null,
    foreign key (current_city_id) references cities(city_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    primary key(user_id) 
);

CREATE TABLE user_hometown_cities(
    user_id NUMBER NOT NULL,
    hometown_city_id INTEGER NOT NULL,
    PRIMARY KEY(user_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (hometown_city_id) REFERENCES cities(city_id)
);



CREATE TABLE messages(
    message_id NUMBER NOT NULL,
    receiver_id NUMBER NOT NULL,
    sender_id NUMBER NOT NULL,
    message_content VARCHAR2(2000) NOT NULL,
    sent_time TIMESTAMP NOT NULL,
    PRIMARY KEY(message_id),
    FOREIGN KEY (sender_id) REFERENCES users(user_id),
    FOREIGN KEY (receiver_id) REFERENCES users(user_id)
);

CREATE TABLE programs(
    program_id integer not null,
    institution varchar2(100) not null,
    concentration varchar2(100) not null,
    degree varchar2(100),
    UNIQUE (institution,concentration,degree),
    --foreign key(user_id) references users
    PRIMARY KEY (program_id)
);


CREATE TABLE education(
    user_id number not null,
    program_id integer not null,
    program_year integer not null,
    primary key (user_id, program_id),
    foreign key(user_id) references users(user_id),
    foreign key(program_id) references programs(program_id)
);

CREATE TABLE user_events (
    event_id number not null,
    event_creator_id number not null,
    event_name varchar2(100) not null,
    event_tagline varchar2(100),
    event_description varchar2(100),
    event_host varchar2(100),
    event_type varchar2(100),
    event_subtype varchar2(100),
    event_address varchar2(2000),
    event_city_id integer not null,
    event_start_time timestamp,
    event_end_time timestamp,
    foreign key (event_creator_id) references users(user_id),
    foreign key (event_city_id) references cities(city_id),
    primary key (event_id)
);

CREATE TABLE participants(
    event_id NUMBER NOT NULL,
    user_id NUMBER NOT NULL,
    comfirmation VARCHAR2(100) NOT NULL,
    CHECK(comfirmation='ATTENDING' or comfirmation = 'UNSURE' or comfirmation = 'DECLINES' or comfirmation = 'NOT_REPLIED'),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    foreign key (event_id) references user_events(event_id),
    PRIMARY KEY (event_id, user_id)
);

CREATE TABLE albums (
    album_id NUMBER NOT NULL,
    album_owner_id NUMBER NOT NULL,
    album_name VARCHAR2(100) NOT NULL,
    album_created_time TIMESTAMP NOT NULL,
    album_modified_time TIMESTAMP,
    album_link VARCHAR2(100) NOT NULL,
    album_visibility VARCHAR2(100) NOT NULL,
    CHECK( album_visibility = 'EVERYONE'or album_visibility = 'FRIENDS' 
            or album_visibility = 'FRIENDS_OF_FRIENDS' or album_visibility = 'MYSELF'),
    cover_photo_id NUMBER NOT NULL,
    PRIMARY KEY (album_id),
    FOREIGN KEY (album_owner_id) REFERENCES users(user_id)
);


CREATE TABLE photos (
    photo_id NUMBER NOT NULL,
    album_id NUMBER NOT NULL,
    photo_caption VARCHAR2(2000),
    photo_created_time TIMESTAMP NOT NULL,
    photo_modified_time TIMESTAMP,
    photo_link VARCHAR2(2000) NOT NULL,
    PRIMARY KEY (photo_id)
);

ALTER TABLE albums 
ADD CONSTRAINT album_cover 
FOREIGN KEY(cover_photo_id) REFERENCES photos(photo_id) ON DELETE CASCADE
initially deferred deferrable;

ALTER TABLE photos 
ADD CONSTRAINT album
FOREIGN KEY(album_id) REFERENCES albums(album_id) ON DELETE CASCADE
initially deferred deferrable;

CREATE TABLE tags (
    tag_photo_id number NOT NULL,
    tag_subject_id number NOT NULL,
    tag_created_time TIMESTAMP NOT NULL,
    tag_x NUMBER NOT NULL,
    tag_y NUMBER NOT NULL,
    foreign key (tag_photo_id) references photos(photo_id),
    foreign key (tag_subject_id) references users(user_id),
    PRIMARY KEY (tag_photo_id,tag_subject_id)
);

CREATE SEQUENCE cities_seq
START WITH 1
INCREMENT BY 1;

CREATE TRIGGER cities_trigger
     BEFORE INSERT ON cities
     FOR EACH ROW
     BEGIN
       SELECT cities.NEXTVAL INTO :NEW.city_id FROM DUAL;
     END;
/

CREATE SEQUENCE programs_seq
START WITH 1
INCREMENT BY 1;

CREATE TRIGGER programs_trigger
     BEFORE INSERT ON programs
     FOR EACH ROW
     BEGIN
       SELECT programs.NEXTVAL INTO :NEW.programs_id FROM DUAL;
     END;
/