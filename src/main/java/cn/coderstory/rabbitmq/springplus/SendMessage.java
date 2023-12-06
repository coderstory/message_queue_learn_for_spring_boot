package cn.coderstory.rabbitmq.springplus;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class SendMessage {
    private final RabbitTemplate rabbitTemplate;

    public SendMessage(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        sendDirectMessage();
    }

    public void sendDirectMessage() {
        rabbitTemplate.convertAndSend("TestDirectExchange", "TestDirectRouting", "xxxxxx");
    }
}
