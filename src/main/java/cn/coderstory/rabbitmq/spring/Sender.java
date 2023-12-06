package cn.coderstory.rabbitmq.spring;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

@Component
public class Sender {
    public Sender(AmqpTemplate amqpTemplate, Listener listener) {
        String msg = "hello, Spring boot amqp";
        amqpTemplate.convertAndSend("spring.test.exchange", "a.b", msg);
    }
}
