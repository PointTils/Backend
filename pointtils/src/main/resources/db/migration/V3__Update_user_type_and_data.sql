-- V3__Add_client_to_enum.sql
-- Adicionar 'CLIENT' temporariamente ao ENUM caso não exista
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_type t
        JOIN pg_enum e ON t.oid = e.enumtypid
        WHERE t.typname = 'user_type_enum' AND e.enumlabel = 'CLIENT'
    ) THEN
        ALTER TYPE user_type_enum ADD VALUE 'CLIENT';
    END IF;
END$$;