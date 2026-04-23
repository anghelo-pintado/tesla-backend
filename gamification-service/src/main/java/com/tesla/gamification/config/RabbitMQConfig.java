package com.tesla.gamification.config;

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

    public static final String QUEUE_EVALUACIONES = "gamification.evaluacion.queue";
    public static final String EXCHANGE_COURSE = "course.exchange";
    public static final String ROUTING_KEY_EVALUACION = "evaluacion.completada";

    @Bean
    public Queue queueEvaluaciones() {
        return new Queue(QUEUE_EVALUACIONES, true);
    }

    @Bean
    public TopicExchange courseExchange() {
        return new TopicExchange(EXCHANGE_COURSE);
    }

    @Bean
    public Binding bindingEvaluaciones(Queue queueEvaluaciones, TopicExchange courseExchange) {
        return BindingBuilder.bind(queueEvaluaciones).to(courseExchange).with(ROUTING_KEY_EVALUACION);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}