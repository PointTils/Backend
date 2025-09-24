-- V5__Update_address_data.sql

-- 1. Adiciona bairro na tabela de localizacao
ALTER TABLE location
ADD COLUMN neighborhood VARCHAR(255);

-- 2. Atualiza registros de localizacao existentes para preencher bairro
UPDATE location
SET neighborhood = 'Higienópolis'
WHERE city = 'São Paulo' AND neighborhood IS NULL;

UPDATE location
SET neighborhood = 'Ipanema'
WHERE city = 'Rio de Janeiro' AND neighborhood IS NULL;

UPDATE location
SET neighborhood = 'Anchieta'
WHERE city = 'Belo Horizonte' AND neighborhood IS NULL;

-- 3. Atualiza colunas de localizacao para que sejam NOT NULL
ALTER TABLE location
ALTER COLUMN city SET NOT NULL;

ALTER TABLE location
ALTER COLUMN UF SET NOT NULL;

ALTER TABLE location
ALTER COLUMN neighborhood SET NOT NULL;

-- 4. Atualiza colunas de appointment para que sejam nullable, dado que para casos online nao serao preenchidas
ALTER TABLE appointment
ALTER COLUMN UF DROP NOT NULL;

ALTER TABLE appointment
ALTER COLUMN city DROP NOT NULL;

-- 5. Adiciona colunas de endereco na tabela de appointment
ALTER TABLE appointment
ADD COLUMN neighborhood VARCHAR(255),
ADD COLUMN street VARCHAR(255),
ADD COLUMN street_number INTEGER,
ADD COLUMN address_details TEXT;

-- 6. Atualiza registros de appointment existentes para nao preencher endereco, por ser online
UPDATE appointment
SET UF = NULL,
    city = NULL
WHERE UF = 'SP' AND modality = 'ONLINE';

-- 7. Remove localizacoes vinculadas a usuarios que nao sao interpretes
DELETE FROM location
WHERE id IN (
    SELECT l.id
    FROM location l
    LEFT JOIN user_account ua ON l.user_id = ua.id
    WHERE ua.type != 'INTERPRETER'
);