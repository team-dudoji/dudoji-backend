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
create table user_walk_distance (
    id BIGSERIAL PRIMARY KEY ,
    user_id BIGINT NOT NULL,
    distance_date DATE NOT NULL,
    distance_count INT NOT NULL DEFAULT 0,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp not null default current_timestamp,
    constraint fk_user_walk_distance_user
        foreign key (user_id)
            references "User"(id)
            on delete cascade -- 정보 유지하려면 삭제해도 될 듯
            on update cascade,
    constraint unique_user_walk_distance UNIQUE (user_id, distance_date)
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

CREATE TABLE PinSkins (
    skinId BIGINT PRIMARY KEY,
    name VARCHAR NOT NULL UNIQUE,
    content VARCHAR,
    imageUrl VARCHAR NOT NULL,
    price BIGINT
);

CREATE TABLE UserPinSkins (
    skinId BIGINT REFERENCES PinSkins(skinId),
    userId BIGINT REFERENCES "User"(id),
    PRIMARY KEY (skinId, userId)
);

ALTER TABLE PinSkins
    ALTER COLUMN skinId
        ADD GENERATED ALWAYS AS IDENTITY;

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
                             achievementId BIGSERIAL PRIMARY KEY ,
                             title VARCHAR(20) NOT NULL,
                             checker VARCHAR(30) NOT NULL,
                             unit mission_unit
);

CREATE TABLE Quest (
                       questId BIGSERIAL PRIMARY KEY ,
                       title VARCHAR(20) NOT NULL,
                       checker VARCHAR(20) NOT NULL,
                       goalValue INT,
                       unit mission_unit,
                       questType quest_type
);

-- follow table
ALTER TABLE follow RENAME COLUMN follower_id TO followerId;
ALTER TABLE follow RENAME COLUMN followee_id TO followeeId;

-- userWalkDistance table
ALTER TABLE user_walk_distance RENAME TO userWalkDistance;

ALTER TABLE userWalkDistance RENAME COLUMN user_id TO userId;
ALTER TABLE userWalkDistance RENAME COLUMN distance_date TO distanceDate;
ALTER TABLE userWalkDistance RENAME COLUMN distance_count TO distanceMeter;
ALTER TABLE userWalkDistance RENAME COLUMN created_at TO createdAt;
ALTER TABLE userWalkDistance RENAME COLUMN updated_at TO updatedAt;

-- Pin table
ALTER TABLE Pin RENAME COLUMN user_id TO userId;
ALTER TABLE Pin RENAME COLUMN created_at TO createdAt;
ALTER TABLE Pin RENAME COLUMN image_url TO imageUrl;

-- likes table
ALTER TABLE likes RENAME COLUMN user_id TO userId;
ALTER TABLE likes RENAME COLUMN pin_id TO pinId;
ALTER TABLE likes RENAME COLUMN created_at TO createdAt;
ALTER MATERIALIZED VIEW like_counts RENAME TO likeCounts;
ALTER MATERIALIZED VIEW likeCounts RENAME COLUMN pin_id TO pinId;
ALTER MATERIALIZED VIEW likeCounts RENAME COLUMN like_count TO likeCount;

-- landmark detection
ALTER TABLE landmark_detection RENAME TO landmarkDetection;
ALTER TABLE landmarkDetection RENAME COLUMN landmark_id TO landmarkId;
ALTER TABLE landmarkDetection RENAME COLUMN user_id TO userId;
ALTER TABLE landmarkDetection RENAME COLUMN detected_at TO detectedAt;

-- User
ALTER TABLE "User" RENAME COLUMN provider_id TO providerId;
ALTER TABLE "User" RENAME COLUMN profile_image TO profileImage;
ALTER TABLE "User" RENAME COLUMN created_at TO createdAt;

-- MapSection
ALTER TABLE MapSection RENAME COLUMN user_id TO userId;
ALTER TABLE MapSectionStateBitmap RENAME COLUMN user_id TO userId;
