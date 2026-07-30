package com.tutorneo.adapters.out.persistence.jpa.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tutorneo.adapters.out.persistence.jpa.entities.FormatEntity;

public interface FormatRepositoryJpa extends CrudRepository<FormatEntity, Long> {

    //Inserto un metodo que haga el join con la tabla puente Discipline_Format
    @Query(value = """
    SELECT f.* FROM "Formats" f
    INNER JOIN discipline_formats df ON df.format_id = f.id
    WHERE df.discipline_id = :disciplineId
    """, nativeQuery = true)
    List<FormatEntity> findAllByDisciplineId(Long disciplineId);

    @Query(value = """
    SELECT df.format_id FROM discipline_formats df
    WHERE df.discipline_id = :disciplineId
    """, nativeQuery = true)
    List<Long> findIdsByDisciplineId(Long disciplineId);
}
