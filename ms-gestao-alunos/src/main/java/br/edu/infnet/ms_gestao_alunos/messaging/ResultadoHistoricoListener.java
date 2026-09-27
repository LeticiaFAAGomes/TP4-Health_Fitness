package br.edu.infnet.ms_gestao_alunos.messaging;

import lombok.NoArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class ResultadoHistoricoListener {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_RESULTADO_HISTORICO)
    public void receber(ResultadoHistorico resultado) {
        System.out.println("Resultado do histórico recebido");
        System.out.println("Alunos: " + resultado.getAlunoId());

        if(resultado.isConfirmado()) {
            System.out.println("Histórico Confirmado");
        } else {
            System.out.println("Histórico recusado");
            System.out.println("Motivo: " + resultado.getMotivo());
        }
    }
}
