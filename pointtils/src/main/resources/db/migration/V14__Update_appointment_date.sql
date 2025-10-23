-- ========================
-- MIGRATION: Atualizar data de appointment específico
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
UPDATE appointment
SET date = '2025-10-22'::date
FROM enterprise, interp
WHERE appointment.interpreter_id = interp.interpreter_id
  AND appointment.user_id = enterprise.enterprise_id
  AND appointment.city = 'Rio de Janeiro'
  AND appointment.neighborhood = 'Copacabana'
  AND appointment.street = 'Av. Atlântica'
  AND appointment.street_number = 789
  AND appointment.start_time = '09:30'::time
  AND appointment.end_time = '11:00'::time
  AND appointment.status = 'ACCEPTED'::appointment_status_enum
  AND appointment.modality = 'PERSONALLY'::appointment_modality_enum;