package com.study.coupon.repository;

import com.study.coupon.domain.Coupon;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * [실습 5-3] Testcontainers 로 "운영과 같은 DB" 에서 테스트 (관련 문서: 테스트코드 02.스프링테스트/04.Testcontainers.md)
 *
 *  - Docker 가 실행 중이어야 한다. Docker 가 없으면 이 클래스는 건너뛴다(disabledWithoutDocker).
 *  - 처음 실행 시 mysql 이미지를 내려받느라 시간이 걸린다.
 *  - Testcontainers 2.x 에서는 org.testcontainers.mysql.MySQLContainer (제네릭 없음) 로 바뀐다.
 */
@DataJpaTest
@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)   // H2 로 바꾸지 말 것
@DisplayName("쿠폰 레포지토리 (MySQL, Testcontainers)")
class CouponRepositoryMySqlTest {

    /**
     * TODO 5-3. MySQL 컨테이너 선언
     *  - @Container: 테스트 시작 전 컨테이너 실행, 끝나면 정리 (static → 클래스의 모든 테스트가 공유)
     *  - @ServiceConnection: 컨테이너의 접속 정보(URL, 계정)를 spring.datasource.* 에 자동 연결
     *  - new MySQLContainer<>("mysql:8.4")
     *  - 선언하지 않으면 H2 로 실행되어 아래 테스트가 실패한다.
     */
    // TODO 5-3: 컨테이너 필드를 선언하세요

    @Autowired
    DataSource dataSource;

    @Autowired
    CouponRepository couponRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("실제 MySQL 에서 실행된다")
    void runsOnMySql() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection.getMetaData().getDatabaseProductName()).isEqualTo("MySQL");
        }
    }

    @Test
    @DisplayName("MySQL 에서도 발급 가능한 쿠폰 쿼리가 동작한다")
    void findIssuable() {
        LocalDateTime now = LocalDateTime.of(2026, 11, 12, 12, 0);
        couponRepository.save(Coupon.create("A", 1000, 10, now.plusDays(3)));
        couponRepository.save(Coupon.create("B", 1000, 10, now.plusDays(1)));
        em.flush();
        em.clear();

        assertThat(couponRepository.findIssuable(now)).extracting(Coupon::getCode).containsExactly("B", "A");
    }
}
