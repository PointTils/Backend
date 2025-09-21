-- V5__Update_specialty_names.sql

-- 1. Atualizar os registros existentes de especialidades para respeitar lista passada pelos stakeholders
UPDATE specialties
SET name = 'Intérprete de Libras'
WHERE id = (SELECT id FROM specialties WHERE name = 'Libras' ORDER BY id LIMIT 1);

UPDATE specialties
SET name = 'Guia-intérprete de Libras'
WHERE id = (SELECT id FROM specialties WHERE name = 'Tradução Simultânea' ORDER BY id LIMIT 1);

UPDATE specialties
SET name = 'Intérprete Tátil'
WHERE name = 'Libras';

UPDATE specialties
SET name = 'Intérprete de Sinais Internacionais'
WHERE name = 'Tradução Simultânea';