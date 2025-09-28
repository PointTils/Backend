-- Atualiza email do usuario surdo de client1 para person1, para respeitar os nomes das entidades

UPDATE user_account
SET email = 'person1@email.com'
WHERE email = 'client1@email.com';

-- Adicionar especialidades aos usuários para teste

INSERT INTO user_specialties(user_id, specialtie_id) VALUES
((SELECT id FROM user_account WHERE email = 'interpreter1@email.com'), 'f960f5b0-be40-4bff-8625-84fbe6e86588'), -- Usuário intérprete
((SELECT id FROM user_account WHERE email = 'interpreter1@email.com'), '9e9a0cc0-f968-4852-9683-7fa5c138a5da'), -- Usuário intérprete
((SELECT id FROM user_account WHERE email = 'empresa1@email.com'), '9e9a0cc0-f968-4852-9683-7fa5c138a5da'),-- Usuário empresa
((SELECT id FROM user_account WHERE email = 'person1@email.com'), 'f7462efe-b4fe-4098-86ae-d1b0290a32f6'); -- Usuário pessoa

-- Corrige enum de dias da semana

ALTER TYPE schedule_day_enum RENAME VALUE 'WEN' TO 'WED';