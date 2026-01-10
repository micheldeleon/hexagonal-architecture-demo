package com.example.demo.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
class FormatSeederTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ApplicationArguments args;

    @Test
    void run_executesIdempotentInsertSql() {
        FormatSeeder seeder = new FormatSeeder(jdbcTemplate);
        seeder.run(args);

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).update(sqlCaptor.capture());

        String sql = sqlCaptor.getValue();
        assertThat(sql).contains("insert into elimination_formats");
        assertThat(sql).contains("from \"formats\"");
        assertThat(sql).contains("where lower(f.name) = 'eliminatorio'");
    }
}
