-- Migración para crear la tabla de notificaciones

CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    related_entity_id BIGINT,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at)
);

-- Comentarios sobre la estructura:
-- user_id: ID del usuario que recibe la notificación
-- type: Tipo de notificación (TOURNAMENT_CANCELED, TEAM_REMOVED, etc.)
-- title: Título de la notificación
-- message: Mensaje detallado de la notificación
-- related_entity_id: ID de la entidad relacionada (torneo, equipo, etc.)
-- is_read: Indica si la notificación ha sido leída
-- created_at: Fecha de creación de la notificación
-- read_at: Fecha en que se marcó como leída
