-- 1. Atualizar os registros existentes de 'CLIENT' para 'PERSON'
UPDATE user_account
SET type = 'PERSON'
WHERE type = 'CLIENT';