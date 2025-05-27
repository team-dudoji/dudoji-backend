drop table if exists MapSectionStateBitmap;
drop table if exists MapSection;
drop table if exists user_steps;
drop table if exists Pin;
drop table if exists follow;
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


-- 테스트 데이터 코드

insert into "User" (name, email, role)
values
    (
        'dudoji', 'example@example.com', 'admin'
    );

INSERT INTO public."User" (
    id, name, email, password, role, provider, provider_id, created_at, profile_image
) VALUES
      (100, '테스트1', '1@1',   NULL, 'user'::user_role, NULL, NULL, DEFAULT, NULL),
      (101, '테스트2', '2@2',   NULL, 'user'::user_role, NULL, NULL, DEFAULT, NULL),
      (102, '테스트3', '3@3',   NULL, 'user'::user_role, NULL, NULL, DEFAULT, NULL),
      (103, '테스트4', '4@4',   NULL, 'user'::user_role, NULL, NULL, DEFAULT, NULL),
      (104, '테스트5', '5@5',   NULL, 'user'::user_role, NULL, NULL, DEFAULT, NULL),
      (105, '테스트6', '6@6',   NULL, 'user'::user_role, NULL, NULL, DEFAULT, NULL),
      (106, '테스트7', '7@7',   NULL, 'user'::user_role, NULL, NULL, DEFAULT, NULL),
      (107, '테스트8', '8@8',   NULL, 'user'::user_role, NULL, NULL, DEFAULT, NULL),
      (108, '테스트9', '9@9',   NULL, 'user'::user_role, NULL, NULL, DEFAULT, NULL),
      (109, '테스트10','10@10', NULL, 'user'::user_role, NULL, NULL, DEFAULT, NULL);