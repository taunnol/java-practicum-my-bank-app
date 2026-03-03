package ru.yandex.practicum.bank.accounts.contract;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Date;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "bank.security.enabled=false"
        }
)
@Testcontainers
public abstract class BaseContractTest {

    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.flyway.enabled", () -> true);
        registry.add("spring.flyway.locations", () -> "classpath:db/migration");
    }

    @LocalServerPort
    int port;

    @Autowired
    JdbcTemplate jdbc;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        jdbc.execute("TRUNCATE TABLE accounts");

        jdbc.update(
                "INSERT INTO accounts(login, name, birthdate, balance) VALUES (?,?,?,?)",
                "testuser", "Test User", Date.valueOf("1990-01-01"), 0L
        );

        jdbc.update(
                "INSERT INTO accounts(login, name, birthdate, balance) VALUES (?,?,?,?)",
                "petrov", "Петров Пётр", Date.valueOf("1991-02-02"), 1000L
        );
    }
}