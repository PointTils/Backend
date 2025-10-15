-- Migration para adicionar constraint única na coluna key da tabela parameters
ALTER TABLE parameters ADD CONSTRAINT parameters_key_unique UNIQUE (key);
