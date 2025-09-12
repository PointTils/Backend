-- Add UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Tipos
CREATE TYPE user_status_enum AS ENUM ('ACTIVE', 'INACTIVE', 'PENDING');
CREATE TYPE user_type_enum AS ENUM ('CLIENT', 'ENTERPRISE', 'INTERPRETER');
CREATE TYPE interpreter_modality_enum AS ENUM ('ONLINE', 'PERSONALLY', 'ALL');
CREATE TYPE appointment_modality_enum AS ENUM ('ONLINE', 'PERSONALLY');
CREATE TYPE appointment_status_enum AS ENUM ('PENDING', 'ACCEPTED', 'CANCELED', 'COMPLETED');
CREATE TYPE schedule_day_enum AS ENUM ('MON', 'TUE', 'WEN', 'THU', 'FRI', 'SAT', 'SUN');
CREATE TYPE person_gender_enum AS ENUM ('MALE', 'FEMALE', 'OTHERS');

-- Usuário base
CREATE TABLE user_account (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  phone VARCHAR(11) NOT NULL,
  picture TEXT,
  status user_status_enum NOT NULL,
  type user_type_enum NOT NULL
);

-- Pessoa (CLIENT)
CREATE TABLE person (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  gender person_gender_enum NOT NULL,
  birthday DATE NOT NULL,
  cpf VARCHAR(11) NOT NULL UNIQUE,
  CONSTRAINT fk_person_user FOREIGN KEY (id) REFERENCES user_account (id) ON DELETE CASCADE
);

-- Empresa (ENTERPRISE)
CREATE TABLE enterprise (
  id UUID PRIMARY KEY,
  corporate_reason VARCHAR(255) NOT NULL,
  cnpj VARCHAR(14) NOT NULL UNIQUE,
  CONSTRAINT fk_enterprise_user FOREIGN KEY (id) REFERENCES user_account (id) ON DELETE CASCADE
);

-- Intérprete (INTERPRETER)
CREATE TABLE interpreter (
  id UUID PRIMARY KEY,
  cnpj VARCHAR(14),
  rating NUMERIC NOT NULL,
  min_value DECIMAL NOT NULL,
  max_value DECIMAL NOT NULL,
  image_rights BOOLEAN NOT NULL,
  modality interpreter_modality_enum NOT NULL,
  description TEXT NOT NULL,
  CONSTRAINT fk_interpreter_user FOREIGN KEY (id) REFERENCES user_account (id) ON DELETE CASCADE
);

-- Especialidades
CREATE TABLE specialties (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name VARCHAR(255) NOT NULL
);

CREATE TABLE user_specialties (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  specialtie_id UUID NOT NULL,
  user_id UUID NOT NULL,
  CONSTRAINT fk_user_specialties_specialtie FOREIGN KEY (specialtie_id) REFERENCES specialties (id),
  CONSTRAINT fk_user_specialties_user       FOREIGN KEY (user_id) REFERENCES user_account (id)
);

-- Agendamento
CREATE TABLE appointment (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  UF VARCHAR(2) NOT NULL,
  city VARCHAR(255) NOT NULL,
  modality appointment_modality_enum NOT NULL,
  date DATE NOT NULL,
  description TEXT NOT NULL,
  status appointment_status_enum NOT NULL,
  interpreter_id UUID NOT NULL,
  user_id UUID NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  CONSTRAINT fk_appointment_interpreter FOREIGN KEY (interpreter_id) REFERENCES interpreter (id),
  CONSTRAINT fk_appointment_user        FOREIGN KEY (user_id)        REFERENCES user_account (id)
);

-- Avaliação
CREATE TABLE rating (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  stars DECIMAL NOT NULL,
  description TEXT,
  appointment_id UUID NOT NULL,
  CONSTRAINT fk_rating_appointment FOREIGN KEY (appointment_id) REFERENCES appointment (id)
);

-- Agenda intérprete
CREATE TABLE schedule (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  interpreter_id UUID NOT NULL,
  day schedule_day_enum NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  CONSTRAINT fk_schedule_interpreter FOREIGN KEY (interpreter_id) REFERENCES interpreter (id)
);

-- Parâmetros gerais
CREATE TABLE parameters (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  key TEXT NOT NULL,
  value TEXT NOT NULL
);

-- Documentos do intérprete
CREATE TABLE interpreter_documents (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  interpreter_id UUID NOT NULL,
  document TEXT NOT NULL,
  CONSTRAINT fk_documents_interpreter FOREIGN KEY (interpreter_id) REFERENCES interpreter (id)
);

-- Localização do usuário
CREATE TABLE location (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  UF VARCHAR(2),
  city VARCHAR(255),
  user_id UUID NOT NULL,
  CONSTRAINT fk_location_user FOREIGN KEY (user_id) REFERENCES user_account (id)
);

-- Create a view named "user" that selects all data from "user_account"
CREATE OR REPLACE VIEW "user" AS
SELECT * FROM user_account;
