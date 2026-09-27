package br.edu.infnet.ms_gestao_alunos.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HistoricoPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publicarHistorico(RegistrarHistoricoCommand comando) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_REGISTRAR,
                comando
        );
    }
}
