package cn.coderstory.rabbitmq.basic.workmode;

import cn.coderstory.rabbitmq.basic.ConnectionUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component("6")
public class Recv2 {
    private final ConnectionUtil connectionUtil;
    private final static String QUEUE_NAME = "test_work_queue";

    public Recv2(ConnectionUtil connectionUtil) {
        this.connectionUtil = connectionUtil;
    }

    public void recv() throws IOException {
        // channel 无法使用try自动释放 否则后续监听失效
        Channel channel = connectionUtil.getInstance().createChannel();
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        // QSQ 表示并发量
        channel.basicQos(1);
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                log.warn("Recv2 body {}", new String(body));
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // 手动确认  deliveryTag  multiple
                // channel.basicAck(envelope.getDeliveryTag(),false);
            }
        };
        // 自动确认模式 哪怕消费异常
        channel.basicConsume(QUEUE_NAME, true, defaultConsumer);
    }
}
