-- Cria tabela para armazenar tokens do usuario por device + app, a ser utilizada para notificaoes push
CREATE TABLE user_app (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    device_id VARCHAR(255) NOT NULL,
    token VARCHAR(255) NOT NULL,
    platform VARCHAR(30) NOT NULL,
    user_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_user_app_user FOREIGN KEY (user_id) REFERENCES user_account (id)
);