-- 1. Adiciona a coluna permitindo nulos
ALTER TABLE location
ADD COLUMN neighborhood VARCHAR(255);

-- 2. Atualiza os registros existentes com valor padrão
UPDATE location
SET neighborhood = 'Centro'
WHERE neighborhood IS NULL;

-- 3. Torna a coluna obrigatória
ALTER TABLE location
ALTER COLUMN neighborhood SET NOT NULL;