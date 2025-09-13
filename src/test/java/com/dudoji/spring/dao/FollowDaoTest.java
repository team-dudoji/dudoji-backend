package com.dudoji.spring.dao;

import com.dudoji.spring.dao.DBtest.DBTestBase;
import com.dudoji.spring.dto.user.UserSimpleDto;
import com.dudoji.spring.models.dao.FollowDao;

import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@JdbcTest
@Import({FollowDao.class}) // 의존하는 클래스들을 모두 Import
@TestPropertySource(properties = "SLACK_WEBHOOK_URL=http://test-slack-url.com")
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // @BeforeAll을 non-static으로 사용하기 위함
class FollowDaoTest extends DBTestBase {

    @Autowired
    FollowDao followDao;

    // 모든 테스트의 기준이 되는 사용자 ID
    private static final long TEST_USER_ID = 101L;

    @BeforeAll
    void setUp() {
        System.out.println("===== 테스트 데이터 초기화 (한 번만 실행) =====");

        // --- 101번 유저의 팔로잉 관계 설정 ---
        followDao.createFollowingByUser(101, 105); // 101 -> 105 (단방향, 가장 최근)
        followDao.createFollowingByUser(101, 106); // 101 -> 106 (단방향)
        followDao.createFollowingWithSelectingDay(101, 104, LocalDate.now().minusDays(10L)); // 맞팔
        followDao.createFollowingWithSelectingDay(101, 103, LocalDate.now().minusDays(20L)); // 맞팔
        followDao.createFollowingByUser(101, 102); // 맞팔 (가장 오래전)

        // --- 101번 유저를 팔로우하는 관계 설정 (맞팔 관계 완성) ---
        // '맞팔순 정렬' 테스트를 위해 맞팔 시점을 다르게 설정
        followDao.createFollowingByUser(102, 101); // 102가 101을 가장 최근에 맞팔
        followDao.createFollowingWithSelectingDay(103, 101, LocalDate.now().minusDays(5L));
        followDao.createFollowingWithSelectingDay(104, 101, LocalDate.now().minusDays(15L));
    }

    // =================================================================
    //                    팔로잉(Following) 목록 테스트
    // =================================================================
    @Nested
    @DisplayName("팔로잉(Following) 목록 조회 테스트")
    class FollowingTests {

        @Test
        @DisplayName("기본 조회 (최신 팔로우순)")
        void getFollowingList_default() {
            Sort sort = Sort.by(Sort.Direction.DESC, "followingAt");
            List<UserSimpleDto> result = followDao.getUsers(TEST_USER_ID, 0, 10, sort, null, "FOLLOWING");
            printResult("팔로잉 목록: 기본 조회 (ID:105가 가장 먼저 나와야 함)", result);
        }

        @Test
        @DisplayName("키워드 '3'으로 이름 검색")
        void getFollowingList_withKeyword() {
            Sort sort = Sort.by(Sort.Direction.DESC, "followingAt");
            String keyword = "3"; // '테스트3'(ID:103) 검색
            List<UserSimpleDto> result = followDao.getUsers(TEST_USER_ID, 0, 10, sort, keyword, "FOLLOWING");
            printResult("팔로잉 목록: 키워드 '3' 검색 (ID:103 한 명만 나와야 함)", result);
        }

        @Test
        @DisplayName("페이지네이션 (2번째 페이지, 2개씩)")
        void getFollowingList_withPagination() {
            Sort sort = Sort.by(Sort.Direction.DESC, "followingAt");
            // 전체 순서(followingAt DESC): 105, 106, [104, 103], 102
            List<UserSimpleDto> result = followDao.getUsers(TEST_USER_ID, 2, 2, sort, null, "FOLLOWING");
            printResult("팔로잉 목록: 페이지네이션 (ID:104, 103이 나와야 함)", result);
        }

