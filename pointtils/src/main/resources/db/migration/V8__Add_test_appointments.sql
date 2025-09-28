-- ========================
-- APPOINTMENTS ADICIONAIS PARA TESTE DO ENDPOINT DE FILTROS
-- ========================

WITH client AS (
  SELECT id AS client_id FROM user_account WHERE email = 'client1@email.com'
),
enterprise AS (
  SELECT id AS enterprise_id FROM user_account WHERE email = 'empresa1@email.com'
),
interp AS (
  SELECT id AS interpreter_id FROM user_account WHERE email = 'interpreter1@email.com'
)
INSERT INTO appointment (UF, city, neighborhood, street, street_number, address_details, modality, date, description, status, interpreter_id, user_id, start_time, end_time)
-- Appointment PENDING + ONLINE + Hoje
SELECT 'SP', 'São Paulo', 'Vila Madalena', 'Rua Harmonia', 456, 'Casa 2', 'ONLINE'::appointment_modality_enum, '2025-09-23'::date,
       'Consulta médica online', 'PENDING'::appointment_status_enum,
       interp.interpreter_id, client.client_id, '14:00'::time, '15:00'::time
FROM client, interp
UNION ALL
-- Appointment ACCEPTED + PERSONALLY + Amanhã
SELECT 'RJ', 'Rio de Janeiro', 'Copacabana', 'Av. Atlântica', 789, 'Sala 202', 'PERSONALLY'::appointment_modality_enum, '2025-09-24'::date,
       'Reunião presencial importante', 'ACCEPTED'::appointment_status_enum,
       interp.interpreter_id, enterprise.enterprise_id, '09:30'::time, '11:00'::time
FROM enterprise, interp
UNION ALL
-- Appointment COMPLETED + ONLINE + Ontem (passado)
SELECT 'MG', 'Belo Horizonte', 'Savassi', 'Rua da Bahia', 321, 'Coworking Space', 'ONLINE'::appointment_modality_enum, '2025-09-22'::date,
       'Sessão de terapia online', 'COMPLETED'::appointment_status_enum,
       interp.interpreter_id, client.client_id, '16:00'::time, '17:30'::time
FROM client, interp
UNION ALL
-- Appointment CANCELED + PERSONALLY + Próxima semana
SELECT 'PR', 'Curitiba', 'Batel', 'Rua XV de Novembro', 654, 'Edifício Central', 'PERSONALLY'::appointment_modality_enum, '2025-09-30'::date,
       'Apresentação cancelada', 'CANCELED'::appointment_status_enum,
       interp.interpreter_id, enterprise.enterprise_id, '10:15'::time, '12:00'::time
FROM enterprise, interp
UNION ALL
-- Appointment PENDING + ONLINE + Hoje tarde
SELECT 'SP', 'São Paulo', 'Jardins', 'Rua Augusta', 987, 'Loja 15', 'ONLINE'::appointment_modality_enum, '2025-09-23'::date,
       'Workshop online de capacitação', 'PENDING'::appointment_status_enum,
       interp.interpreter_id, enterprise.enterprise_id, '18:30'::time, '20:00'::time
FROM enterprise, interp;