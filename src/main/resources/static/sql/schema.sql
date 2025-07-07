drop table if exists MapSectionStateBitmap;
drop table if exists MapSection;
drop table if exists user_steps;
drop table if exists follow;
drop materialized view if exists like_counts;
drop table if exists likes;
drop table if exists Pin;
drop table if exists "User";
drop type if exists user_role;


CREATE TYPE user_role AS ENUM ('user', 'admin');

create table "User" ( -- User table
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(200),
    role user_role,
    provider VARCHAR(20),        -- OAuth 제공자 이름 (google, kakao, naver 등)
    provider_id VARCHAR(100),    -- 제공자별 사용자 고유 ID
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    profile_image TEXT
);

create table MapSection (
    user_id BIGINT, -- user id
    x INT NOT NULL,
    y INT NOT NULL,
    explored BOOLEAN DEFAULT FALSE,
    UNIQUE (user_id, x, y),
    primary key (user_id, x, y),
    foreign key (user_id) references "User"(id)
);

create table MapSectionStateBitmap (
   user_id BIGINT, -- user id
   x INT NOT NULL,
   y INT NOT NULL,
   bitmap BYTEA NOT NULL, -- section bitmap
   primary key (user_id, x, y),
   foreign key (user_id, x, y) references MapSection(user_id, x, y)
);


-- 유저 스텝 추가
create table user_steps (
    id BIGSERIAL PRIMARY KEY ,
    user_id BIGINT NOT NULL,
    step_date DATE NOT NULL,
    step_count INT NOT NULL DEFAULT 0,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp,
    constraint fk_user_steps_user
        foreign key (user_id)
            references "User"(id)
            on delete cascade -- 정보 유지하려면 삭제해도 될 듯
            on update cascade,
    constraint unique_user_step UNIQUE (user_id, step_date)
);

-- Pin 테이블 추가
create table Pin (
     id BIGSERIAL PRIMARY KEY ,
     user_id BIGINT NOT NULL ,
     lat DOUBLE PRECISION ,
     lng DOUBLE PRECISION ,
     title VARCHAR NOT NULL ,
     content VARCHAR NOT NULL ,
     created_at timestamp not null default current_timestamp,
     foreign key (user_Id) references "User"(id)
);

ALTER TABLE Pin DROP COLUMN title;
ALTER TABLE Pin ADD COLUMN image_url VARCHAR;


CREATE TABLE follow (
    follower_id   BIGSERIAL    NOT NULL,   -- 팔로잉 하는 사용자
    followee_id   BIGSERIAL    NOT NULL,   -- 팔로잉 당하는 사용자
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_follow PRIMARY KEY (follower_id, followee_id), -- 주키 설정
    CONSTRAINT chk_follow_self CHECK (follower_id <> followee_id), -- 자기 자신 팔로우 금지
    CONSTRAINT fk_follow_follower FOREIGN KEY (follower_id)
        REFERENCES "User"(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_follow_followee FOREIGN KEY (followee_id)
        REFERENCES "User"(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE likes (
   id BIGSERIAL NOT NULL,
   user_id BIGSERIAL NOT NULL,
   pin_id BIGSERIAL NOT NULL,
   created_at DATE NOT NULL DEFAULT current_timestamp,
   CONSTRAINT fk_user FOREIGN KEY (user_id) references "User"(id),
   CONSTRAINT fk_pin FOREIGN KEY (pin_id) references pin(id),
   CONSTRAINT unique_user_pin UNIQUE (user_id, pin_id)
);

CREATE MATERIALIZED VIEW like_counts AS
SELECT pin_id, COUNT(*) AS like_count
FROM likes
GROUP BY pin_id;


-- 조회 성능을 위한 인덱스
CREATE INDEX idx_follow_follower ON follow (follower_id);
CREATE INDEX idx_follow_followee ON follow (followee_id);
CREATE INDEX idx_likes_pin_id ON likes(pin_id);

-- Pin에 장소, 주소 추가
ALTER TABLE Pin ADD COLUMN placeName VARCHAR(255) DEFAULT 'place name';
ALTER TABLE Pin ADD COLUMN address VARCHAR(255) DEFAULT 'address';

-- 랜드마크 관련
create table Landmark (
  landmarkId BIGSERIAL PRIMARY KEY ,
  lat DOUBLE PRECISION ,
  lng DOUBLE PRECISION ,
  placeName VARCHAR ,
  content VARCHAR ,
  imageUrl VARCHAR ,
  address VARCHAR
);

CREATE TABLE landmark_detection (
    landmark_id   BIGINT  REFERENCES landmark(landmarkId),
    user_id       BIGINT  NOT NULL,
    detected_at   TIMESTAMPTZ DEFAULT now(),
    PRIMARY KEY (landmark_id, user_id)
);

BEGIN;

------------------------------------------------------------
-- 1) 테이블·시퀀스 이름 변경
------------------------------------------------------------
ALTER TABLE user_steps RENAME TO user_walk_distance;
ALTER SEQUENCE user_steps_id_seq RENAME TO user_walk_distance_id_seq;
ALTER TABLE user_walk_distance
    ALTER COLUMN id SET DEFAULT nextval('user_walk_distance_id_seq');

------------------------------------------------------------
-- 2) 컬럼 이름 변경 및 속성 조정
------------------------------------------------------------
ALTER TABLE user_walk_distance RENAME COLUMN step_date  TO distance_date;
ALTER TABLE user_walk_distance RENAME COLUMN step_count TO distance_meter;
ALTER TABLE user_walk_distance ALTER COLUMN distance_meter SET DEFAULT 0;
ALTER TABLE user_walk_distance ALTER COLUMN distance_meter SET NOT NULL;

COMMIT;

CREATE TYPE mission_unit AS ENUM (
    'DISTANCE',
    'COUNT',
    'PERCENTAGE'
    );

CREATE TYPE quest_type as ENUM (
    'DAILY',
    'LANDMARK'
    );

CREATE TABLE Achievement (
                             achievementId BIGINT PRIMARY KEY ,
                             title VARCHAR(20) NOT NULL,
                             checker VARCHAR(20) NOT NULL,
                             unit mission_unit
);

CREATE TABLE Quest (
                       questId BIGINT PRIMARY KEY ,
                       title VARCHAR(20) NOT NULL,
                       checker VARCHAR(20) NOT NULL,
                       goalValue INT,
                       unit mission_unit,
                       questType quest_type
);