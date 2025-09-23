-- V5__Update_specialty_names.sql

-- 1. Atualizar os registros existentes de especialidades para respeitar lista passada pelos stakeholders

DELETE FROM user_specialties
WHERE specialtie_id IN (SELECT id FROM specialties WHERE name IN ('Libras', 'Tradução Simultânea'));

DELETE FROM specialties
WHERE name IN ('Libras', 'Tradução Simultânea');

INSERT INTO specialties (id, name) VALUES
('f960f5b0-be40-4bff-8625-84fbe6e86588', 'Intérprete de Libras'),
('9e9a0cc0-f968-4852-9683-7fa5c138a5da', 'Guia-intérprete de Libras'),
('6539e352-1742-4f70-a484-c256fc36177a', 'Intérprete Tátil'),
('f7462efe-b4fe-4098-86ae-d1b0290a32f6', 'Intérprete de Sinais Internacionais');