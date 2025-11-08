-- ========================
-- MIGRATION: Adiciona campo video_url para a tabela interpreter
-- ========================

ALTER TABLE interpreter
    ADD COLUMN IF NOT EXISTS video_url VARCHAR(2048);

