-- 테스트 데이터 코드
insert into "User" (name, email, role)
values
    (
        'dudoji', 'example@example.com', 'admin'
    );

INSERT INTO public."User" (
    id, name, email, password, role, provider, providerId, createdAt, profileImage
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


INSERT INTO follow (followerId, followeeId, createdAt) VALUES
    (100, 101, now() - '5 days'::interval), -- 테스트1 -> 테스트2 (맞팔 관계)
    (100, 102, now() - '4 days'::interval), -- 테스트1 -> 테스트3
    (100, 103, now() - '3 days'::interval), -- 테스트1 -> 테스트4 (맞팔 관계)
    (100, 104, now() - '2 days'::interval), -- 테스트1 -> 테스트5
    (100, 105, now() - '1 day'::interval),  -- 테스트1 -> 테스트6 (가장 최근에 팔로우)

    (101, 100, now() - '4 days'::interval), -- 테스트2 -> 테스트1
    (103, 100, now() - '1 day'::interval),  -- 테스트4 -> 테스트1 (가장 최근에 맞팔)

    (106, 100, now()); -- 테스트7 -> 테스트1 (결과에 포함되면 안됨

INSERT INTO Landmark(landmarkId, lat, lng, placeName, content, mapImageUrl, address)
VALUES
    (1, 0, 0, 'test', 'test', 'test', 'test');

INSERT INTO Achievement(title, checker, unit) VALUES
    ('안개 제거', 'FogAchievement', 'PERCENTAGE'),
    ('총 이동거리', 'TotalMovementAchievement', 'DISTANCE'),
    ('랜드마크', 'LandmarkAchievement', 'COUNT');

INSERT INTO Quest(title, checker, goalValue, unit, questType) VALUES
    ('오늘 하루도 힘차게!', 'DailyMovementQuest', 5, 'DISTANCE', 'DAILY'),
    ('핀을 꽂아보자!', 'DailyPinQuest', 5, 'COUNT', 'DAILY'),
    ('새로운 랜드마크를 방문하자!', 'NewLandmarkQuest', 5, 'DISTANCE', 'LANDMARK'),
    ('모든 랜드마크를 방문하자!', 'LandmarkCountQuest', 5, 'COUNT', 'LANDMARK');

INSERT INTO Region(regionId, name) VALUES
    (1, '부산광역시'),
    (2, '서울특별시');