        @Test
        @DisplayName("이름순 정렬 (오름차순)")
        void getFollowingList_sortByName() {
            Sort sort = Sort.by(Sort.Direction.ASC, "name");
            List<UserSimpleDto> result = followDao.getUsers(TEST_USER_ID, 0, 10, sort, null, "FOLLOWING");
            printResult("팔로잉 목록: 이름순 정렬 (ID:102가 가장 먼저 나와야 함)", result);
        }

        @Test
        @DisplayName("맞팔로우순 정렬 (맞팔이 위로, 최신 맞팔순)")
        void getFollowingList_sortByMutual() {
            Sort sort = Sort.by(Sort.Direction.DESC, "mutual");
            List<UserSimpleDto> result = followDao.getUsers(TEST_USER_ID, 0, 10, sort, null, "FOLLOWING");
            printResult("팔로잉 목록: 맞팔순 정렬 (ID:102, 103, 104 순으로 먼저 나와야 함)", result);
        }
    }

    // =================================================================
    //                    팔로워(Follower) 목록 테스트
    // =================================================================
    @Nested
    @DisplayName("팔로워(Follower) 목록 조회 테스트")
    class FollowerTests {

        @Test
        @DisplayName("기본 조회 (최신 팔로우순)")
        void getFollowerList_default() {
            Sort sort = Sort.by(Sort.Direction.DESC, "followingAt");
            List<UserSimpleDto> result = followDao.getUsers(TEST_USER_ID, 0, 10, sort, null, "FOLLOWER");
            printResult("팔로워 목록: 기본 조회 (ID:102가 가장 먼저 나와야 함)", result);
        }

        @Test
        @DisplayName("키워드 '4'로 이름 검색")
        void getFollowerList_withKeyword() {
            Sort sort = Sort.by(Sort.Direction.DESC, "followingAt");
            String keyword = "4"; // 이메일 4@4 검색
            List<UserSimpleDto> result = followDao.getUsers(TEST_USER_ID, 0, 10, sort, keyword, "FOLLOWER");
            printResult("팔로워 목록: 키워드 '4' 검색 (ID:102 한 명만 나와야 함)", result);
        }

        @Test
        @DisplayName("페이지네이션 (1개 건너뛰고 2개씩)")
        void getFollowerList_withPagination() {
            Sort sort = Sort.by(Sort.Direction.DESC, "followingAt");
            // 전체 순서: 102, [103, 104]
            List<UserSimpleDto> result = followDao.getUsers(TEST_USER_ID, 1, 2, sort, null, "FOLLOWER");
            printResult("팔로워 목록: 페이지네이션 (ID:103, 104가 나와야 함)", result);
        }
    }

    // =================================================================
    //                    논 (None) 목록 테스트
    // =================================================================
    @Nested
    @DisplayName("None 목록 조회 테스트")
    class NoneTests {
        @Test
        @DisplayName("None 을 이름으로 찾기")
        void getNoneList_with100() {
            Sort sort = Sort.by(Sort.Direction.DESC, "name");
            List<UserSimpleDto> result = followDao.getUsers(TEST_USER_ID, 0, 10, sort, null, "NONE");
            printResult("None 100 의 기준에서 바라보기", result);
        }
    }

    // --- Helper Method ---
    private void printResult(String title, List<UserSimpleDto> users) {
        System.out.println("\n===== " + title + " =====");
        System.out.printf("%-5s %-10s %-20s %-15s %-15s%n",
            "ID", "Name", "Email", "FollowingAt", "FollowedAt");
        System.out.println("----------------------------------------------------------------------");
        if (users.isEmpty()) {
            System.out.println("... 결과 없음 ...");
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm");
        users.forEach(user ->
            System.out.printf("%-5d %-10s %-20s %-15s %-15s%n",
                user.id(), user.name(), user.email(),
                user.followingAt() != null ? user.followingAt().format(formatter) : "N/A",
                user.followedAt() != null ? user.followedAt().format(formatter) : "N/A"
            )
        );
        System.out.println("======================================================================\n");
    }
}