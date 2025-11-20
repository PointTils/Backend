-- Adiciona titulo e corpo de notificacoes push de agendamentos na tabela de parametros

INSERT INTO parameters (id, key, value) VALUES
(uuid_generate_v4(), 'NOTIFICATION_APPOINTMENT_REQUESTED', '{"title":"Você recebeu uma nova solicitação!","body":"Alguém acabou de pedir sua ajuda como intérprete."}'),
(uuid_generate_v4(), 'NOTIFICATION_APPOINTMENT_CANCELED', '{"title":"Status da solicitação atualizado","body":"Uma solicitação foi cancelada ou recusada."}'),
(uuid_generate_v4(), 'NOTIFICATION_APPOINTMENT_ACCEPTED', '{"title":"Sua solicitação foi aceita!","body":"Boas notícias! O intérprete aceitou sua solicitação."}'),
(uuid_generate_v4(), 'NOTIFICATION_APPOINTMENT_REMINDER', '{"title":"Lembrete para você!","body":"Seu agendamento está chegando. Não esqueça!"}'),
(uuid_generate_v4(), 'NOTIFICATION_DEFAULT', '{"title":"Nova notificação","body":"Você recebeu uma atualização importante. Clique para mais detalhes."}');