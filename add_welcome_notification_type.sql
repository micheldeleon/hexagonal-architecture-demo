-- Script para agregar el tipo de notificación WELCOME al constraint
-- Ejecutar este script en la base de datos PostgreSQL

-- Primero eliminamos el constraint existente
ALTER TABLE notifications DROP CONSTRAINT IF EXISTS notifications_type_check;

-- Creamos el nuevo constraint con todos los tipos de notificación incluyendo WELCOME
ALTER TABLE notifications 
ADD CONSTRAINT notifications_type_check 
CHECK (type IN (
    'WELCOME',
    'TOURNAMENT_CANCELED',
    'TEAM_REMOVED',
    'TOURNAMENT_STARTED',
    'MATCH_SCHEDULED',
    'MATCH_RESULT',
    'REGISTRATION_CONFIRMED',
    'TOURNAMENT_FULL',
    'TOURNAMENT_REMINDER',
    'GENERAL'
));

-- Verificar que el constraint fue creado correctamente
SELECT conname, contype, convalidated
FROM pg_constraint
WHERE conname = 'notifications_type_check';
