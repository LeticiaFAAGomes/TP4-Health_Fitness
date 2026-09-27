package br.edu.infnet.ms_gestao_alunos.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "alunos.exchange";

    public static final String ROUTING_KEY_REGISTRAR = "historico.registrar";
    public static final String ROUTING_KEY_CONFIRMADO = "historico.confirmado";
    public static final String ROUTING_KEY_RECUSADO = "historico.recusado";

    public static final String QUEUE_RESULTADO_HISTORICO = "alunos.resultado-historico.queue";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue queueResultadoHistorico() {
        return new Queue(QUEUE_RESULTADO_HISTORICO);
    }

    @Bean
    public Binding queueResultadoConfirmado() {
        return BindingBuilder
                .bind(queueResultadoHistorico())
                .to(exchange())
                .with(ROUTING_KEY_CONFIRMADO);
    }

    @Bean
    public Binding bindingResultadoRecusado() {
        return BindingBuilder
                .bind(queueResultadoHistorico())
                .to(exchange())
                .with(ROUTING_KEY_RECUSADO);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

}
