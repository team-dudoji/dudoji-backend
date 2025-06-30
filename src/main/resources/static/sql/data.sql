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