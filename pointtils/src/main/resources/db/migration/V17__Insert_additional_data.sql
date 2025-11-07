-- ========================
-- V17 - Additional test data to exercise flows (clients, enterprises, interpreters,
-- specialties links, appointments with different statuses, ratings, schedules,
-- parameters and interpreter documents)
-- NOTE: created_at/modified_at columns have defaults (see V15), so inserts don't
-- need to provide them.
-- ========================

-- ======== Additional Clients =========
WITH new_user1 AS (
	INSERT INTO user_account (email, password, phone, picture, status, type)
	VALUES ('client2@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '51999999998', NULL, 'ACTIVE', 'PERSON')
	RETURNING id
)
INSERT INTO person (id, name, gender, birthday, cpf)
SELECT id, 'Ana Santos', 'FEMALE', '1995-08-15', '23456789012' FROM new_user1;

WITH new_user2 AS (
	INSERT INTO user_account (email, password, phone, picture, status, type)
	VALUES ('client3@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '47999999997', NULL, 'ACTIVE', 'PERSON')
	RETURNING id
)
INSERT INTO person (id, name, gender, birthday, cpf)
SELECT id, 'Carlos Oliveira', 'MALE', '1988-12-03', '34567890123' FROM new_user2;

-- Add locations for the new clients
INSERT INTO location (user_id, UF, city, neighborhood)
SELECT id, 'RS', 'Porto Alegre', 'Centro' FROM user_account WHERE email = 'client2@email.com';

INSERT INTO location (user_id, UF, city, neighborhood)
SELECT id, 'SC', 'Florianópolis', 'Trindade' FROM user_account WHERE email = 'client3@email.com';


-- ======== Additional Enterprises =========
WITH new_user3 AS (
	INSERT INTO user_account (email, password, phone, picture, status, type)
	VALUES ('empresa2@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '11988888887', NULL, 'ACTIVE', 'ENTERPRISE')
	RETURNING id
)
INSERT INTO enterprise (id, corporate_reason, cnpj)
SELECT id, 'Tech Solutions SA', '23456789000191' FROM new_user3;

WITH new_user4 AS (
	INSERT INTO user_account (email, password, phone, picture, status, type)
	VALUES ('empresa3@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '11988888886', NULL, 'ACTIVE', 'ENTERPRISE')
	RETURNING id
)
INSERT INTO enterprise (id, corporate_reason, cnpj)
SELECT id, 'Educação Inclusiva LTDA', '34567890000192' FROM new_user4;

INSERT INTO location (user_id, UF, city, neighborhood)
SELECT id, 'SP', 'Campinas', 'Barão Geraldo' FROM user_account WHERE email = 'empresa2@email.com';

INSERT INTO location (user_id, UF, city, neighborhood)
SELECT id, 'PR', 'Curitiba', 'Batel' FROM user_account WHERE email = 'empresa3@email.com';


-- ======== Additional Interpreters =========
WITH new_user5 AS (
	INSERT INTO user_account (email, password, phone, picture, status, type)
	VALUES ('interpreter2@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '11977777776', NULL, 'ACTIVE', 'INTERPRETER')
	RETURNING id
),
new_person2 AS (
	INSERT INTO person (id, name, gender, birthday, cpf)
	SELECT id, 'Pedro Mendes', 'MALE', '1992-07-25', '45678901234' FROM new_user5
	RETURNING id
)
INSERT INTO interpreter (id, cnpj, rating, image_rights, modality, description)
SELECT id, '45678901000193', 4.9, TRUE, 'ALL', 'Intérprete especializado em contextos educacionais e empresariais.' FROM new_person2;

WITH new_user6 AS (
	INSERT INTO user_account (email, password, phone, picture, status, type)
	VALUES ('interpreter3@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '11977777775', NULL, 'ACTIVE', 'INTERPRETER')
	RETURNING id
),
new_person3 AS (
	INSERT INTO person (id, name, gender, birthday, cpf)
	SELECT id, 'Lucia Costa', 'FEMALE', '1987-11-10', '56789012345' FROM new_user6
	RETURNING id
)
INSERT INTO interpreter (id, cnpj, rating, image_rights, modality, description)
SELECT id, NULL, 4.7, TRUE, 'PERSONALLY', 'Intérprete com experiência em eventos culturais e artísticos.' FROM new_person3;

INSERT INTO location (user_id, UF, city, neighborhood)
SELECT id, 'SP', 'São Paulo', 'Vila Mariana' FROM user_account WHERE email = 'interpreter2@email.com';

INSERT INTO location (user_id, UF, city, neighborhood)
SELECT id, 'RJ', 'Rio de Janeiro', 'Botafogo' FROM user_account WHERE email = 'interpreter3@email.com';


-- ======== Link specialties to the new interpreters =========
-- Use the canonical specialty names inserted in V5
INSERT INTO user_specialties (specialtie_id, user_id)
SELECT s.id, u.id
FROM specialties s, user_account u
WHERE s.name = 'Intérprete de Libras' AND u.email = 'interpreter2@email.com';

INSERT INTO user_specialties (specialtie_id, user_id)
SELECT s.id, u.id
FROM specialties s, user_account u
WHERE s.name = 'Intérprete de Sinais Internacionais' AND u.email = 'interpreter3@email.com';


-- ======== Appointments with varied statuses and addresses =========
-- 1) PENDING - client2 with interpreter2 (in-person)
WITH client AS (
	SELECT id AS client_id FROM user_account WHERE email = 'client2@email.com'
),
interp AS (
	SELECT id AS interpreter_id FROM user_account WHERE email = 'interpreter2@email.com'
)
INSERT INTO appointment (UF, city, neighborhood, street, street_number, address_details, modality, date, description, status, interpreter_id, user_id, start_time, end_time)
SELECT 'SP', 'São Paulo', 'Vila Mariana', 'Rua Domingos de Morais', 1000, 'Auditório 3', 'PERSONALLY'::appointment_modality_enum, '2025-11-10'::date,
			 'Palestra sobre inclusão em ambiente corporativo', 'PENDING'::appointment_status_enum,
			 interp.interpreter_id, client.client_id, '14:00'::time, '16:00'::time
FROM client, interp;

-- 2) COMPLETED - empresa2 with interpreter2 (online)
WITH client AS (
	SELECT id AS client_id FROM user_account WHERE email = 'empresa2@email.com'
),
interp AS (
	SELECT id AS interpreter_id FROM user_account WHERE email = 'interpreter2@email.com'
)
INSERT INTO appointment (UF, city, modality, date, description, status, interpreter_id, user_id, start_time, end_time)
SELECT 'SP', 'Campinas', 'ONLINE'::appointment_modality_enum, '2025-10-15'::date,
			 'Treinamento corporativo online', 'COMPLETED'::appointment_status_enum,
			 interp.interpreter_id, client.client_id, '09:00'::time, '12:00'::time
FROM client, interp;

-- 3) CANCELED - client3 with interpreter3 (in-person)
WITH client AS (
	SELECT id AS client_id FROM user_account WHERE email = 'client3@email.com'
),
interp AS (
	SELECT id AS interpreter_id FROM user_account WHERE email = 'interpreter3@email.com'
)
INSERT INTO appointment (UF, city, neighborhood, street, street_number, address_details, modality, date, description, status, interpreter_id, user_id, start_time, end_time)
SELECT 'RJ', 'Rio de Janeiro', 'Botafogo', 'Rua Voluntários da Pátria', 200, 'Clínica Central', 'PERSONALLY'::appointment_modality_enum, '2025-11-20'::date,
			 'Consulta médica com interpretação', 'CANCELED'::appointment_status_enum,
			 interp.interpreter_id, client.client_id, '10:30'::time, '11:30'::time
FROM client, interp;


-- ======== Ratings =========
-- Add rating for the completed appointment
WITH appt AS (
	SELECT id as appointment_id 
	FROM appointment 
	WHERE description = 'Treinamento corporativo online'
)
INSERT INTO rating (stars, description, appointment_id)
SELECT 4.5, 'Ótimo profissional, muito pontual e eficiente!', appointment_id FROM appt;


-- ======== Schedules (use WED - schedule enum was renamed in V7) =========
WITH interp AS (
	SELECT id as interpreter_id FROM user_account WHERE email = 'interpreter2@email.com'
)
INSERT INTO schedule (interpreter_id, day, start_time, end_time)
SELECT interpreter_id, 'MON'::schedule_day_enum, '08:00'::time, '18:00'::time FROM interp
UNION ALL
SELECT interpreter_id, 'TUE'::schedule_day_enum, '08:00'::time, '18:00'::time FROM interp
UNION ALL
SELECT interpreter_id, 'THU'::schedule_day_enum, '13:00'::time, '21:00'::time FROM interp;

WITH interp AS (
	SELECT id as interpreter_id FROM user_account WHERE email = 'interpreter3@email.com'
)
INSERT INTO schedule (interpreter_id, day, start_time, end_time)
SELECT interpreter_id, 'TUE'::schedule_day_enum, '09:00'::time, '17:00'::time FROM interp
UNION ALL
SELECT interpreter_id, 'WED'::schedule_day_enum, '09:00'::time, '17:00'::time FROM interp
UNION ALL
SELECT interpreter_id, 'FRI'::schedule_day_enum, '10:00'::time, '19:00'::time FROM interp;


-- ======== Interpreter documents =========
WITH interp AS (
	SELECT id as interpreter_id FROM user_account WHERE email = 'interpreter2@email.com'
)
INSERT INTO interpreter_documents (interpreter_id, document)
SELECT interpreter_id, 'https://storage.example.com/docs/interp2_id_front.jpg' FROM interp;


-- ======== Parameters (unique keys) =========
-- Avoid duplicating keys already inserted in other migrations (e.g. 'PENDING_INTERPRETER_ADMIN', 'FAQ')
INSERT INTO parameters (id, key, value)
VALUES
	(uuid_generate_v4(), 'TEST_CONTACT_EMAIL', 'suporte@test.pointtils.local'),
	(uuid_generate_v4(), 'FEATURE_FLAG_SAMPLE_FLOW', 'true');

-- End of V17

