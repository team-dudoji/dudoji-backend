package com.dudoji.spring.dao.DBtest;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class DBTestBase {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> isolated =
            new PostgreSQLContainer<>("postgres:17.4-alpine")
                    .withInitScripts("static/sql/schema.sql", "static/sql/data.sql")
                    .withReuse(true);
}
