package cn.coderstory.rabbitmq.basic.directmode;

import cn.coderstory.rabbitmq.basic.ConnectionUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component("14")
public class Recv2 {
    private final ConnectionUtil connectionUtil;
    private final static String EXCHANGE_NAME = "DIRECT_EXCHANGE";
    private final static String QUEUE_NAME = "direct_queue_2";

    public Recv2(ConnectionUtil connectionUtil) {
        this.connectionUtil = connectionUtil;
    }

    public void recv() throws IOException {
        // channel 无法使用try自动释放 否则后续监听失效
        Channel channel = connectionUtil.getInstance().createChannel();
        // 声明队列
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        // 绑定队列到交换机
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "r_k_2");


        DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                log.warn("Recv2 body {}", new String(body));

                // 手动确认  deliveryTag  multiple
                // channel.basicAck(envelope.getDeliveryTag(),false);
            }
        };
        // 自动确认模式 哪怕消费异常
        channel.basicConsume(QUEUE_NAME, true, defaultConsumer);
    }
}
