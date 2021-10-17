package com.example.spring.consumer.amqp.implementation;

import com.example.spring.consumer.amqp.AmqpRePublish;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class RePublishRabbitMQ implements AmqpRePublish {

    private static final String X_RETRIES_HEADER = "x-retries";

    @Autowired
    private RabbitTemplate rabbitTemplate; // vai ler a deadLetter

    @Value("${spring.rabbitmq.request.exchange.producer}")
    private String exchange;

    @Value("${spring.rabbitmq.request.routing-key.producer}")
    private String queue;

    @Value("${spring.rabbitmq.request.dead-letter.producer}")
    private String deadLetter;

    @Value("${spring.rabbitmq.request.parking-lot.producer}")
    private String parkingLot;

    @Override
    @Scheduled(cron = "${spring.rabbitmq.listener.time-retry}") // vai ser acionado a partir do horario
    public void rePublish() {
        List<Message> messages = getQueueMessages();

        messages.forEach(message -> {
            Map<String, Object> headers = message.getMessageProperties().getHeaders(); // pegando os headers da mensagens e coloquei em um map
            Integer retriesHeader = (Integer) headers.get(X_RETRIES_HEADER); // vamos ter a quantidade total de tentativas dessa mensagem

            if(retriesHeader == null) {
                retriesHeader = 0;
            }

            if(retriesHeader < 3) {
                headers.put(X_RETRIES_HEADER, retriesHeader + 1); // adiciona a quantidde de vezes que ja executou mais uma vez porque é a quantidade de vez que está executando no momento
                rabbitTemplate.send(exchange, queue, message); // envia a mensagem para a exchange, queue
            } else {
                rabbitTemplate.send(parkingLot, message); // envia a mensagem para a parkingLot para analisar depois, nao quero mais processar essa mensagem
            }
        });
    }

    // esse método é responsavel por entrar na fila de deadLetter(queue), vai ler item por item e fazer uma lista com todas as mensagens que existem dentra da deadLetter e retorna todas as mensagens prontas dentro de uma lista
    private List<Message> getQueueMessages() {
        List<Message> messages = new ArrayList<>();
        Boolean isNull;
        Message message;

        do {
            message = rabbitTemplate.receive(deadLetter);
            isNull  = message != null;

            if(Boolean.TRUE.equals(isNull)) {
                messages.add(message);
            }
        } while (Boolean.TRUE.equals(isNull));

        return messages;
    }
}
