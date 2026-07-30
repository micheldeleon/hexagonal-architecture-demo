package com.tutorneo.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Seed de formatos eliminatorios: asegura que exista la fila hija en
 * elimination_formats para el formato "Eliminatorio" definido en la tabla
 * formats. Es idempotente y evita tener que insertar a mano.
 */
@Component
public class FormatSeeder implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public FormatSeeder(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        jdbcTemplate.update("""
                insert into elimination_formats (id)
                select f.id
                from "formats" f
                where lower(f.name) = 'eliminatorio'
                  and not exists (select 1 from elimination_formats ef where ef.id = f.id)
                """);
    }
}
