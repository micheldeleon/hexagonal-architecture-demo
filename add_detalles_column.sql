-- Script para agregar la columna 'detalles' a la tabla tournaments
-- Ejecutar este script si Hibernate no crea automáticamente la columna

ALTER TABLE public.tournaments 
ADD COLUMN IF NOT EXISTS detalles TEXT;

-- Verificar que la columna fue agregada
SELECT column_name, data_type, character_maximum_length
FROM information_schema.columns
WHERE table_schema = 'public' 
  AND table_name = 'tournaments'
  AND column_name = 'detalles';
