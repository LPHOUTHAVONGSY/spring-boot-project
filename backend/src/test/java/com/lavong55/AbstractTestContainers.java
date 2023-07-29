package com.lavong55;

import com.github.javafaker.Faker;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
//annotation is used to enable Testcontainers integration with JUnit. It allows the use of containers in tests.
public abstract class AbstractTestContainers {
//Abstract so it's intended use is to be extended by other classes.
    @BeforeAll
    //Annotation is used to indicate that the beforeAll method should be executed once before all the
    //tests in the class. In this method, Flyway is configured to use the PostgreSQL database provided by
    //the postgreSQLContainer. The migration is then performed, ensuring that the database schema is up-to-date
    //before running the tests.
    static void beforeAll() {
            Flyway flyway = Flyway
                    .configure()
                    .dataSource(
                    postgreSQLContainer.getJdbcUrl(),
                    postgreSQLContainer.getUsername(),
                    postgreSQLContainer.getPassword()
            ).load();
            flyway.migrate();
            //Migrate method is invoked to execute any pending database migrations and bring the database schema up-to-date.
        }

    @Container
    //Annotation is used to define a static field postgreSQLContainer as a PostgreSQLContainer object.
    //This container will be responsible for spinning up a PostgreSQL database instance during the tests.
    //The container is configured with the latest version of the PostgreSQL image, a specific database name,
    //username, and password.
    protected static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("lavong55-dao-unit-test")
                    .withUsername("lavong55")
                    .withPassword("password");

    @DynamicPropertySource
    //Annotation is used to define a static method registerDataSourceProperties.
    //This method is responsible for registering dynamic properties for the Spring application context.
    //In this case, it sets the spring.datasource.url, spring.datasource.username, and spring.datasource.password
    //properties to the corresponding values from the postgreSQLContainer.
    private static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.datasource.url",
                postgreSQLContainer::getJdbcUrl
        );
        registry.add(
                "spring.datasource.username",
                postgreSQLContainer::getUsername
        );
        registry.add(
                "spring.datasource.password",
                postgreSQLContainer::getPassword
        );
    }

    private static DataSource getDataSource() {
        DataSourceBuilder builder = DataSourceBuilder.create()
                .driverClassName(postgreSQLContainer.getDriverClassName())
                .url(postgreSQLContainer.getJdbcUrl())
                .username(postgreSQLContainer.getUsername())
                .password(postgreSQLContainer.getPassword());
        return builder.build();
    }

    protected static JdbcTemplate getJdbcTemplate () {
        return new JdbcTemplate(getDataSource());
    }

    protected static final Faker FAKER = new Faker();
}