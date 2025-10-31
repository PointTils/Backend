-- Adiciona as colunas na tabela correspondente à entidade User
ALTER TABLE user_account
ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- Adiciona as colunas na tabela correspondente à entidade Specialty
ALTER TABLE specialties
ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- Adiciona as colunas na tabela correspondente à entidade UserSpecialty
ALTER TABLE user_specialties
ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- Adiciona as colunas na tabela correspondente à entidade Schedule
ALTER TABLE schedule
ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- Adiciona as colunas na tabela correspondente à entidade Location
ALTER TABLE location
ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- Adiciona as colunas na tabela correspondente à entidade Parameters
ALTER TABLE parameters
ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- Adiciona as colunas na tabela correspondente à entidade InterpreterDocuments
ALTER TABLE interpreter_documents
ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- Adiciona as colunas na tabela correspondente à entidade Appointment
ALTER TABLE appointment
ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- Adiciona as colunas na tabela correspondente à entidade Rating

ALTER TABLE rating
ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

