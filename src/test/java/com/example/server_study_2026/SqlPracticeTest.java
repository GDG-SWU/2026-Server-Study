package com.example.server_study_2026;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@JdbcTest
@Sql(scripts = "/insert-customers.sql", config = @SqlConfig(encoding = "UTF-8"))
class SqlPracticeTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void selectCustomers() {
        assertThat(jdbc.queryForList("SELECT * FROM customers")).hasSize(3);
        assertThat(jdbc.queryForObject(
                "SELECT name FROM customers WHERE id = 2", String.class))
                .isEqualTo("이이번");
        assertThat(jdbc.queryForList("SELECT * FROM customers WHERE id = 1")).hasSize(1);
        var customer = jdbc.queryForMap(
                "SELECT id, name FROM customers WHERE phone_number = '010-2222-2222'");
        assertThat(customer.get("ID")).isEqualTo(2L);
        assertThat(customer.get("NAME")).isEqualTo("이이번");
    }

    @Test
    void filterCustomers() {
        assertThat(jdbc.queryForList(
                "SELECT name FROM customers WHERE age >= 20", String.class))
                .containsExactlyInAnyOrder("이이번", "정삼번");
        assertThat(jdbc.queryForList(
                "SELECT name FROM customers WHERE age BETWEEN 10 AND 30", String.class))
                .containsExactlyInAnyOrder("김일번", "이이번");
        assertThat(jdbc.queryForList(
                "SELECT name FROM customers WHERE name LIKE '김%' OR name LIKE '이%'",
                String.class)).containsExactlyInAnyOrder("김일번", "이이번");
        assertThat(jdbc.queryForList(
                "SELECT name FROM customers WHERE age >= 20 AND age <= 30", String.class))
                .containsExactly("이이번");
        assertThat(jdbc.queryForList(
                "SELECT name FROM customers WHERE age <> 20", String.class))
                .containsExactlyInAnyOrder("김일번", "정삼번");
        assertThat(jdbc.queryForList("SELECT * FROM customers WHERE name IS NULL")).isEmpty();
        assertThat(jdbc.queryForList("SELECT * FROM customers WHERE name IS NOT NULL")).hasSize(3);
    }

    @Test
    void insertCustomers() {
        jdbc.update("INSERT INTO customers (name, phone_number, age) VALUES ('박사번', '010-4444-4444', 40)");
        jdbc.update("INSERT INTO customers (name, phone_number, age) VALUES ('최오번', '010-5555-5555', 51)");

        assertThat(jdbc.queryForObject(
                "SELECT name FROM customers WHERE id = 4", String.class)).isEqualTo("박사번");
        assertThat(jdbc.queryForObject(
                "SELECT age FROM customers WHERE id = 5", Integer.class)).isEqualTo(51);
    }

    @Test
    void deleteCustomer() {
        jdbc.update("INSERT INTO customers (name, phone_number, age) VALUES ('박사번', '010-4444-4444', 40)");
        jdbc.update("INSERT INTO customers (name, phone_number, age) VALUES ('최오번', '010-5555-5555', 51)");

        assertThat(jdbc.update("DELETE FROM customers WHERE id = 5")).isEqualTo(1);
        assertThat(jdbc.queryForList("SELECT * FROM customers WHERE id = 5")).isEmpty();
        assertThat(jdbc.queryForList("SELECT * FROM customers")).hasSize(4);
    }

    @Test
    void updateCustomers() {
        assertThat(jdbc.update("UPDATE customers SET age = 11 WHERE name = '김일번'"))
                .isEqualTo(1);
        assertThat(jdbc.update("UPDATE customers SET name = '김일' WHERE id = 1"))
                .isEqualTo(1);

        assertThat(jdbc.queryForObject(
                "SELECT age FROM customers WHERE id = 1", Integer.class)).isEqualTo(11);
        assertThat(jdbc.queryForObject(
                "SELECT name FROM customers WHERE id = 1", String.class)).isEqualTo("김일");
        assertThat(jdbc.queryForObject(
                "SELECT age FROM customers WHERE id = 2", Integer.class)).isEqualTo(20);

        assertThat(jdbc.update("UPDATE customers SET phone_number = ''")).isEqualTo(3);
        assertThat(jdbc.queryForList("SELECT phone_number FROM customers", String.class))
                .containsExactly("", "", "");
    }
}

