-- Alterar o tipo ENUM de user_type_enum de 'CLIENT' para 'PERSON'
ALTER TYPE user_type_enum RENAME VALUE 'CLIENT' TO 'PERSON';

-- Atualizar os registros existentes que usam 'CLIENT' para 'PERSON'
UPDATE user_account SET type = 'PERSON' WHERE type = 'CLIENT';

-- Inserir especialidades adicionais se necessário
INSERT INTO specialties (id, name) 
VALUES 
  ('f654dbc8-59f2-4285-8cab-9aad7f94eec1', 'Libras'),
  ('24019bfa-1940-4cf9-b850-f44d95a14ac7', 'Tradução Simultânea')
ON CONFLICT (id) DO NOTHING;

-- Inserir usuários adicionais que aparecem no dump completo
INSERT INTO user_account (id, email, password, phone, picture, status, type) VALUES
  ('fc2deec4-607d-4535-8d06-0392a09ff02b', 'string', '$2a$10$JBATmY/jM85PlzukMYw4Y.N4BE6LEPHMtSTdoC0K43NE/m6kyxlbC', 'string', 'string', 'ACTIVE', 'PERSON'),
  ('4d48ea39-65b5-491a-9b84-12d928f04eeb', 'teste11@teste.com', '$2a$10$xwGI1Hk6/6ySX24xbiLarORWs/JfGXJybpu.YdM4XiI.94lWgVeHm', 'string', 'string', 'ACTIVE', 'PERSON'),
  ('0e588986-e77c-47a3-b9e4-5e3cc7ae211b', 'testefinal2@email.com', '$2a$10$D3TRDWl4viBguFVY3.uG5eEkZB.zbTS5892EtmxYI6r75vMzkVF6C', 'string', 'string', 'INACTIVE', 'PERSON')
ON CONFLICT (id) DO NOTHING;

-- Inserir pessoas correspondentes
INSERT INTO person (id, name, gender, birthday, cpf) VALUES
  ('fc2deec4-607d-4535-8d06-0392a09ff02b', 'string', 'MALE', '2025-09-19', 'string'),
  ('4d48ea39-65b5-491a-9b84-12d928f04eeb', 'string', 'MALE', '2025-09-19', ''),
  ('0e588986-e77c-47a3-b9e4-5e3cc7ae211b', 'string', 'MALE', '2025-09-19', '4245555')
ON CONFLICT (id) DO NOTHING;
