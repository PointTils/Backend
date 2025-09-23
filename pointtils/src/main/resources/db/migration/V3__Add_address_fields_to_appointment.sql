-- Add address fields to appointment
ALTER TABLE appointment
    ADD COLUMN IF NOT EXISTS neighborhood VARCHAR(255),
    ADD COLUMN IF NOT EXISTS street VARCHAR(255),
    ADD COLUMN IF NOT EXISTS street_number INTEGER,
    ADD COLUMN IF NOT EXISTS address_details TEXT;
