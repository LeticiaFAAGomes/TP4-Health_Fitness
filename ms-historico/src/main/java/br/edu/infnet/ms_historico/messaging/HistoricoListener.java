package br.edu.infnet.ms_historico.messaging;

import br.edu.infnet.ms_historico.services.HistoricoService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HistoricoListener {

    @Autowired
    private HistoricoService service;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_REGISTRAR_HISTORICO)
    public void receber(RegistrarHistoricoCommand comando) {
        System.out.println("Mensagem recebida pelo ms-historico");
        System.out.println("Alunos: " + comando.getAlunoId());
        System.out.println("Descrição: " + comando.getDescricao());

        try {
            service.registrar(
                    comando.getAlunoId(),
                    comando.getDescricao()
            );

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ROUTING_KEY_CONFIRMADO,
                    new ResultadoHistorico(
                            comando.getAlunoId(),
                            true,
                            null
                    )
            );
        } catch (IllegalArgumentException e) {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ROUTING_KEY_RECUSADO,
                    new ResultadoHistorico(
                            comando.getAlunoId(),
                            false,
                            e.getMessage()
                    )
            );
        }
    }
}
