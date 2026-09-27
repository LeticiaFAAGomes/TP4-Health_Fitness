package br.edu.infnet.ms_historico.messaging;

import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "alunos.exchange";

    public static final String ROUTING_KEY_REGISTRAR = "historico.registrar";
    public static final String ROUTING_KEY_CONFIRMADO = "historico.confirmado";
    public static final String ROUTING_KEY_RECUSADO= "historico.recusado";

    public static final String QUEUE_REGISTRAR_HISTORICO = "historico.registrar.queue";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue queueRegistrarHistorico() {
        return new Queue(QUEUE_REGISTRAR_HISTORICO);
    }

    @Bean
    public Binding bindingRegistrarHistorico() {
        return BindingBuilder
                .bind(queueRegistrarHistorico())
                .to(exchange())
                .with(ROUTING_KEY_REGISTRAR);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

}
