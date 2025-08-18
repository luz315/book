package com.book;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
public abstract class IntegrationTestSupport {

    // 1) 컨테이너를 정적 초기화로 '먼저' 기동
    private static final PostgreSQLContainer<?> POSTGRES;
    private static final GenericContainer<?> REDIS;
    static {
        POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");
        POSTGRES.start();

        REDIS = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
                .withExposedPorts(6379);
        REDIS.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try { REDIS.stop(); } catch (Throwable ignored) {}
            try { POSTGRES.stop(); } catch (Throwable ignored) {}
        }));
    }

    // 2) 이미 떠 있는 컨테이너 정보만 스프링 프로퍼티로 주입
    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        r.add("spring.datasource.username", POSTGRES::getUsername);
        r.add("spring.datasource.password", POSTGRES::getPassword);
        r.add("spring.data.redis.host", REDIS::getHost);              // ★ 반드시 spring.data.redis.*
        r.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
    }

    @Autowired private DatabaseCleaner databaseCleaner;
    @Autowired private StringRedisTemplate redis;

    @BeforeEach
    void reset() {
        databaseCleaner.clear(); // DB 초기화 (커밋됨)
        var c = redis.getConnectionFactory().getConnection();
        try { c.serverCommands().flushDb(); } finally { c.close(); } // Redis 초기화
    }
}
