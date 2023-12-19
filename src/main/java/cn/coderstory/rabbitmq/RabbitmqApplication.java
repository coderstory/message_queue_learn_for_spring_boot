package cn.coderstory.rabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

@EnableKafka
@SpringBootApplication
public class RabbitmqApplication {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public RabbitmqApplication(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        produceMessage("你好啊");
    }

    // 创建消息生产者
    public void produceMessage(String message) {
        kafkaTemplate.send("myTopic", message);
    }

    // 消费消息
    @KafkaListener(topics = "myTopic", groupId = "your-group-id")
    public void consumeMessage(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
        System.out.println("Received message: " + message + " from partition: " + partition);
    }


    public static void main(String[] args) {
        SpringApplication.run(RabbitmqApplication.class, args);
    }

}
