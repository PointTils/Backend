-- V4__Update_user_type_and_data.sql
-- 1. Atualizar os registros existentes de 'CLIENT' para 'PERSON'
UPDATE user_account
SET type = 'PERSON'
WHERE type = 'CLIENT';

-- 2. Inserir especialidades adicionais
INSERT INTO specialties (id, name) 
VALUES 
    ('f654dbc8-59f2-4285-8cab-9aad7f94eec1', 'Libras'),
    ('24019bfa-1940-4cf9-b850-f44d95a14ac7', 'Tradução Simultânea')
ON CONFLICT (id) DO NOTHING;

-- 3. Inserir usuários adicionais
INSERT INTO user_account (id, email, password, phone, status, type)
VALUES
    ('fc2deec4-607d-4535-8d06-0392a09ff02b', 'jose.gustavo.silva@gmail.com', '$2a$10$JBATmY/jM85PlzukMYw4Y.N4BE6LEPHMtSTdoC0K43NE/m6kyxlbC', '51945454545', 'ACTIVE', 'PERSON'),
    ('4d48ea39-65b5-491a-9b84-12d928f04eeb', 'thiago.medeiros@gmail.com', '$2a$10$xwGI1Hk6/6ySX24xbiLarORWs/JfGXJybpu.YdM4XiI.94lWgVeHm', '51930204125', 'ACTIVE', 'PERSON'),
    ('0e588986-e77c-47a3-b9e4-5e3cc7ae211b', 'carlos.ferreira@gmail.com', '$2a$10$D3TRDWl4viBguFVY3.uG5eEkZB.zbTS5892EtmxYI6r75vMzkVF6C', '54987879952', 'INACTIVE', 'PERSON')
ON CONFLICT (id) DO NOTHING;

-- 4. Inserir pessoas correspondentes
INSERT INTO person (id, name, gender, birthday, cpf) 
VALUES
    ('fc2deec4-607d-4535-8d06-0392a09ff02b', 'José Gustavo da Silva', 'MALE', '2000-09-19', '12345678987'),
    ('4d48ea39-65b5-491a-9b84-12d928f04eeb', 'Thiago Medeiros', 'MALE', '1991-01-24', '32165432111'),
    ('0e588986-e77c-47a3-b9e4-5e3cc7ae211b', 'Carlos Ferreira', 'MALE', '1975-05-12', '42455554466')
ON CONFLICT (id) DO NOTHING;