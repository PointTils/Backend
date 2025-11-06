-- Migration para inserir templates de email na tabela parameters
INSERT INTO parameters
    (id, key, value)
VALUES
    (
        uuid_generate_v4(),
        'FAQ',
        '[
  {
    "question": "Como faço para agendar um intérprete?",
    "answer": "Para agendar um intérprete, você deve acessar a área de busca dentro do aplicativo e selecionar o profissional que melhor atende às suas necessidades. Ao abrir o perfil do intérprete desejado, basta enviar uma solicitação de agendamento informando o dia, horário e demais detalhes do atendimento. A confirmação do agendamento dependerá da aprovação do intérprete, que analisará sua disponibilidade antes de aceitar ou recusar o pedido."
  },
  {
    "question": "O que acontece se o intérprete recusar ou não confirmar o agendamento?",
    "answer": "Se o intérprete recusar a sua solicitação, você será informado por meio de uma notificação no aplicativo. Caso o intérprete não responda dentro do prazo determinado, a solicitação será automaticamente cancelada para que você possa procurar outro profissional disponível. Dessa forma, você não fica aguardando indefinidamente por uma resposta."
  },
  {
    "question": "Posso cancelar um agendamento? Existe prazo ou multa?",
    "answer": "Sim. Você pode cancelar um agendamento a qualquer momento, e não há cobrança de taxas ou multas pelo cancelamento. Essa flexibilidade foi pensada para garantir que você tenha liberdade caso ocorra algum imprevisto ou mudança de planos. No entanto, recomendamos que o cancelamento seja feito com antecedência, por consideração ao tempo do intérprete."
  },
  {
    "question": "Como faço para reagendar um atendimento?",
    "answer": "Caso seja necessário reagendar um atendimento, você deverá realizar uma nova solicitação para o intérprete, informando o novo dia e horário desejados. O sistema não altera automaticamente a data do agendamento anterior, pois o intérprete precisa confirmar a nova solicitação conforme sua agenda. Assim, o reagendamento funciona como um novo pedido."
  },
  {
    "question": "Como funciona o pagamento pelos serviços?",
    "answer": "O aplicativo não gerencia pagamentos entre clientes e intérpretes. Isso significa que os valores, formas de pagamento e prazos devem ser combinados diretamente entre as partes envolvidas. O sistema tem como foco facilitar o contato e o agendamento, não atuando como intermediador financeiro ou responsável por transações."
  },
  {
    "question": "Existe chat dentro da plataforma para falar com o intérprete?",
    "answer": "Não há chat interno no aplicativo para comunicação direta. Após o agendamento ser confirmado, o sistema disponibiliza o e-mail e/ou WhatsApp do intérprete e do cliente, permitindo que ambos conversem e alinhem os detalhes do atendimento. Essa medida busca manter a comunicação simples e direta, sem a necessidade de uma ferramenta de mensagens dentro da plataforma."
  },
  {
    "question": "Sou intérprete. Como recebo pelos atendimentos realizados?",
    "answer": "O Point Tils não realiza o gerenciamento de pagamentos e não faz repasses financeiros. Sendo assim, o recebimento pelos atendimentos deve ser acordado diretamente entre você e o cliente. Vocês podem definir juntos a forma de pagamento, como transferência bancária, Pix ou outra opção, bem como o valor e o prazo de pagamento."
  },
  {
    "question": "Sou intérprete. Posso recusar um agendamento?",
    "answer": "Sim, o intérprete tem liberdade para aceitar ou recusar qualquer solicitação de agendamento. Ao receber um pedido, você pode avaliá-lo conforme sua disponibilidade de horário, tipo de atendimento ou preferência pessoal. Caso opte por recusar, o cliente será informado e poderá buscar outro profissional disponível."
  }
]'
);
