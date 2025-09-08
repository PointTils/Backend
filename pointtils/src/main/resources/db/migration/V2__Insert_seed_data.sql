-- ========================
-- CLIENT
-- ========================
WITH new_user AS (
  INSERT INTO user_account (email, password, phone, picture, status, type)
  VALUES ('client1@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '11999999999', NULL, 'ACTIVE', 'CLIENT')
  RETURNING id
)
INSERT INTO person (id, name, gender, birthday, cpf)
SELECT id, 'João da Silva', 'MALE', '1990-05-20', '12345678901' FROM new_user;

INSERT INTO location (user_id, UF, city)
SELECT id, 'SP', 'São Paulo' FROM user_account WHERE email = 'client1@email.com';


-- ========================
-- ENTERPRISE
-- ========================
WITH new_user AS (
  INSERT INTO user_account (email, password, phone, picture, status, type)
  VALUES ('empresa1@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '11888888888', NULL, 'ACTIVE', 'ENTERPRISE')
  RETURNING id
)
INSERT INTO enterprise (id, corporate_reason, cnpj)
SELECT id, 'Empresa Exemplo LTDA', '12345678000190' FROM new_user;

INSERT INTO location (user_id, UF, city)
SELECT id, 'RJ', 'Rio de Janeiro' FROM user_account WHERE email = 'empresa1@email.com';


-- ========================
-- INTERPRETER
-- ========================
WITH new_user AS (
  INSERT INTO user_account (email, password, phone, picture, status, type)
  VALUES ('interpreter1@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '11777777777', NULL, 'ACTIVE', 'INTERPRETER')
  RETURNING id
),
new_person AS (
  INSERT INTO person (id, name, gender, birthday, cpf)
  SELECT id, 'Maria Souza', 'FEMALE', '1985-03-15', '98765432100' FROM new_user
  RETURNING id
)
INSERT INTO interpreter (id, cnpj, rating, min_value, max_value, image_rights, modality, description)
SELECT id, NULL, 4.8, 100.00, 300.00, TRUE, 'ONLINE', 'Intérprete de Libras com experiência em conferências.' FROM new_person;

INSERT INTO location (user_id, UF, city)
SELECT id, 'MG', 'Belo Horizonte' FROM user_account WHERE email = 'interpreter1@email.com';


-- ========================
-- SPECIALTIES & vínculo com intérprete
-- ========================
INSERT INTO specialties (name) VALUES ('Libras'), ('Tradução Simultânea');

INSERT INTO user_specialties (specialtie_id, user_id)
SELECT s.id, u.id
FROM specialties s, user_account u
WHERE s.name = 'Libras'
  AND u.email = 'interpreter1@email.com';


-- ========================
-- APPOINTMENT (Client agenda intérprete)
-- ========================
WITH client AS (
  SELECT id AS client_id FROM user_account WHERE email = 'client1@email.com'
),
interp AS (
  SELECT id AS interpreter_id FROM user_account WHERE email = 'interpreter1@email.com'
)
INSERT INTO appointment (UF, city, modality, date, description, status, interpreter_id, user_id, start_time, end_time)
SELECT 'SP', 'São Paulo', 'ONLINE', '2025-09-01',
       'Reunião de negócios com interpretação', 'ACCEPTED',
       interp.interpreter_id, client.client_id, (CAST('10:00' as time)), (CAST('11:00' as time))
FROM client, interp;


-- ========================
-- RATING (Client avaliando o intérprete)
-- ========================
WITH appt AS (
  SELECT id as appointment_id FROM appointment WHERE description LIKE 'Reunião%'
)
INSERT INTO rating (stars, description, appointment_id)
SELECT 5, 'Excelente trabalho!', appointment_id FROM appt;


-- ========================
-- SCHEDULE (Agenda do intérprete)
-- ========================
WITH interp AS (
  SELECT id as interpreter_id FROM user_account WHERE email = 'interpreter1@email.com'
)
INSERT INTO schedule (interpreter_id, day, start_time, end_time)
SELECT interpreter_id, 'MON'::schedule_day_enum, (CAST('09:00' as time)), (CAST('18:00' as time)) FROM interp
UNION ALL
SELECT interpreter_id, 'WEN'::schedule_day_enum, (CAST('14:00' as time)), (CAST('20:00' as time)) FROM interp;
