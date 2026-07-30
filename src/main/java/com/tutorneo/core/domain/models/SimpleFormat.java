package com.tutorneo.core.domain.models;

/**
 * Implementación concreta mínima de {@link Format}. Nos permite instanciar
 * formatos cuando solo necesitamos los atributos básicos (id, name,
 * generaFixture) sin depender de subclases más específicas.
 */
public class SimpleFormat extends Format {

    public SimpleFormat() {
        super();
    }

    public SimpleFormat(Long id, String name, boolean generaFixture) {
        super(id, name, generaFixture);
    }
}
