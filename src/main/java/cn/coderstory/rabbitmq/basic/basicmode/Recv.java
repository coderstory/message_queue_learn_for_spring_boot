package cn.coderstory.rabbitmq.basic.basicmode;

import cn.coderstory.rabbitmq.basic.ConnectionUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component("2")
public class Recv {
    private final ConnectionUtil connectionUtil;
    private final static String QUEUE_NAME = "a_queue_name";

    public Recv(ConnectionUtil connectionUtil) {
        this.connectionUtil = connectionUtil;
    }

    public void recv() throws IOException {
        // 创建一个通道用于与服务器连接
        Channel channel = connectionUtil.getInstance().createChannel();
        // 创建和绑定一个队列 如果队列已存在 则必须配置一致 否则会报错
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        // 定义一个消费者
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                log.warn("consumerTag {}", consumerTag);
                log.warn("envelope {}", envelope);
                log.warn("body {}", new String(body));
                // 手动确认  deliveryTag  multiple
                // channel.basicAck(envelope.getDeliveryTag(),false);
            }
        };
        // 绑定队列和消费者 并开启自动确认 哪怕消费失败 也会消费掉消息
        channel.basicConsume(QUEUE_NAME, true, defaultConsumer);
    }
}
