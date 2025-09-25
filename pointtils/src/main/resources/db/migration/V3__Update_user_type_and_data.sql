-- V3__Add_person_to_enum.sql
-- Adicionar 'PERSON' temporariamente ao ENUM caso não exista
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_type t
        JOIN pg_enum e ON t.oid = e.enumtypid
        WHERE t.typname = 'user_type_enum' AND e.enumlabel = 'PERSON'
    ) THEN
        ALTER TYPE user_type_enum ADD VALUE 'PERSON';
    END IF;
END$$;