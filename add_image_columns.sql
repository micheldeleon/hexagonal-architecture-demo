-- Script de migración para agregar columnas de imagen
-- Fecha: 2026-01-11

-- Agregar columna de imagen de perfil a la tabla users
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS profile_image_url TEXT;

-- Agregar comentario para documentación
COMMENT ON COLUMN users.profile_image_url IS 'URL de la imagen de perfil del usuario almacenada en Supabase Storage';

-- Agregar columna de imagen a la tabla tournaments
ALTER TABLE tournaments 
ADD COLUMN IF NOT EXISTS image_url TEXT;

-- Agregar comentario para documentación
COMMENT ON COLUMN tournaments.image_url IS 'URL de la imagen del torneo almacenada en Supabase Storage';
